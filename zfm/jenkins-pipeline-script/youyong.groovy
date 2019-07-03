#!/usr/bin/env groovy

import groovy.json.JsonSlurper

def a = 'tomcat'

def b = 'asdlfas/asdf/tomcat/asdfa/'

if ('tomcat' in b.split("/")) {
	println(a)
}


//   groovy 调用接口  并而解析json   

def connection = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=3.3.3.3").openConnection()
connection.setRequestMethod('GET')
connection.doOutput = true


def writer = new OutputStreamWriter(connection.outputStream)
writer.flush()
writer.close()
connection.connect()


def respText = connection.content.text
	println respText


def jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(respText)
        println(result.data)

