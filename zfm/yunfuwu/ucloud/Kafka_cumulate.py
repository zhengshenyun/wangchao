#!/usr/bin/python
#encoding=utf8

import time,sys
import datetime
import requests
import json
from ucloud.core import auth
import logging
logging.captureWarnings(True)

#Version =  '2017-03-12'
#Region = 'ap-beijing'
PrivateKey = "0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf"
PublicKey = "zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_"

#def get_string_to_sign(method, endpoint, params):
#    s = method + endpoint + "/?"
#    query_str = "&".join("%s=%s" % (k, params[k]) for k in sorted(params))
#    return s + query_str

##########  查询上5分钟的慢sql


def _verfy_ac(kafka_name,Consumer_Group_name):
    cred = auth.Credential("zJInmDqJs5qcKW9lzWe0J2Mf-r9jmNJHkRcj6Cp_","0OQFsrHvzKwHXpb0Lh8-J3PS4Uc7V0cqjXhXZGOS1LxiGtOUTXB43rxfKMqahKWf")
    d = {'Action': 'DescribeUKafkaConsumer', 'Region': 'cn-bj2',"ProjectId" :  "org-20366","Zone": "cn-bj2-03","ClusterInstanceId": kafka_name,"Type":  "KF","ConsumerGroup":Consumer_Group_name}
    #print(cred.verify_ac(d)) 
    return cred.verify_ac(d)

def public(kafka_name,Consumer_Group_name):
    endpoint = "https://api.ucloud.cn/"
    public_data = {
        'Action': "DescribeUKafkaConsumer",
        'PublicKey' : PublicKey,
	"Region"     :  "cn-bj2",
	"Zone": "cn-bj2-03",
	"ClusterInstanceId": kafka_name,
	"Type":  "KF",
	"ConsumerGroup":Consumer_Group_name,
	"ProjectId": 'org-20366'
    }
    data = dict(public_data.items())
    data["Signature"] = _verfy_ac(kafka_name,Consumer_Group_name)
    resp = requests.get(endpoint, params=data,verify=False).text
    res = json.loads(resp)
    try:
        #print("--- %s ---的积压数为%s个"%(Consumer_Group_name,res["Topics"][0]["TotalLag"]))
        if res["Topics"][0]["TotalLag"] == None or res["Topics"][0]["TotalLag"]<100000:
            pass
        else:
            payload = {"title": "kafka积压","text": "--- %s ---的积压数为%s个"%(Consumer_Group_name,res["Topics"][0]["TotalLag"])}
            head = {"Content-Type": "application/json"}
            requests.post('https://open.feishu.cn/open-apis/bot/hook/84bdddf9-767d-43c5-8fb9-c3c1f18828b5', data=json.dumps(payload),headers=head)
    except Exception as e:
        #print(Consumer_Group_name,res)
        pass

if __name__ == '__main__':
    kafka_name = [{"ukafka-dkfjtqul":["bidata","jobserver-consumer","DataAppPageView",'data_td_log','data_big_data_tag','logstash_live_room_stat_mysql','datawechatevent','syslog','logstash_data_realtime_binlog','dataeventall','logstash_bidata_live_room_stat_bin','ahatagserver','elkaha']},{"ukafka-kjvmrqv3":["ahatagserver","member_order_give_group","PlanRoundGroup","audio_play_group","ahaLiveVideoGroup","order_student_plan_group","userSurveyWorkEvent","userLoginGroupbuy","order_give_create_group","operationserver","courseTag","event503AppMsgGroup","memberPrivilege","userDeviceGroup","orderContractGroup","memberserver","red_package_pay_call_back","msgTopic2Group","event505AppMsgGroup","mkt_kafka","event501AppMsgGroup","liveserver","userPlayEvent","IosContractPromotionGroup","gameserver","pay_call_back","event502AppMsgGroup","ahaLiveEventGroup","orderMember","sms","event507AppMsgGroup","train","userEventGroup","invitation","eventOperationPlayTime","courseSkuUpdateGroup","groupStarGet","contractGroupbuy","weiXinReport","member_withhold_first_group","ahaLiveEventFixGroup","groupStarGet1","eventPlayTime","event508AppMsgGroup","coursepermission","miniEventGroup","userStarRankEvent","distributionserver","msgTopic1Group","yhuso_test_hongdao","ahaLiveVideoCanlGroup","newUserGift","policy_order_created_group","credit_change","msgserver","message","orderAddMember","event500AppMsgGroup","event504AppMsgGroup","third_pro","orderCourseUpdateGroup","logstash_pbl_playtime_consumer","contract_call_back","eventUserTask","userPblWorkEvent"]}]
    for i in kafka_name:
        for ii in i.values()[0]:
            public(i.keys()[0],ii)

