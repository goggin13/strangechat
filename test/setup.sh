#!/bin/bash
# run this to set up the slave server instances
rm -rf /Users/goggin/Documents/CS/chatslave
cp -r /Users/goggin/Documents/CS/chatmaster /Users/goggin/Documents/CS/chatslave
sed 's/# http.port=9000/http.port=8080/' /Users/goggin/Documents/CS/chatslave/conf/application.conf > /Users/goggin/Documents/CS/chatslave/conf/temp.conf
sed 's/db=mysql:root:root@chat_master/db=mem/' /Users/goggin/Documents/CS/chatslave/conf/temp.conf > /Users/goggin/Documents/CS/chatslave/conf/application.conf
rm /Users/goggin/Documents/CS/chatslave/conf/temp.conf
mv /Users/goggin/Documents/CS/chatslave/conf/master.txt /Users/goggin/Documents/CS/chatslave/conf/chat.txt 
rm /Users/goggin/Documents/CS/chatmaster/conf/chat.txt
