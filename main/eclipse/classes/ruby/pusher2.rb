require 'rubygems'
require 'pusher-client'

# PusherClient.logger = Logger.new(STDOUT)
socket = PusherClient::Socket.new('c6c59a2e80e51c248a47')
socket.connect(true) # Connect asynchronously

# Subscribe to two channels
socket.subscribe('channel1')
socket.subscribe('channel2')

# Bind to a global event (can occur on either channel1 or channel2)
socket.bind('globalevent') do |data|
  puts data
end

# Bind to a channel event (can only occur on channel1)
socket['channel1'].bind('channelevent') do |data|
  puts data
end

loop do
  sleep(1) # Keep your main thread running
end