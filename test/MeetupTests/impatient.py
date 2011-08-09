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

        
class ImpatientTester(Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    
    def __init__ (self, id, thread_queue, num_iters):
        Thread.__init__(self)
        self.user_id = id
        self.API = ChatAPI(self.user_id)
        self.num_iters = num_iters
        self.thread_queue = thread_queue
        self.API.login()
        self.API.timeout = .5
        self.unique = 0
        
    def run(self):
        """Main method, log in and perform requests"""

        print self.name, " LIVES!"
        for i in range(0, self.num_iters):
            self.API.heartbeat()
            self.API.getListenResponse()
            time.sleep(.25)
            self.speak("iter = %d" % i)
        
        self.API.signout()
        myID = self.thread_queue.get(True, 1)
        self.thread_queue.task_done()
                                
    def report (self):
        self.speak("done")
        
    def speak(self, txt):
        print self.name, txt
    
    
ROOT_ID = 1040
NUM_USERS = 8
NUM_ITERS = 1000

thread_queue = Queue.Queue()
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    thread_queue.put(i)
    
testers = []
for i in range(ROOT_ID, ROOT_ID + NUM_USERS):
    time.sleep(.2)
    tester = ImpatientTester(i, thread_queue, NUM_ITERS)
    tester.daemon = True
    testers.append(tester)
    tester.start()      

thread_queue.join()  # block while threads are still working 
print "done", "*" * 40   
