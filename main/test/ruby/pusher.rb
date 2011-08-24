require "rubygems"
require "pusher-client"

PusherClient.logger = Logger.new(STDOUT)
options = {:secret => '529c5fd293355b21387b'} 
socket = PusherClient::Socket.new('c6c59a2e80e51c248a47')

# Subscribe to two channels
socket.subscribe('channel1')
# socket.subscribe('channel2')

# Subscribe to presence channel
# socket.subscribe('presence-channel3', 155)

# Bind to a global event (can occur on either channel1 or channel2)
socket.bind('globalevent') do |data|
  puts data
end

# Bind to a channel event (can only occur on channel1)
socket['channel1'].bind('channelevent') do |data|
  puts data
end

socket.connect