#!/usr/bin/env groovy
def call() {

//  10.6.2.34_10000:8080 10.6.2.34_10001:8080
  def project = "${env.JOB_NAME}".toUpperCase().split('/')[1]
  def appEnv = "${env.JOB_NAME}".toUpperCase().split('/')[0]
  def projectName = "${env.JOB_BASE_NAME}"
  def gitTag = "${env.gitTag}"

  def appPackage = "${env.packageName}"

  def imageName = "${env.JOB_NAME}".toLowerCase()
  def toImage = "image.tfit.com/project/${imageName}:${gitTag}"

  def dockerHosts="${env.dockerHosts}"
  def hostsArry = dockerHosts.split(' ')

  def volumePath = "${env.volumePath}"

  for (int i = 0; i<hostsArry.size(); i++){
    def portMap = hostsArry[i].split('_')[1].trim()
    def appIp = hostsArry[i].split('_')[0].trim()
    def appPort = portMap.split(':')[0].trim()
    def containerName = (project + "_" + appEnv + "_" + projectName).toUpperCase().trim()



    // 保留当前容器的镜像sha值
    try {
      RESULT = sh (script: "docker -H"+" "+ appIp+":2375 inspect -f '{{.Image}}'"+" "+containerName,returnStdout: true).trim()
      println RESULT
    } catch (err) {
      println "Failled: ${err}"
    }

    // 停止并删除当前容器
    try {
      sh (script: "docker -H"+" "+appIp+":2375 stop"+" "+containerName,returnStdout: true)
      sh (script: "docker -H"+" "+appIp+":2375 rm"+" "+containerName,returnStdout: true)
    } catch (err) {
      println "Failled: ${err}"
    }

    sleep(3)

    // 拉取push到registry的image
    sh (script: "docker -H"+" "+appIp+":2375 pull"+" "+toImage,returnStdout: true)

    // 运行容器
    sh (script: "docker -H"+" "+appIp+":2375 run -d --restart=always -p "+portMap+" -v "+volumePath+" --name="+containerName+" "+" "+toImage+" "+"java -jar"+" "+"/opt/"+appPackage.trim(),returnStdout: true)

    sleep(3)
    // 获取当前运行容器的状态码
    def containerStatus = sh (script: "docker -H"+" "+appIp+":2375 inspect -f '{{.State.Status}}'"+" "+containerName,returnStdout: true).trim()
    println containerStatus
    // 检测状态，如果不是running状态，停止并删除，返回错误
    if (containerStatus != 'running') {
      sh (script: "docker -H"+" "+appIp+":2375 stop"+" "+containerName,returnStdout: true)
      sh (script: "docker -H"+" "+appIp+":2375 rm"+" "+containerName,returnStdout: true)
      error "containerStatus is ${containerStatus}"
    } else {
      println "Deploy Success!"
    }

    // 删除前面保存的容器的镜像
    try {
      sh (script: "docker -H"+" "+appIp+":2375 rmi"+" "+RESULT,returnStdout: true)
    } catch (err) {
      println "Failled: ${err}"
    }
  }


}