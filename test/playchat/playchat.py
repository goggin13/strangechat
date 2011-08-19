from urllib import urlencode
import httplib2
import json
from datetime import datetime
from threading import Thread, Lock
import Queue
import time
import random
import math

class MessageTimer (): 
    messages = {}
    msgLock = Lock()
    idLock = Lock()
    messageID = 0
    
    def nextID (self):
        with self.idLock:
            self.messageID += 1
        if self.messageID % 100 == 0:
            self.report()
        return self.messageID
        
    def sentMessage (self, message_id):
        with self.msgLock:
            self.messages[message_id] = {
                "sent": datetime.now()
            }
            
    def gotMessage (self, message_id):
        with self.msgLock:
            if message_id in self.messages:
                msgData = self.messages[message_id]
                msgData["received"] = datetime.now()
                self.messages[message_id] = msgData
            
    def report (self):
        with self.msgLock:
            totalDiff = 0
            totalReceived = 0
            total = len(self.messages)

            tailLength = 100
            tailDiff = 0
            curTailCount = 0
            curCount = 0
            
            for msg, data in self.messages.iteritems():
                if "received" in data:
                    diff = data["received"] - data["sent"]
                    diffSecs = float(diff.microseconds) / 1000000
                    totalDiff += diffSecs
                    totalReceived += 1
                    curCount += 1
                    if total - curCount < tailLength:
                        tailDiff += diffSecs
                        curTailCount += 1
            totalCount = len(self.messages)
            print "received %d / %d, average = %f" % (totalReceived, totalCount, totalDiff/totalCount)
            print "\tlast %d messages, average = %f" % (curTailCount, tailDiff/curTailCount)
            
class ChatTester(Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    # CHAT_SERVER = "http://localhost:8090/"
    CHAT_SERVER = "http://173.246.101.127/"
    lastReceived = 0
    
    def __init__ (self, id, thread_queue, messageTimer):
        Thread.__init__(self)
        self.name = "user-%d" % id
        self.messageTimer = messageTimer


    def run(self):
        """Main method, log in and perform requests"""
        print self.name, " LIVES!"
        for i in range(0, 4000):
            messageID = self.messageTimer.nextID()
            self.messageTimer.sentMessage(messageID)
            self.say(messageID)
            self.listen()
                            
    def say (self, msg):
        h = httplib2.Http(timeout=3)  
        h.force_exception_to_status_code = True         
        url = self.CHAT_SERVER + "longpolling/room/say?user=%s&message=%s" % (self.name, msg)
        resp, content = h.request(url, "GET")
        if (resp['status'] != "200"):
            if (resp['status'] == "408"):
                print "timeout"
            else:    
                print url
                print resp
            return None        
    
    def listen (self):
        h = httplib2.Http(timeout=3)  
        h.force_exception_to_status_code = True         
        url = self.CHAT_SERVER + "longpolling/room/messages?lastReceived=%d" % self.lastReceived
        resp, content = h.request(url, "GET")
        if (resp['status'] != "200"):
            if (resp['status'] == "408"):
                print "timeout"
            else:    
                print url
                print resp
            return None
            
        for msg in json.loads(content):
            self.lastReceived = msg["id"]
            if msg["data"]["type"] == "message":
                messageID = msg["data"]["text"]
                if messageID.isdigit():
                    self.messageTimer.gotMessage(int(messageID))
            
ROOT_ID = random.randint(1, 10000)
NUM_USERS = 4
NUM_ITERS = 4000
messageTimer =  MessageTimer()
thread_queue = Queue.Queue()

for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    thread_queue.put(i)

testers = []
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    time.sleep(.2)
    tester = ChatTester(i, thread_queue, messageTimer)
    tester.daemon = True
    testers.append(tester)
    tester.start()      

thread_queue.join()  # block while threads are still working 
messageTimer.report()           