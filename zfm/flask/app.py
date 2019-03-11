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
  



class MyThread(threading.Thread):
    def __init__(self,ip):
        super(MyThread, self).__init__()
        self.ip=ip

    def run(self):
        time.sleep(4)      ############# 模拟耗时逻辑 下面输入当作验证
	print("ooooooooooooooooooooooookkkkkkkkkkkkkkkkkkkkkkkkk")


@app.route('/job')
def the_test1():
    for i in [0]:
        t =MyThread(i) ##################  此时用多线程 的确是先跳页面  后面逻辑还是在跑的
        t.start()
    #t.join()   ###########  这个一定要注释掉  这样才能实现 先跳转页面
    return render_template('job.html')





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
