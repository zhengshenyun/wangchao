#!/usr/bin/python
#encoding=utf8
from flask import Flask, request, g, render_template
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
  




@app.route('/job')
def the_test1():
    return render_template('job.html')
 
 
 
  
if __name__ == '__main__':
    app.run(host=myaddr,port=myport,debug=False,threaded=True)
