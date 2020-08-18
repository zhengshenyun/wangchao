#!/usr/bin/python
#encoding=utf8

import time,sys
import datetime
import requests
import json
from ucloud.core import auth

Timestamp = int(time.time())
Version =  '2017-03-12'
#Region = 'ap-beijing'
PrivateKey = "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf"
PublicKey = "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_"

#def get_string_to_sign(method, endpoint, params):
#    s = method + endpoint + "/?"
#    query_str = "&".join("%s=%s" % (k, params[k]) for k in sorted(params))
#    return s + query_str

##########  查询上5分钟的慢sql

BeginTime = int(time.time()) - 60*5*6
EndTime = int(time.time())

def _verfy_ac(DBId):
    cred = auth.Credential(
    "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_",
    "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf",
    )
    d = {'Action': 'DescribeUDBInstanceLog', 'Region': 'cn-bj2', "DBId" : "udbha-if54zk", "ProjectId" :  "org-20366", "DBId" : DBId, "BeginTime" : BeginTime,"LogType" : "slow","EndTime":EndTime}
    #print(cred.verify_ac(d)) 
    return cred.verify_ac(d)

def public(data):
    #endpoint = "cvm.ap-beijing.tencentcloudapi.com"
    #endpoint = 'monitor.api.qcloud.com'
    endpoint = "https://api.ucloud.cn/"
    public_data = {
        'Action': "DescribeUDBInstanceLog",
        'PublicKey' : PublicKey,
	"Region"     :  "cn-bj2"
    }
    data = dict(public_data.items() + data.items())
    data["Signature"] = _verfy_ac(data["DBId"])
    #print(data)
    resp = requests.get(endpoint, params=data,verify=False).text
    res = json.loads(resp)
    if res["Log"] == u"该时间段内未发现慢查询，您也可以在 mysql.slow_log 表中查询\n":
    	return 0
    else:
        return res

if __name__ == '__main__':
    error_tag = 1
    DBId = ["udbha-if54zk","udb-c0awz4","udb-ofzuir","udb-rbesy4","udbha-c2dqj0","udbha-jabsrd","udbha-xfdo5y","udb-vowuzm","udbha-25ce14","udbha-bvdujw","udb-cksrez","udbha-lg5qep","udb-rwlf5e","udbha-vxtpns","udbha-lr04y5ha","udbha-pkugsi","udbha-ilvncv","udb-4tv2gk","udbha-q2oll0","udbha-sq4l43","udbha-odurbkm4","udb-qnhullqf","udbha-w1crelpq","udb-r3ipjeyh","udbha-lqmxh1gb","udbha-vzyktd5z","udb-3cwgxyiv","udbha-ujfqit"]
    Mysql_name = ["aha-main-db","aha-main-db-slave1","aha-main-db-slave3","aha-main-db-slave2","aha-survey-db","aha-pay-db","aha-live-db","aha-live-db-slave1","aha-msg-db","aha-account-db","aha-account-db-slave1","aha-distribution-db","aha-distribution-db-slave1","aha-auto-reply-db","aha-gps-db","aha-tag-db","xkl-account-db","xkl-account-slave","aha-withdraw-db","aha-mkt-db","aha-member-db","aha-member-db-slave1","aha-story-db","aha-story-db-slave","aha-train-db","aha-operation-db","aha-operation-db-slave1","aha-apollo-db"]
    for k,i in enumerate(DBId):
        Data = {
             "Region" : "cn-bj2",
             "ProjectId" :  "org-20366",
             "DBId" : i,
             "BeginTime" : BeginTime,
             "LogType" : "slow",
             "EndTime" : EndTime}
        if "slave" in Mysql_name[k] and "main" not in Mysql_name[k]:
            #print("%s---丢弃" %Mysql_name[k])
            continue
        #print(Mysql_name[k])
        if Mysql_name[k] == "aha-main-db-slave1":
            continue
        ress = public(Data)
        mysql_name = Mysql_name[k]
	if ress == 0:
            pass
        else:
            error_tag = 0
            new_log = ress['Log']
            payload = {"title": "----线上 %s 慢SQL----" %mysql_name,"text": new_log}
	    head = {"Content-Type": "application/json"}
            requests.post('https://open.feishu.cn/open-apis/bot/hook/1e9d29b7c82e4bb3bfdfb65e67a0fe35', data=json.dumps(payload),headers=head)
            #requests.post('https://open.feishu.cn/open-apis/bot/hook/a5dbeebfe6d24fec81a942770abf790a', data=json.dumps(payload),headers=head)
    if error_tag == 0:
        last_log = '-----------本次轮询结束---------'
        payload = {"text": last_log}
        head = {"Content-Type": "application/json"}
        requests.post('https://open.feishu.cn/open-apis/bot/hook/1e9d29b7c82e4bb3bfdfb65e67a0fe35', data=json.dumps(payload),headers=head)
        #requests.post('https://open.feishu.cn/open-apis/bot/hook/a5dbeebfe6d24fec81a942770abf790a', data=json.dumps(payload),headers=head)
