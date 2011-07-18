from urllib import urlencode
import httplib2
import json
from datetime import datetime
from threading import Thread
import Queue
import time
import random
import math

class ChatAPI():
    """Manages all interactions with the chat apis"""
    # CHAT_ROOT = 'http://localhost:9000/'
    CHAT_ROOT = 'http://173.246.102.246/'
    
    def __init__ (self, user_id, name):
        self.user_id = user_id
        self.name = name
        self.room_to_user = {}
        self.lastReceived = 0
        self.room_ids = []
        self.contacts = {}
        self.request_times = []
        self.room_count = 0
        self.msg_count = 0
        self.msg_received_count = 0
        
    def login(self):
        """Login into the chat master server"""
        data = self.request("signin", {
            'facebook_id': self.user_id,
            'name': self.name,
            'access_token': "",
            'avatar': "",
            'alias': self.name,
            'updatefriends': False
        }, self.CHAT_ROOT)
        self.myData = data[str(self.user_id)]
        self.heartbeatServer = data[str(self.user_id)]["heartbeatServer"]["uri"] 

    def requestAndListen(self):
        """
        Request a random room, and check for the listen response to get 
        matched up, and sets this classes room_id variable
        """
        startTime = None        
        if self.lastReceived == 0:
            startTime = round(time.time()) - 1
        self.requestRoom()
        time.sleep(.5)
        room_id = self.getListenResponse(startTime)
        if not room_id:
            self.speak("awwwww no room pour moi")
        else:
            self.room_count += 1
            
    def getListenResponse (self, startTime=None):
        data = self.listen()
        room_id = None
        if not data:
            return
        for d in data:
            ts = d['data']['timestamp'] / 1000
            if d["data"]["type"] == "join" and (not startTime or ts >= startTime):
                room_id = d["data"]["room_id"]
                self.speak("joined room %d with %s" % (room_id, d["data"]["new_user"]))
                self.room_ids.append(room_id)
                self.contacts[d["data"]["new_user"]] = {
                    "server": d["data"]["server"]
                }
                self.room_to_user[room_id] = d["data"]["new_user"]
                

            if d["data"]["type"] == "roommessage" and ts >= startTime:
                d = d["data"]
                # print d["room_id"], d["user_id"], d["from"]
                self.speak("chat from %s : %s" % (d["from"], d["text"]))
                self.msg_received_count += 1

        return room_id
            
    def getRoomIds (self):
        return self.room_ids
    
    def printStats (self):
        count = len(self.request_times)
        sumRequests = math.fsum(self.request_times)
        avg = float(sumRequests) / count
        self.speak("joined %d rooms, sent %d chats, received %d" 
                        % (self.room_count, self.msg_count, self.msg_received_count))
        self.speak("%f requests in %f, avg %f seconds" % (count, sumRequests, avg))
    
    def messageRoom (self, text, room_id):
        self.msg_count += 1
        self.sendMessage(self.room_to_user[room_id], text, room_id)
         
    def sendMessage(self, to, text, room_id):
        """Send a message to a user in a room"""
        # self.speak("msg from %d to %d" % (self.user_id, to))
        self.request("roommessage", {
            'from_user': self.user_id,
            'for_user': to,
            'msg': text,
            'room_id': room_id
        }, self.contacts[to]['server'])
    
    def listen (self):
        data = self.request("listen", {
            'user_id': self.user_id,
            'lastReceived': self.lastReceived
        }, self.heartbeatServer)
        if not data:
            return None
        for d in data:
            self.lastReceived = d["id"]
        return data

    def heartbeat (self):
        self.request("heartbeat", {
            'for_user': self.user_id,
            'room_ids': self.room_ids
        }, self.heartbeatServer)

    def requestRoom (self):
        self.speak("request room")
        self.request("requestrandomroom", {'user_id': self.user_id}, self.CHAT_ROOT)

    def request (self, path, data, server):
        h = httplib2.Http(timeout=4)  
        h.force_exception_to_status_code = True         
        url = server + path + "?" + urlencode(data)
        start = datetime.now()
        # self.speak("GET %s" % url)
        resp, content = h.request(url, "GET")
        if (resp['status'] != "200"):
            print url
            print resp
            return None
        end = datetime.now()
        diff = end - start
        if path != "listen":
            self.request_times.append(diff.seconds + (float(diff.microseconds) / 10000.0))
        return json.loads(content)           

    def speak(self, txt):
        # pass 
        print self.name, txt
        
class ChatTester(Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    
    def __init__ (self, id, thread_queue, num_iters, num_users):
        Thread.__init__(self)
        self.user_id = id
        self.name = "user-%d" % self.user_id
        self.API = ChatAPI(self.user_id, self.name)
        self.num_iters = num_iters
        self.num_users = num_users
        self.thread_queue = thread_queue
        self.API.login()
        self.unique = 0
        
    def run(self):
        """Main method, log in and perform requests"""

        print self.name, " LIVES!"
        for i in range(0, self.num_iters):

            self.API.requestAndListen()
            self.API.heartbeat()
            for r in range(0, 20):
                room_id = self.randomRoom()
                if room_id:
                    self.unique += 1
                    self.API.messageRoom("hello - %d" % self.unique, room_id)
                    time.sleep(.2)
            
            self.API.heartbeat()
            self.API.getListenResponse()
            self.API.heartbeat()
            
        time.sleep(1)
        self.API.heartbeat()
        self.API.getListenResponse()        
        self.API.heartbeat()
        time.sleep(1)
        self.API.heartbeat()
        self.API.getListenResponse()                
        self.API.heartbeat()
        
        self.msg_count = self.API.msg_count
        self.msg_received_count = self.API.msg_received_count
        myID = self.thread_queue.get(True, 1)
        self.thread_queue.task_done()
                                    
    def randomRoom (self):
        room_ids = self.API.getRoomIds()
        if len(room_ids) > 0:
            return room_ids[random.randint(0, len(room_ids) - 1)]
        return None
           
    def report (self):
        self.speak("done")
        self.API.printStats()
        
    def speak(self, txt):
        print self.name, txt
    
ROOT_ID = 1
NUM_USERS = 8
NUM_ITERS = 3

thread_queue = Queue.Queue()
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    thread_queue.put(i)

h = httplib2.Http()
HEARTBEAT_ROOT = 'http://173.246.100.128/'
# HEARTBEAT_ROOT = 'http://localhost:9000/'
resp, content = h.request(HEARTBEAT_ROOT + "mock/resetEventQueue", "GET")
if (resp['status'] != "200"):
    print "failed to reset event queue"

testers = []
start = datetime.now()
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    tester = ChatTester(i, thread_queue, NUM_ITERS, NUM_USERS)
    tester.daemon = True
    testers.append(tester)
    tester.start()      

thread_queue.join()  # block while threads are still working 
print "*" * 40   
msg_sent = 0
msg_got = 0
for t in testers:
    msg_sent += t.msg_count
    msg_got += t.msg_received_count
    t.report()
print "*" * 40   
print "%d total msgs sent, %d received" % (msg_sent, msg_got)
