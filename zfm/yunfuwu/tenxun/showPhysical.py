#!/usr/bin/env python
# encoding: utf-8

import sys
reload(sys)
sys.setdefaultencoding("utf8")


from  publicApiphysical import public
import json
Region = 'ap-beijing'
#endpoint = 'cvm.ap-beijing.tencentcloudapi.com'

data={
      'Action' : 'DescribeDevices',
      'Region' : Region,
      'Offset' : 0,
      'Limit': 100,

}
num = public(**data)
a = num.get('Response').get('DeviceInfoSet')
total = []
while True:
	num = public(**data)
	a = num.get('Response').get('DeviceInfoSet')
	if a:
		total.append(a)
		data['Offset'] += 100
	else:
		break
#print(len(total))
for i in total:
	for ii in i:
		print("腾讯云平台黑石服务器 %s---------------------ok" %(ii.get('LanIp')))

print("-----------------------------------------------------------------------------------------------腾讯云平台云主机总数量为 %s 台--------------------------------------------------------------------------------" %len(sum(total,[])))
