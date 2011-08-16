from urllib import urlencode
import httplib2
import json
from datetime import datetime
from threading import Thread, Lock
import Queue
import time
import random
import math
import os
import sys

ROOT = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, ROOT)
from chatapi import ChatAPI

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
            for msg, data in self.messages.iteritems():
                if "received" in data:
                    diff = data["received"] - data["sent"]
                    diffSecs = float(diff.microseconds) / 1000000
                    totalDiff += diffSecs
                    totalReceived += 1
            totalCount = len(self.messages)
            print "received %d / %d, average = %f" % (totalReceived, totalCount, totalDiff/totalCount)
           
class ChatTester(Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    
    def __init__ (self, id, thread_queue, num_iters, num_users, messageTimer):
        Thread.__init__(self)
        self.user_id = id
        self.API = ChatAPI(self.user_id)
        self.num_iters = num_iters
        self.num_users = num_users
        self.thread_queue = thread_queue
        self.messageTimer = messageTimer
        self.API.login()
        
    def run(self):
        """Main method, log in and perform requests"""
        global messageTimer
        print self.name, " LIVES!"
        self.API.requestAndListen()
        for i in range(0, self.num_iters):
            self.API.heartbeat()
            room_id = self.randomRoom()
            if room_id:
                messageID = self.messageTimer.nextID()
                self.messageTimer.sentMessage(messageID)
                self.API.messageRoom(messageID, room_id)
            
            self.API.heartbeat()
            self.processListen()
            
        time.sleep(1)
        self.API.heartbeat()
        self.processListen()        
        
        self.API.signout()
        
        myID = self.thread_queue.get(True, 1)
        self.thread_queue.task_done()
    
    def processListen (self):
        messages = self.API.getMessageData()
        for m in messages:
            messageID = m["text"]
            if messageID.isdigit():
                self.messageTimer.gotMessage(int(messageID))
                                
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
    
ROOT_ID = random.randint(1, 10000000000)
NUM_USERS = 8
NUM_ITERS = 4000
messageTimer =  MessageTimer()
thread_queue = Queue.Queue()

for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    thread_queue.put(i)
    
testers = []
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    time.sleep(.2)
    tester = ChatTester(i, thread_queue, NUM_ITERS, NUM_USERS, messageTimer)
    tester.daemon = True
    testers.append(tester)
    tester.start()      

thread_queue.join()  # block while threads are still working 
messageTimer.report()