#!/usr/bin/env python
# encoding: utf-8

import sys
reload(sys)
sys.setdefaultencoding("utf8")


from  publicApi import public
import json
Region = 'ap-beijing'
#endpoint = 'cvm.ap-beijing.tencentcloudapi.com'

data={
      'Action' : 'DescribeInstances',
      'Region' : Region,
      'Offset' : 0,
      'Limit': 100,

}
num = public(**data)
a = num.get('Response').get('InstanceSet')
total = []
while True:
	num = public(**data)
	a = num.get('Response').get('InstanceSet')
	if a:
		total.append(a)
		data['Offset'] += 100
	else:
		break
#print(len(total))
for i in total:
	for ii in i:
		if ii['InstanceState'] == "SHUTDOWN":
			print("腾讯云平台云主机机器 %s---------处于关闭状态----------------------error" %(ii.get('PrivateIpAddresses')[0]))
		else:
			print("腾讯云平台云主机机器 %s---------处于运行状态" %(ii.get('PrivateIpAddresses')[0]))
		#print  ii.get('InstanceName'), ii.get('PrivateIpAddresses')[0]

print("-----------------------------------------------------------------------------------------------腾讯云平台云主机总数量为 %s 台--------------------------------------------------------------------------------" %len(sum(total,[])))
