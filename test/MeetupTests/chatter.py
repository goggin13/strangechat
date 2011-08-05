from urllib import urlencode
import httplib2
import json
from datetime import datetime
from threading import Thread
import Queue
import time
import random
import math
import os
import sys

ROOT = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, ROOT)
from chatapi import ChatAPI

        
class ChatTester(Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    
    def __init__ (self, id, thread_queue, num_iters, num_users):
        Thread.__init__(self)
        self.user_id = id
        self.API = ChatAPI(self.user_id)
        self.num_iters = num_iters
        self.num_users = num_users
        self.thread_queue = thread_queue
        self.API.login()
        self.unique = 0
        
    def run(self):
        """Main method, log in and perform requests"""

        print self.name, " LIVES!"
        self.API.requestAndListen()
        for i in range(0, self.num_iters):
            self.API.heartbeat()
            for r in range(0, 2):
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
        
        self.API.signout()
        
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
    
ROOT_ID = 999
NUM_USERS = 16
NUM_ITERS = 4000

thread_queue = Queue.Queue()
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    thread_queue.put(i)
    
testers = []
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    time.sleep(.2)
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
