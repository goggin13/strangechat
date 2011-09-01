require 'rubygems'
require 'net/http'
require 'uri'
require 'json'
require "pusher-client"

NUM_TESTERS = 8
# ROOT_URL = "http://10.0.1.50:9000/"
ROOT_URL = "http://173.246.101.127/"
ROOT_ID = 1001
CHANNEL = "#{ROOT_ID}-channel"
NUM_ITERS = 10000

class PhoneBook
  @@people = {}
  
  def put (hero_id, user_id, session_id)
    @@people[hero_id] = {
      :user_id => user_id,
      :session => session_id
    }
  end
  
  def get (hero_id) 
    @@people[hero_id]
  end
  
end

class ChatAPI 
  attr_accessor :user_id
  @@phone_book = PhoneBook.new
  
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
    myData["superPowers"].each do |power| 
      name = power["power"]
      if (name == "ICE_BREAKER")
        @iceBreaker = power['id']
      end
    end
    @@phone_book.put(@hero_id, @user_id, @session)
  end
    
  def use_icebreaker (channel, other_hero_id) 
    other_hero = @@phone_book.get(other_hero_id)
    if other_hero.nil?
      return false
    end
    data = {
      :channel => channel, 
      :power_id => @iceBreaker,
      :user_id => @user_id,
      :session => @session,
      :for_user => other_hero[:user_id],
      :for_session => other_hero[:session]
    }
    result = send "usepower", data
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
    @powers = {}
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
    @socket.bind('pusher:connection_established') do |data|
      @connected = true
    end 
  end    
    
  def subscribe_to (channel) 
    @socket.subscribe(channel)
    @socket.channels[channel].bind('roommessage') do |data|
      mid = JSON.parse(data)['text']
      markReceived mid
    end
    @socket.channels[channel].bind('usedpower') do |data|
      from_id = JSON.parse(data)['from']
      markPowerReceived from_id
    end    
  end
  
  def incAndGet 
    @myID = @myID + 1
  end
  
  def markSent (id) 
    @messages[id] = [Time.now, nil]
  end

  def markPowerSent (from_id) 
    # puts "sent from #{from_id}"
    counts = @powers[from_id]
    if counts == nil
      counts = [0, 0]  # sent, received
    end
    counts[0] += 1
    @powers[from_id] = counts
  end
  
  def markPowerReceived (from_id) 
    # puts "got from #{from_id}"    
    counts = @powers[from_id]   
    counts[1] += 1
    @powers[from_id] = counts
  end
  
  def markReceived (id) 
    timers = @messages[id]
    if timers == nil
      timers = [nil, nil]
    end
    timers[1] = Time.now
    @messages[id] = timers
    @counter += 1
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
    
    totalSent = 0
    totalReceived = 0
    @powers.each_pair do |from_id, counts|
      totalSent += counts[0]
      totalReceived += counts[1]
    end
    puts "received #{totalReceived} / #{totalSent} icebreakers"
  end
  
end

class Tester
  @@message_timer = MessageTimer.new
  
  def initialize(i, login=true)
     @myID = i
     @otherUser = (i % 2 == 1 ? @myID + 1 : @myID-1)
     @my_channel = "#{CHANNEL}-"
     @my_channel += (i % 2 == 1 ? @myID.to_s : (@myID-1).to_s) 
     if (i % 2 == 1)
       @@message_timer.subscribe_to(@my_channel)
     end     
     puts "user-#{@myID} on channel #{@my_channel}"
     if login
       @api = ChatAPI.new(i, "user-#{i}", "http://gandt.blogs.brynmawr.edu/files/2009/01/robot.jpg")
     end
  end
   
  def run 
    while @@message_timer.myID < NUM_ITERS
      sendMessage(@@message_timer.incAndGet())
      sleep(0.5)
    end
    puts "sleeping..."
    sleep(3)
  end  
  
  def sendMessage (mid) 
    message = '{"type": "roommessage", "from":' + @api.user_id.to_s + ', "text": ' + mid.to_s + '}'
    @@message_timer.markSent(mid)
    @api.push(@my_channel, "roommessage", message)
    if (mid > 20 && mid % 3 == 0) 
      @@message_timer.markPowerSent(@api.user_id)
      @api.use_icebreaker @my_channel, @otherUser
    end
    if (mid % 1000 == 0)
      report
    end
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
Tester.new(99, false).report
