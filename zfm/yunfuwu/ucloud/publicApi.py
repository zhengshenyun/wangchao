# -*- coding: utf8 -*-
import base64
import hashlib
import hmac
import time
import datetime
import requests
import json


Timestamp = int(time.time())
Version =  '2017-03-12'
#Region = 'ap-beijing'
PrivateKey = "28bbdc245e9a2bf0e4192c5d7b6dbd3f2689c80b"
PublicKey = "/5arXJwXNhcnhVYxOzdC1c/g9+uxXrXIlD4A5RuE3XBD8zW1EF/5OA=="

#def get_string_to_sign(method, endpoint, params):
#    s = method + endpoint + "/?"
#    query_str = "&".join("%s=%s" % (k, params[k]) for k in sorted(params))
#    return s + query_str


def _verfy_ac(private_key, params):
    items=params.items()
    # 请求参数串
    items.sort()
    # 将参数串排序
 
    params_data = "";
    for key, value in items:
        params_data = params_data + str(key) + str(value)
    params_data = params_data + private_key
 
    sign = hashlib.sha1()
    sign.update(params_data)
    signature = sign.hexdigest()
 
    return signature

def public(**data):
    #endpoint = "cvm.ap-beijing.tencentcloudapi.com"
    #endpoint = 'monitor.api.qcloud.com'
    endpoint = "https://api.ucloud.cn/"
    public_data = {
	'Action': "DescribeUHostInstance",
        'PublicKey' : PublicKey,
    }
    data = dict( public_data.items() + data.items())
    data["Signature"] = _verfy_ac(PrivateKey, data)
    # 此处会实际调用，成功后可能产生计费
    resp = requests.get(endpoint, params=data)
    #print(resp.url)
    json_data = json.loads(resp.text)
    return json_data 
