#!/usr/bin/groovy

def call() {

  // 项目位置
  def projectPath = "${env.WORKSPACE}/${env.JOB_BASE_NAME}"
  // 编译包位置
  def packagePath = "${projectPath}/target"
  // 编译包名称
  def packageName = "${env.PACKAGE_NAME}"
  //解压目录名
//  def packageUnzipName = config.appTarget.substring(0,config.appTarget.lastIndexOf("."))
  //需要将编译后的软件包拷贝到的路径
  def buildPath = "${env.WORKSPACE}/${env.JOB_BASE_NAME}/target"


  //Dockerfile内容
  def fromImage = "${env.fromImage}"
  if ("${fromImage}".contains('tomcat')){
    def tomcatDir = '/opt/tomcat8/webapps/'
    env.dockerFileContext = """FROM "${env.fromImage}"
MAINTAINER devops "ops@ssic.cn"
ADD ${packageName} ${tomcatDir}
RUN cd  ${tomcatDir} && unzip ${env.PACKAGE_NAME} -d ${tomcatDir}
    """
    }
  else {
    def jarDir = '/opt/'
    env.dockerFileContext = """FROM "${env.fromImage}"
MAINTAINER devops "ops@ssic.cn"
COPY ${packageName} ${jarDir}
    """
  }	

  println(dockerFileContext)

	
  // 生成env上下文的imageTag
  def imageTag = "${env.GIT_BRANCH}"
  def imageName = "${env.JOB_NAME}".toLowerCase()
  def toImage = "image.tfit.com/project/${imageName}"

  // 生成Dockerfile
  writeFile encoding: 'UTF-8', file: "${buildPath}/Dockerfile",text: dockerFileContext

  // 执行docker build
  sh (script: "docker pull ${env.fromImage}",returnStdout: true)
  sh (script: "docker build --no-cache=true -t ${toImage}:${imageTag} ${buildPath}",returnStdout: true)
  sh (script: "docker push ${toImage}:${imageTag}",returnStdout: true)

//  sh (script: "docker rmi ${config.toImage}/${config.appEnv}/${config.appProject}/${env.JOB_BASE_NAME}:${imageTag}",returnStdout: true)
}
