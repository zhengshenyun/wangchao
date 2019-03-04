#!/usr/bin/python
#encoding=utf8

__author__ = 'Suoerking'

import socket,sys,json
from configobj import ConfigObj
import sys,logging,time
sys.path.append("../lib/logmodule")
sys.path.append("../lib/cmdbcollcet")
from printlog import Printlog
from asset import Server_agent

def agentconn():
	sock = socket.socket()
	try:
		sock.connect(ADDR)
		logging.info('have connected with server %s' %(HOST))
		while True:
			try:
				infornation = Server_agent()
				data = {infornation.Ip():[infornation.Ip(),infornation.Cpu(),infornation.Mem(),infornation.Release(),int(time.time())]}
				if len(data)>0:
					print('send:',data)
					sock.sendall(json.dumps(data))
					del infornation
					recv_data = sock.recv(BUFSIZE)
					print('receive:',recv_data)
					logging.info('程序心跳时间在执行')
					time.sleep(120)
			except Exception:
				logging.error('have connected without server %s' %(HOST))
	except Exception:
		print('error')
		logging.error('have connected without server %s' %(HOST))
		sock.close()
		sys.exit()

if __name__ =='__main__':
	conf_ini = "../conf/beatjobs.conf"
	config = ConfigObj(conf_ini,encoding='UTF8')
	BUFSIZE = int(config['Server']['buf_size'])
	HOST = config['Server']['server_ip']
	PORT = config['Server']['port']
	Level = config['Log']['loglevel']
	filepath = config['Log']['logpath']
	filename = config['Log']['logfilename']
	fileall = str(filepath) + str(filename)
	ADDR =(str(HOST),int(PORT))
	Printlog(Level,fileall)
	agentconn()
