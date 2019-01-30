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
secret_id = "AKIDCKOOIDVeE7ucFvT0Qn3eCbt6A7z36q8I"
secret_key = "9FD2Rp9LXbWM5ecTU3WoywjLhw0vkQvH"

def get_string_to_sign(method, endpoint, params):
    s = method + endpoint + "/?"
    query_str = "&".join("%s=%s" % (k, params[k]) for k in sorted(params))
    return s + query_str

def sign_str(key, s, method):
    hmac_str = hmac.new(key.encode("utf8"), s.encode("utf8"), method).digest()
    return base64.b64encode(hmac_str)

def public(**data):
    endpoint = "cvm.ap-beijing.tencentcloudapi.com"
    #endpoint = 'monitor.api.qcloud.com'
    #endpoint = endpoint
    public_data = {
        'Nonce' : 11886,
        'SecretId' : secret_id,
        'Timestamp' : Timestamp,
        'Version': Version,
    }
    data = dict( public_data.items() + data.items())
    s = get_string_to_sign("GET", endpoint, data)
    data["Signature"] = sign_str(secret_key, s, hashlib.sha1)
    #print(data["Signature"])
    # 此处会实际调用，成功后可能产生计费
    resp = requests.get("https://" + endpoint, params=data)
    #print(resp.url)
    json_data = json.loads(resp.text)
    return json_data 
