#!/usr/bin/python
#encoding=utf-8

import sys
reload(sys)
sys.setdefaultencoding("utf8")
import jenkins

server = jenkins.Jenkins("http://106.75.96.51:8080", username="wangchao", password="wangchao123")
user = server.get_whoami()
version = server.get_version()
Arg1,Arg2,Arg3,Arg4 = sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4]
#print(Arg1,Arg2,Arg3,Arg4)
CI_CD = Arg1
Work = "多个项目并发部署"
Env = Arg2
Project_name = Arg3
Branch_name = Arg4
server.build_job("crontab-job",{"CI_CD":CI_CD,"Work":Work,"Env":Env,"Project_name":Project_name,"Branch_name":Branch_name})
