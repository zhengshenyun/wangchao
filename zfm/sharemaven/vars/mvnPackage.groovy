#!/usr/bin/env groovy

//def call(args) {
//  args = null==args ? "clean install -Dmaven.test.skip=true" : args
//  sh "${tool 'M3'}/bin/mvn ${args}"
//}

def call(){
//  args = null == "mvn clean install -Dmaven.test.skip=true"
  def mvnHome = tool 'MAVEN'
  def jdkHome = tool 'JAVA_HOME'
  def jdk7 = tool 'JDK1.7'
  env.PATH = "${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"

//  sh "cd ${env.MODULE_NAME}  && $args"

  def project_env = "${env.JOB_NAME}".toLowerCase().split('/')[0]
  def project_name = "${env.JOB_NAME}".toLowerCase().split('/')[2]
  println(project_name)

  // sh "mvn  compile package -pl ${env.JOB_BASE_NAME} -am -Dmaven.test.skip=true -P${project_env}"   edu项目测试使用命令
  
  if (project_name == 'erp') {
    env.PATH = "${jdk7}/bin:${mvnHome}/bin:${env.PATH}"
  	sh "mvn clean package -Dmaven.test.skip=true -U --settings /opt/maven/conf/erp-dev.xml"
  }
  else if (project_name == 'bco') {
    env.PATH = "${jdk7}/bin:${mvnHome}/bin:${env.PATH}"
  	sh "mvn clean package -Dmaven.test.skip=true -U --settings /opt/maven/conf/bco-dev.xml"
  }
  else if (project_name == 'itration-education-service') {
    env.PATH = "${jdk7}/bin:${mvnHome}/bin:${env.PATH}"
        sh "mvn  clean compile package -pl education-government -am -Dmaven.test.skip=true -Ptest"
       // sh "mvn clean package install org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -Ptest -U --settings /opt/maven/conf/new_edu.xml"
  }
  else {
  	sh "mvn  clean package -Dmaven.test.skip=true -P${project_env} --settings /opt/maven/conf/settings-public.xml"  
  }
  // sh "mvn  clean package -Dmaven.test.skip=true -P${project_env}"  
}
