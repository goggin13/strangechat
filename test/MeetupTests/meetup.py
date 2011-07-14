from urllib import urlencode
import httplib2
import json
from datetime import datetime
from threading import Thread
import Queue
import time

CHAT_ROOT = 'http://localhost:9000/'
HEARTBEAT_ROOT = 'http://localhost:9000/'
# CHAT_ROOT = 'http://173.246.102.246/'
# HEARTBEAT_ROOT = 'http://173.246.100.128/'
NUM_USERS = 15
NUM_ITERS = 10

ROOMS = dict()

class Tester(Thread):
    TOTAL_ROOMS = 7
    ITERATIONS = 15
    MSG_PER = 3

    def __init__ (self, id, thread_queue):
        Thread.__init__(self)
        self.id = id
        self.name = "user-%d" % self.id
        self.thread_queue = thread_queue
        self.lastReceived = 0
        self.room_ids = []
        
    def login(self):
        data = self.request("signin", {
            'facebook_id': self.id,
            'name': self.name,
            'access_token': "",
            'avatar': "",
            'alias': self.name,
            'updatefriends': False
        });
        self.myData = data[str(self.id)]
        self.heartbeatServer = data[str(self.id)]["heartbeatServer"]["uri"]

    def run(self):
        print self.name, " LIVES!"
        self.start = datetime.now()
        self.end = datetime.now()
        self.login()
        for i in range(0, NUM_ITERS):
            self.requestAndListen()
        self.report()
        myID = self.thread_queue.get(True, 1)
        self.thread_queue.task_done()
    
    def requestAndListen(self):
        self.requestRoom()
        data = self.listen()
        # data.reverse()
        if not data:
            return
        for d in data:
            if (d["data"]["type"]=="join"):
                room_id = d["data"]["room_id"]
                # self.speak("joined room %d with %s" % (room_id, d["data"]["new_user"]))
                self.room_ids.append(room_id)
                self.addToRoomList(self.id, room_id)
                self.addToRoomList(d["data"]["new_user"], room_id)                
                # break
        
    def addToRoomList(self, name, room_id):
        if room_id in ROOMS:
            ppl = ROOMS[room_id]
        else:
            ppl = list()
        if not name in ppl:
            ppl.append(name)
        ROOMS[room_id] = ppl

    def report(self):
        self.speak("I joined %d rooms" % len(self.room_ids))
    
    def speak(self, txt):
        print self.name, txt
        
    def listen (self):
        # self.speak("listening for %d " % self.lastReceived)
        data = self.request("listen", {
            'user_id': self.id,
            'lastReceived': self.lastReceived
        }, False)
        if not data:
            return None
        for d in data:
            self.lastReceived = data[0]["id"]
        return data

    def requestRoom (self):
        self.request("requestrandomroom", {
            'user_id': self.id,
            'lastReceived': self.lastReceived
        })

    def request (self, path, data, base_server=True):
        h = httplib2.Http(timeout=5)  
        h.force_exception_to_status_code = True         
        url = CHAT_ROOT if base_server else self.heartbeatServer
        url += path + "?" + urlencode(data)
        resp, content = h.request(url, "GET")
        if (resp['status'] != "200"):
            print url
            print resp
            return None
        return json.loads(content)
      
h = httplib2.Http()
resp, content = h.request(HEARTBEAT_ROOT + "mock/resetEventQueue", "GET")
if (resp['status'] != "200"):
    print "failed to reset event queue"
    
thread_queue = Queue.Queue()
for i in range(0, NUM_USERS):
    thread_queue.put(i)


time.sleep(1)
start = datetime.now()
for i in range(0, NUM_USERS):
    tester = Tester(i, thread_queue)
    tester.daemon = True
    tester.start()
    
thread_queue.join()  # block while threads are still working
end = datetime.now()
diff = end - start
time = diff.seconds
print "*" * 50

bad_rooms = list()
users = dict()
for room, ppl in ROOMS.items():
    print room, ppl
    if len(ppl) != 2:
        bad_rooms.append(room)
    for p in ppl:
        if p in users:
            users[p] = users[p] + 1
        else:
            users[p] = 1
            
bad_users = list()
for user, count in users.items():
    if count > NUM_ITERS or count < (NUM_ITERS - 1):
        bad_users.append(user)

print "test completed in %d seconds" % time
print "%d bad rooms and %d bad users" % (len(bad_rooms), len(bad_users))
print bad_rooms
print bad_users