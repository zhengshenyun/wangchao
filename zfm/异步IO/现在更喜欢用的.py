import asyncio                     ############  这个例子只是帮我们理解而已  #############

async def compute(x, y):
    print("Compute %s + %s ..." % (x, y))
    await asyncio.sleep(x + y)         ##############  模拟耗时人物  真正可以不用写  
    return x + y


async def print_sum(x, y):
    result = await compute(x, y)
    print("%s + %s = %s" % (x, y, result))


loop = asyncio.get_event_loop()
tasks = [print_sum(1, 2), print_sum(1, 3)]             ###########  任务一个一个加  社和小任务的
loop.run_until_complete(asyncio.wait(tasks))
loop.close()



====================================================================================================================
import threading                                        ###############  这个是协程  ###############
import asyncio
import requests
import os,time

loop = asyncio.get_event_loop()

def copy():
    os.system("cp -rf /root/wc /tmp/%s" %(time.time()))                     ###########  显示异步io
    return "ok"

async def t():                                                    
    get = lambda:requests.get('http://www.sunshinelunch.com')         ##########   这一步 可要可不要   #############
    print("ok"+ str(threading.currentThread()))    #############此处线程对象是一样的 证明没有线程切换
    temp = await loop.run_in_executor(None, copy)      ########### 跟那个gevent一样的  都是要等待10秒的 此处连接../asyncio的最后一个例子
    print(temp)   
loop.run_until_complete(asyncio.gather(*[t() for i in range(10)]))

