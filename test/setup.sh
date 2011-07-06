#!/bin/bash
# run this from to set up the slave server 
rm -rf /Users/goggin/Documents/CS/chatslave
cp -r /Users/goggin/Documents/CS/chatmaster /Users/goggin/Documents/CS/chatslave
sed 's/# http.port=9000/http.port=8080/' /Users/goggin/Documents/CS/chatslave/conf/application.conf > /Users/goggin/Documents/CS/chatslave/conf/temp.conf
mv /Users/goggin/Documents/CS/chatslave/conf/temp.conf /Users/goggin/Documents/CS/chatslave/conf/application.conf
