require 'rubygems'
require 'net/http'
require 'uri'
require 'json'
require "pusher-client"

NUM_TESTERS = 2
CHANNEL = "test-channel"
ROOT_URL = "http://10.0.1.50:9000/"
ROOT_ID = 1000
NUM_ITERS = 200

class ChatAPI 
  attr_accessor :user_id
  
  def initialize(u, a, av)
    @hero_id = u
    @alias = a
    @avatar = av
    signin
  end
    
  def signin
    data = {:sign_in_id => @hero_id, :avatar => @alias, :alias => @alias}
    resp = send("signin", data)
    sign_in_data = JSON.parse(resp)
    myData = sign_in_data[@hero_id.to_s]
    @user_id = myData["id"]
    @session = myData["session_id"]
  end
  
  def push (channel, event, message)
    data = {:channel => channel, :event => event, :message => message}
    send "push", data
  end
  
  def send (path, data)
    url = URI.parse(ROOT_URL + path)
    res = Net::HTTP.post_form(url, data)    
    res.body
  end
    
end

class Dummy
  def debug (msg)
  end
end

class MessageTimer
  attr_accessor :myID
  
  def initialize()
    @myID = 0
    @messages = {}
    @connected = false
    @counter = 0
    connect
    while !@connected
      puts "connecting..."
      sleep(0.3)
    end
    puts "connected!"    
  end
    
  def connect

    PusherClient.logger = Dummy.new
    @socket = PusherClient::Socket.new('c6c59a2e80e51c248a47')
    @socket.connect(true) # Connect asynchronously
    @socket.subscribe(CHANNEL)
    @socket.bind('pusher:connection_established') do |data|
      @connected = true
    end
    @socket.channels[CHANNEL].bind('roommessage') do |data|
      mid = JSON.parse(data)['text']
      markReceived mid
    end
  end    
    
  def incAndGet 
    @myID = @myID + 1
  end
  
  def markSent (id) 
    @messages[id] = [Time.now, nil]
    # puts "sent #{id} at #{@messages[id][0]}"    
  end
  
  def markReceived (id) 
    timers = @messages[id]
    if timers == nil
      timers = [nil, nil]
    end
    timers[1] = Time.now
    @messages[id] = timers
    @counter += 1
    if @counter % 100 == 0
      report
    end
    # puts "received #{id} at #{@messages[id][1]}, #{@messages[id][1] - @messages[id][0]}"    
  end
  
  def report 
    received_count = 0
    sent_count = 0
    both_count = 0
    total_time = 0.0
    @messages.each_pair do |mid, timers|
      received = timers[1] != nil
      sent = timers[0] != nil
      if sent && received
        both_count += 1
        total_time += timers[1] - timers[0]
      end
      if sent
        sent_count += 1
      end
      if received  
        received_count += 1
      end
    end
    avg = total_time / both_count
    puts "sent #{sent_count}, received #{received_count} of #{@messages.length}"
    puts "sent and received #{both_count}, avg #{avg}"
  end
  
end

class Tester
  @@message_timer = MessageTimer.new
  
  def initialize(i)
     @myID = i
     @api = ChatAPI.new(i, "user-#{i}", "http://gandt.blogs.brynmawr.edu/files/2009/01/robot.jpg")
  end
   
  def run 
    while @@message_timer.myID < NUM_ITERS
      sendMessage(@@message_timer.incAndGet())
      # sleep(0.01)
    end
  end  
  
  def sendMessage (mid) 
    message = '{"type": "roommessage", "from":' + @api.user_id.to_s + ', "text": ' + mid.to_s + '}'
    # puts message
    @@message_timer.markSent(mid)
    @api.push(CHANNEL, "roommessage", message)
  end
  
  def report
    @@message_timer.report
  end
end

start = Time.now
threads = Array.new
for i in 0..NUM_TESTERS - 1
  tester = Tester.new(ROOT_ID + i)
  threads[i] = Thread.new { tester.run() }
end

for i in 0..NUM_TESTERS - 1 
  threads[i].join
end
finished = Time.now
duration = finished - start
puts "Completed in #{duration}"
Tester.new(99).report
