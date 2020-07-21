#!/usr/bin/python
#encoding=utf8

import requests,re,datetime,time,commands
from elasticsearch import Elasticsearch
import sys
reload(sys)
sys.setdefaultencoding('utf8')


#### 全备份

def Find_indexs(url):
	res = commands.getstatusoutput("curl -s " + url + " --user elastic:hEcQSIyMwqQ3| awk '{print $3}'| grep -v -P '^\.'")
	backup_list = res[1].split('\n')
	return (backup_list)

def Start_bak(data_snapshot,backup_list=None):
	es = Elasticsearch(["http://10.10.94.41"],http_auth=('elastic', 'hEcQSIyMwqQ3'),Transport=9200,timeout=30000)
	for i in backup_list:
		print("开始索引--------%s------------" %i)
		params = {
		"type": "ufile",
		"settings": {
		"endpoint": "aha-log.ufile.cn-north-04.ucloud.cn",
		"public_key": "TOKEN_5445258c-3808-40dc-8389-d107b28a42c0", 
		"private_key": "2429aafd-c676-47f3-a632-474ed799744a", 
		"bucket": "aha-log",
		"compress": True,
		"chunk_size": "50mb",
		"base_path": "prodlog-bigdata-" + time.strftime("%Y-%m-%d") + "/" + i,
		"max_snapshot_bytes_per_sec": "40mb",
		"max_restore_bytes_per_sec": "40mb"
			}
		}
		ii = i.replace(".",'-')
		#es.snapshot.create_repository(repository=data_snapshot, body=params)
		#break             这一行和上面一行打开 就是创建仓库
		#res = requests.put(put_url, json=params)
		#print(res.content)
		##########  开始备份索引
		#put_index_name = put_url + "/"+ i + "?wait_for_completion"
		params = {
		"indices": i
		}
		es.snapshot.create(repository=data_snapshot, snapshot=time.strftime("%Y-%m-%d")+"_"+ii, body=params,wait_for_completion=True)
		#print(put_index_name)
		#res = requests.put(put_index_name, json=params)
		#print(res.content)



if __name__ == '__main__':
	Args = Find_indexs("http://10.10.94.41:9200/_cat/indices")
	Args_backup_list = Args
	print(Args_backup_list)
	Start_bak("bigdata_snapshot",Args_backup_list)
