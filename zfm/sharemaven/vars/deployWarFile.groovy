#!/usr/bin/env groovy


def call(args){
  // println(args.erp.ouser-web.host)
  // println(args)
  // println(args.erp.'ouser-web'.host)

  //模块名字
  def moduleName = "${env.JOB_BASE_NAME}"

  def desServer = args.erp."${moduleName}".host
  def container = args.erp."${moduleName}".container


  def binaryBaseDir = '/opt/binary'
  def projectDir = "${env.JOB_NAME}".toLowerCase()
  def binaryDir = "${binaryBaseDir}/${projectDir}"

  
  // def pidNum = sh (script: " ansible all -i  \"${desServer}\"  -m shell -a \'ps -ef |grep ${container}|grep -v grep |awk \"{print \\\$2}\"  \'|awk NR==2\"{print}\" ")
  // sh " ansible all -i  \"${desServer}\"  -m shell -a \'ps -ef |grep ${container}|grep -v grep |awk \"{print \\\$2}\"  \' |awk NR==2\"{print}\" >/tmp/${env.JOB_BASE_NAME}.pid " 
  // def pidNum = sh " ansible all -i  \"${desServer}\"  -m shell -a \'cat /tmp/${env.JOB_BASE_NAME}.pid \'|awk NR==2\"{print}\"" 
  // 
  pidNum = sh (script: " ansible all -i  \"${desServer}\"  -m shell -a \'ps -ef |grep ${container}|grep -v grep |awk \"{print \\\$2}\"  \' |awk NR==2\"{print}\" ",returnStdout: true)
  println "${pidNum}"

  if ("${pidNum}" > 0 ) {
  	// sh (script: " ansible all -i  \"${desServer}\"  -m shell -a \'ps -ef |grep ${container}|grep -v grep |awk \"{print \\\$2}\" |xargs kill -9\' ")
  	sh (script: " ansible all -i  \"${desServer}\"  -m shell -a \'kill -9 ${pidNum}\' ")
    sh (script: """ ansible all -i "${desServer}" -m shell -a "rm -rf /data/tomcat/${container}/webapps/*" """)
    sh (script: """ ansible all -i "${desServer}" -m copy -a "src=${binaryDir}/${moduleName}.war dest=/data/tomcat/${container}/webapps/" """)
    sh (script:""" ansible all -i "${desServer}" -m  shell -a "export BUILD_ID=dontkillme" """)
    sh (script: """ ansible all -i "${desServer}" -m shell -a "nohup /data/tomcat/${container}/bin/catalina.sh start >/dev/null 2>&1 & " """)
  }
  else {

    sh (script: """ ansible all -i "${desServer}" -m copy -a "src=${binaryDir}/${moduleName}.war dest=/data/tomcat/${container}/webapps/" """)
    sh (script: """ ansible all -i "${desServer}" -m shell -a "rm -rf /data/tomcat/${container}/webapps/*" """)
    // sh (script: """ ansible all -i "${desServer}" -m shell -a "/data/tomcat/${container}/bin/catalina.sh start & " """)
    sh (script:""" ansible all -i "${desServer}" -m  shell -a "export BUILD_ID=dontkillme" """)
    sh (script: """ ansible all -i "${desServer}" -m shell -a "nohup /data/tomcat/${container}/bin/startup.sh >/dev/null 2>&1 & " """)
  }
  // sh (script: " ansible all -i  \"${desServer}\"  -m shell -a \'ps -ef |grep ${container}|grep -v grep |awk \"{print \\\$2}\" |xargs kill -9\' ")
  // sh (script: """ ansible all -i "${desServer}" -m copy -a "src=${binaryDir}/${moduleName}.war dest=/data/tomcat/${container}/webapps/" """)
  // sh (script: """ ansible all -i "${desServer}" -m shell -a "/data/tomcat/${container}/bin/catalina.sh start" """)

}