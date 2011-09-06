require 'rubygems'
require 'net/http'
require 'uri'
require 'json'
require "pusher-client"

# ruby thread.rb 1001 

ROOT_ID = Integer(ARGV[0])
NUM_TESTERS = ARGV.length < 2 ? 8 : Integer(ARGV[1])
USE_POWERS = ARGV.length < 3 || ARGV[2] == "usepowers"

# ROOT_URL = "http://10.0.1.50:9000/"
ROOT_URL = "http://173.246.101.127/"

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
  @@counter = 0
  
  def initialize(u, a, av)
    @hero_id = u
    @alias = a
    @avatar = av
    @my_channel = "#{CHANNEL}-"
    @my_channel += (@hero_id % 2 == 1 ? @hero_id.to_s : (@hero_id-1).to_s)
    signin
  end
    
  def signin
    data = {:sign_in_id => @hero_id, :avatar => @alias, :alias => @alias}
    resp = send("signin", data)
    sign_in_data = JSON.parse(resp)
    myData = sign_in_data[@hero_id.to_s]
    @user_id = myData["id"]
    subscribe_to_me()
    @session = myData["session_id"]
    myData["superPowers"].each do |power| 
      name = power["power"]
      if (name == "ICE_BREAKER")
        @iceBreaker = power['id']
      end
      if (name == "KARMA")
        @karma = power['id']
      end      
    end
    @@phone_book.put(@hero_id, @user_id, @session)
  end

  def subscribe_to_me
    PusherClient.logger = Dummy.new
    @socket = PusherClient::Socket.new('c6c59a2e80e51c248a47')
    @socket.connect(true) # Connect asynchronously
    @socket.bind('pusher:connection_established') do |data|
      @connected = true
    end    
    channel = @user_id.to_s + "_channel";
    @socket.subscribe(channel)
    @socket.channels[channel].bind('karma') do |json|
      data = JSON.parse(json)
      open_kube data['id']
    end
  end

  def open_kube (kube_id) 
    data = {
      :channel => @my_channel, 
      :kube_id => kube_id,
      :user_id => @user_id,
      :session => @session,
    }
    result = send "openkube", data
  end

  def use_karma(channel, other_hero_id) 
    # @@counter += 1
    # puts "karma - #{@@counter}"
    use_power channel, @karma, other_hero_id
  end
    
  def use_icebreaker (channel, other_hero_id) 
    use_power channel, @iceBreaker, other_hero_id
  end
  
  def use_power (channel, power_id, other_hero_id) 
    other_hero = @@phone_book.get(other_hero_id)
    if other_hero.nil?
      return false
    end
    data = {
      :channel => channel, 
      :power_id => power_id,
      :user_id => @user_id,
      :session => @session,
      :for_user => other_hero[:user_id],
      :for_session => other_hero[:session],
      "params[0]" => 1
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
    @start_time = Time.now
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
    if counts != nil
      counts[1] += 1
      @powers[from_id] = counts      
    end
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
    # puts "sent #{sent_count}, received #{received_count} of #{@messages.length}"
    puts "sent and received #{both_count}, avg #{avg}"
    puts "elapsed time = #{Time.now - @start_time}"
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
  
  def initialize(i, login=true, usePowers=true)
     @myID = i
     @usePowers = usePowers
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
      sleep(0.2)
    end
    puts "sleeping..."
    sleep(3)
  end  
  
  def sendMessage (mid) 
    message = '{"type": "roommessage", "from":' + @api.user_id.to_s + ', "text": ' + mid.to_s + '}'
    @@message_timer.markSent(mid)
    @api.push(@my_channel, "roommessage", message)
    if (@usePowers && mid > 20 && mid % 3 == 0) 
      @@message_timer.markPowerSent(@api.user_id)
      @api.use_icebreaker @my_channel, @otherUser
    end
    if (@usePowers && mid > 20 && mid % 5 == 0) 
      @@message_timer.markPowerSent(@api.user_id)
      @api.use_karma @my_channel, @otherUser
    end    
    if (mid % 100 == 0)
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
  tester = Tester.new(ROOT_ID + i, true, USE_POWERS)
  threads[i] = Thread.new { tester.run() }
end

for i in 0..NUM_TESTERS - 1 
  threads[i].join
end
finished = Time.now
duration = finished - start
puts "Completed in #{duration}"
Tester.new(99, false).report


