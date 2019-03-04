#!/usr/bin/python
#encoding=utf8
#encoding=utf8
__author__ = "Superking"

from SocketServer import BaseRequestHandler,ThreadingTCPServer
import threading,logging,json
from configobj import ConfigObj
import sys,uuid
sys.path.append("../lib/logmodule")
from printlog import Printlog
sys.path.append("../lib/redismodule")
from connredis import conn_Redis
sys.path.append("../lib/mysqlmodule")
from conn_mysql import Conn_mysql

class Handler(BaseRequestHandler):
	def handle(self):
		address,pid = self.client_address
		logging.info('%s connected!'%address)
		while True:
			data = self.request.recv(BUF_SIZE)
			if len(data)>0:
				print('receive=',json.loads(data))
				jsondata = json.loads(data)
				logging.info(jsondata)
				mysqlselect = Conn_mysql(mysql_conf = "../conf/beatjobs.conf")
				selectres = mysqlselect.Mysqlconn("select server_ip from cmdb_cmdbmanage where server_ip=%s",[jsondata.keys()[0]])
				if selectres:
					pass
				else:
					mysqlselect.Mysqlconn("insert into cmdb_cmdbmanage(uid,server_ip,server_mem,server_cpu,server_system,server_service,server_position,`group`,env) \
						values(%s,%s,%s,%s,%s,%s,%s,%s,%s)",(str(uuid.uuid1()).split("-")[0],jsondata.values()[0][0],jsondata.values()[0][1],jsondata.values()[0][2],\
						jsondata.values()[0][3],"空","空","空","空"))
				Conn_redis = conn_Redis(conf_ini)
				Conn_redis.push_list(jsondata)
				cur_thread = threading.current_thread()
				self.request.sendall('response')
				print('send:','response')
			else:
				logging.error('%s without connected!'%address)
				print('close')
				break

if __name__ == '__main__':
	conf_ini = "../conf/beatjobs.conf"
	config = ConfigObj(conf_ini,encoding='UTF8')
	BUF_SIZE = int(config['Server']['buf_size'])
	HOST = config['Server']['server_ip']
	PORT = config['Server']['port']
	Level = config['Log']['loglevel']
	Printlog(Level,"../log/beatjobs-server.log")
	ADDR = (str(HOST),int(PORT))
	server = ThreadingTCPServer(ADDR,Handler)
	print('listening')
	server.serve_forever()

