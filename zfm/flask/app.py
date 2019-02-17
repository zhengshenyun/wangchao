#!/usr/bin/python
#encoding=utf8
from flask import Flask, request, g, render_template
import os,socket,time,logging
import sys,json,Mysql
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
 
@app.route('/all')
def the_test2():
    res = Mysql.Mysqlconnidatabase()
    return json.dumps(res)

@app.route('/rollres',methods=["POST"])
def the_test3():
    databases = request.form.get('datas')
    sql = request.form.get('sql')
    Mysql.printlog()
    logging.debug("查询库: %s  SQL语句 %s" %(databases,sql))
    res = Mysql.Mysqlconn(databases,sql)
    return json.dumps(res)


if __name__ == '__main__':
    app.run(host=myaddr,port=myport,debug=False,threaded=True)
