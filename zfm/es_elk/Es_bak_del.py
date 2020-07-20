#!/usr/bin/python
#encoding=utf8

import requests,re,datetime,time
import sys
reload(sys)
sys.setdefaultencoding('utf8')

#备份当前的策略 一天前
bakuptime = 1

#删除策略 三天前
deletetime = 3

def Find_indexs(url):
	res = requests.get(url).text
	indexs_list = [i.split()[2] for i in res.split("\n") if i]
	#print(indexs_list)
	#找出匹配符合的索引
	can_indexs = []
	for i in indexs_list:
		if re.compile(r"(.*-[0-9].*-[0-9].*-[0-9].*)").findall(i):
			can_indexs.append(i)
	now_time = datetime.datetime.now().strptime(datetime.datetime.now().strftime("%Y.%m.%d"),"%Y.%m.%d") 
	#找出索引对应的时间 先备份 并找出需要删除的索引列表 初始化
	backup_list = []
	delete_list = []
	for i in can_indexs:
		ii  =  "-".join(i.split("-")[-3:])
		find_index_time = datetime.datetime.strptime(ii, "%Y-%m-%d")
		Ca = (now_time - find_index_time)
		#print(Ca)
		try:
			if int(str(Ca).split()[0]) == bakuptime:                   #########  备份逻辑
				backup_list.append(i)
			if int(str(Ca).split()[0]) > deletetime:
				delete_list.append(i)
		#except Exception as e:
		except:
			pass
		#print("%s is %s" %(Ca,i))
	return (backup_list,delete_list)

def Start_bak(ues_snapshot,backup_list=None):
	for i in backup_list:
		put_url = "http://10.42.71.37:9200/_snapshot/"+ues_snapshot
		params = {
		"type": "ufile",
		"settings": {
		"endpoint": "aha-log.ufile.cn-north-04.ucloud.cn",
		"public_key": "TOKEN_5445258c-3808-40dc-8389-d107b28a42c0", 
		"private_key": "2429aafd-c676-47f3-a632-474ed799744a", 
		"bucket": "aha-log",
		"compress": True,
		"chunk_size": "50mb",
		"base_path": "prodlog-" + time.strftime("%Y-%m-%d") + "/" + i,
		"max_snapshot_bytes_per_sec": "40mb",
		"max_restore_bytes_per_sec": "40mb"
			}
		}
		res = requests.put(put_url, json=params)         ###########  这一步好像会重复创建仓库 造成后面一个镜像会覆盖前面一个镜像
		#print(res.content)
		##########  开始备份索引
		put_index_name = put_url + "/"+ i + "?wait_for_completion"
		params = {
		"indices": i
		}
		print(put_index_name)
		res = requests.put(put_index_name, json=params)
		print(res.content)

def Delete_index(delete_list=None):
	if delete_list:
		for i in delete_list:
			delete_url = 'http://10.42.71.37:9200/' + i
			result = requests.delete(delete_url)
			print(result.content)


if __name__ == '__main__':
	Args = Find_indexs("http://10.42.71.37:9200/_cat/indices")
	Args_backup_list, Args_delete_list = Args
	Start_bak("ues_snapshot",Args_backup_list)
	Delete_index(Args_delete_list)



###### 恢复用   curl -X POST http://10.42.71.37:9200/_snapshot/ues_snapshot/logstash-java-useraggregationserver-service-info-2020-06-20/_restore
#  curl http://10.10.94.41:9200/_snapshot?pretty --user elastic:hEcQSIyMwqQ3      带账户密码的es仓库查看                                                                 仓库名                          镜像名	
