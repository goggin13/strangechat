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

NUM_MESSAGES = 10
        
class MessageSender (Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """
    
    def __init__ (self, id, listener_id, thread_queue):
        Thread.__init__(self)
        self.user_id = id
        self.API = ChatAPI(
            134, 
            "Brother Death Engineer",
            "http://superheroclubhouse.com/web/assets/images/users/9b3c74b6b8085acbb764b79bf5bda8db.png"
        )
        self.thread_queue = thread_queue
        self.listener_id = listener_id
        self.API.login()
        self.sent_messages = {}       
        self.unique = 0
        
    def run(self):
        """Main method, log in and perform requests"""
        self.API.requestAndListen()
        room_id = self.getRoom()        
        if self.API.other_user_id != self.listener_id:
            self.speak("oops! Thats not who I want to talk to")
            self.API.signout() 
            self.thread_queue.task_done()
            return        
            
        time.sleep(2)
        self.speak("START SENDING!")
        for i in range(0, NUM_MESSAGES):
            self.sent_messages[i] = datetime.now()
            self.speak("SENDING MESSAGE %d" % i)
            self.API.messageRoom(i, room_id)
            time.sleep(.5)

        self.API.signout()            
        self.thread_queue.task_done()
        
    def getRoom (self):
        room_ids = self.API.getRoomIds()
        if len(room_ids) > 0:
            return room_ids[0]
        return None
           
    def report (self):
        self.speak("done")
        
    def speak(self, txt):
        print self.name, txt
    
class MessageListener (Thread):
    """ 
    An instance of ChatTester logs in to the chat server and
    performs requests for rooms, speaks, etc...
    """

    def __init__ (self, id, sender_id, thread_queue):
        Thread.__init__(self)
        self.user_id = id
        self.API = ChatAPI(
            135, 
            "Sister Death Engineer",
            "http://superheroclubhouse.com/web/assets/images/users/e10a7729e97ddd7d1b6f406adf9745fa.png"
        )
        self.thread_queue = thread_queue
        self.sender_id = sender_id
        self.API.login()
        self.got_messages = {}       

    def run(self):
        """Main method, log in and perform requests"""
        self.API.requestAndListen()
        if self.API.other_user_id != self.sender_id:
            self.speak("oops! Thats not who I want to talk to")
            self.API.signout() 
            self.thread_queue.task_done()
            return     
        
        self.speak("LISTENING....")
        while (len(self.got_messages)) < NUM_MESSAGES:
            messages = self.API.getMessageData()
            for m in messages:
                self.speak("GOT MESSAGE %d" % int(m["text"]))
                self.got_messages[int(m["text"])] = datetime.now() 
            time.sleep(.5)
        
        self.API.signout()          
        self.thread_queue.task_done()
        
    def getRoom (self):
        room_ids = self.API.getRoomIds()
        if len(room_ids) > 0:
            return room_ids[0]
        return None

    def speak(self, txt):
        print self.name, txt
        
    def report(self, sent_messages):
        total = 0
        for i in range(0, NUM_MESSAGES):
            diff = self.got_messages[i] - sent_messages[i]
            diffSecs = float(diff.microseconds) / 1000000
            total += diffSecs
            print i, diffSecs
        print "%d messages, average = %f" % (NUM_MESSAGES, total/i)
        

# check to make sure we dont pair with a real user
def waitingRoomIsEmpty ():
    h = httplib2.Http(timeout=3)  
    url = ChatAPI.CHAT_ROOT + "users/waiting_room_is_empty"
    resp, content = h.request(url, "GET")
    content = json.loads(content) 
    return content["status"] == "okay"

tries = 0
max_tries = 10
empty = waitingRoomIsEmpty()    
while not empty and tries < max_tries:
    print "waiting room is full, try again..."
    empty = waitingRoomIsEmpty()    
    tries += 1
    time.sleep(2)

if not empty:
    print "couldn't get empty waiting room"
    sys.exit(0)
    
thread_queue = Queue.Queue()
thread_queue.put(1)
thread_queue.put(2)
listener_id = 47
sender_id = 48

listener = MessageListener(listener_id, sender_id, thread_queue)
sender = MessageSender(sender_id, listener_id, thread_queue)
for t in [listener, sender]:
    t.daemon = True
    t.start()

thread_queue.join()  # block while threads are still working 
print "*" * 40
listener.report(sender.sent_messages)