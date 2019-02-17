#!/usr/bin/python
#encoding=utf8
from flask import Flask, request, g
import os,socket,time
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
 
 
app = Flask(__name__)
app.config.update(DEBUG=True)
 
#获取本机电脑名
myname = socket.getfqdn(socket.gethostname(  ))
 
#获取本机ip
myaddr = socket.gethostbyname(myname)
myport = 8912
  
@app.route('/test1')
def the_test1():
    print "test1 print start"
    time.sleep(10)
    print "test1 print after sleep"
    return 'hello asyn'
 
@app.route('/test2')
def the_test2():
    print "test2 print!"
    return 'test2 return'
 
 
  
if __name__ == '__main__':
    app.run(host=myaddr,port=myport,debug=False,threaded=True)
