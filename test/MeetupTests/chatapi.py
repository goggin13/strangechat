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
    # CHAT_ROOT = 'http://localhost:9000/'   # Dev
    # CHAT_ROOT = 'http://localhost:8080/'   # Dev    
    # CHAT_ROOT = 'http://173.246.100.79/' # live
    CHAT_ROOT = 'http://173.246.101.45/' # staging
    
    def __init__ (self, user_id, name=None, avatar=None):
        self.user_id = user_id
        self.name = name if name else "N/A"
        self.room_to_user = {}
        self.other_user_id = -1
        self.avatar = avatar if avatar else "http://www.everafterstore.com/media/belt-buckle-robot-red-BR11845ZNKRD.jpg"
        self.lastReceived = 0
        self.room_ids = []
        self.contacts = {}
        self.request_times = []
        self.room_count = 0
        self.msg_count = 0
        self.logged_in_at = round(time.time())
        self.msg_received_count = 0
        
    def login(self):
        """Login into the chat master server"""
        data = self.request("signin", {
            'facebook_id': self.user_id,
            'name': self.name,
            'access_token': "",
            'avatar': self.avatar,
            'alias': self.name,
            'updatefriends': False
        }, self.CHAT_ROOT)
        # print data
        # print str(self.user_id)
        for k, v in data.iteritems():
            if v['name'] == self.name:
                self.myData = v
                self.user_id = v["id"]    
                self.name = "user-%d" % self.user_id        
                self.heartbeatServer = v["heartbeatServer"]["uri"] 
                self.speak("set my data, id = %s, server = %s " % (self.user_id, self.heartbeatServer))  
                
    def signout(self):
       data = self.request("signout", {
             'user_id': self.user_id,
         }, self.CHAT_ROOT)
         
    def requestAndListen(self):
        """
        Request a random room, and check for the listen response to get 
        matched up, and sets this classes room_id variable
        """
        startTime = None        
        if self.lastReceived == 0:
            startTime = round(time.time()) - 1
        self.requestRoom()
        time.sleep(1)
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
                self.other_user_id = d["data"]["new_user"]
                self.room_to_user[room_id] = d["data"]["new_user"]
                
            if d["data"]["type"] == "roommessage" and ts >= startTime:
                d = d["data"]
                # print d["room_id"], d["user_id"], d["from"]
                self.speak("chat from %s : %s" % (d["from"], d["text"]))
                self.msg_received_count += 1
                
            elif d["data"]["type"] == "newpower" and ts >= startTime:
                d = d["data"]
                power_id = d["power_id"]
                power_name = d["superPower"]["name"]
                self.speak("sweet, new power %s - %s" % (power_name, power_id))
                self.use_power(power_id)

        return room_id
         
    def getMessageData (self):
        data = self.listen()
        messages = []
        if not data:
            return messages
        for d in data:
            ts = d['data']['timestamp'] / 1000

            if d["data"]["type"] == "roommessage" and ts >= self.logged_in_at:
                messages.append(d["data"])
        return messages
        
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
            'room_ids': ",".join(map(str, self.room_ids))
        }, self.heartbeatServer)

    def use_power (self, power_id):
        self.request("usepower", {
            'user_id': self.user_id,
            'power_id': power_id,
            'other_id': self.other_user_id,
            'room_id': self.room_ids[0] if len(self.room_ids) > 0 else -1
        }, self.CHAT_ROOT)        

    def requestRoom (self):
        self.speak("request room")
        self.request("requestrandomroom", {'user_id': self.user_id}, self.CHAT_ROOT)
        
    def request (self, path, data, server):
        h = httplib2.Http(timeout=3)  
        h.force_exception_to_status_code = True         
        url = server + path + "?" + urlencode(data)
        start = datetime.now()
        resp, content = h.request(url, "GET")
        if (resp['status'] != "200"):
            if (resp['status'] == "408"):
                print "timeout"
            else:    
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
