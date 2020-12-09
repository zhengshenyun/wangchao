#!/usr/bin/python
#encoding=utf8

import time,sys,os
import datetime
import requests
import json
from ucloud.core import auth
import warnings
warnings.filterwarnings("ignore")
Timestamp = int(time.time())
Version =  '2017-03-12'
#Region = 'ap-beijing'
PrivateKey = "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf"
PublicKey = "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_"


##########  查询上5分钟的慢sql

#BeginTime = int(time.time()) - 60*5*6
#EndTime = int(time.time())

def _verfy_ac():
    cred = auth.Credential(
    "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_",
    "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf",
    )
    d = {"PublicKey":PublicKey, 'Action': 'DescribeUDBBackup', 'Region': 'cn-bj2', "Offset":0, "Limit":100, "ProjectId":"org-20366"}
    #print(cred.verify_ac(d)) 
    return cred.verify_ac(d)

def _verfy_ac2(DBid,DBbackid):
    cred = auth.Credential(
    "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_",
    "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf",
    )
    d = {"PublicKey":PublicKey, 'Action': 'DescribeUDBInstanceBackupURL', 'Region': 'cn-bj2', "ProjectId":"org-20366","DBId":DBid, "BackupId":DBbackid}
    #print(cred.verify_ac(d)) 
    return cred.verify_ac(d)


def public(Data):
    endpoint = "https://api.ucloud.cn/"
    public_data = {
        'Action': "DescribeUDBInstanceLog",
        'PublicKey' : PublicKey,
	"Region"     :  "cn-bj2"
    }
    data = dict(public_data.items() + Data.items())
    #print(data)
    data["Signature"] = _verfy_ac()
    resp = requests.get(endpoint, params=data,verify=False).text
    res = json.loads(resp)
    return res

def public2(Data):
    endpoint = "https://api.ucloud.cn/"
    public_data = {
        'Action': "DescribeUDBInstanceBackupURL",
        'PublicKey' : PublicKey,
        "Region"     :  "cn-bj2"
    }
    data = dict(public_data.items() + Data.items())
    DBid = Data["DBId"]
    DBbackid = Data['BackupId']
    data["Signature"] = _verfy_ac2(DBid,DBbackid)
    resp = requests.get(endpoint, params=data,verify=False).text
    res = json.loads(resp)
    return res

def Download(Dir_name,DBname,InnerBackupPath):
    os.system("mkdir /mysql_back/%s/%s" %(Dir_name,DBname))
    os.system("wget -P /mysql_back/%s/%s %s" %(Dir_name,DBname,InnerBackupPath))

if __name__ == '__main__':
      Data = {
             "Region" : "cn-bj2",
             "ProjectId" :  "org-20366",
             "Offset" : 0,
             "Limit"   : 100,
             "Action"  : "DescribeUDBBackup"
               }
      ress = public(Data)["DataSet"]
      dir_name = time.strftime('%Y-%m-%d',time.localtime(time.time()))
      os.system("mkdir /mysql_back/%s" %dir_name)
      today = datetime.date.today()
      today_start_time = int(time.mktime(time.strptime(str(today), '%Y-%m-%d')))
      Data2 = {
                "Region" : "cn-bj2",
             "ProjectId" :  "org-20366",
             "Action"  : "DescribeUDBInstanceBackupURL"
              }
      for i in ress:
            if i["DBName"] == "redash":
	          pass
		  continue
            if i["BackupTime"] > today_start_time and i["DBName"]:
                  #return (i["DBName"],i['BackupId'],i['DBId'])  "DBId":DBId, "BackupId":BackupId
                  Data2["DBId"] = i['DBId']
                  Data2["BackupId"] = i['BackupId']
                  ress2 = public2(Data2)
		  print(i["DBName"],"------------",ress2['InnerBackupPath'])
		  Download(dir_name,i["DBName"],ress2['InnerBackupPath'])
            else:
                  pass


