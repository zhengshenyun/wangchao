#!/usr/bin/env groovy
@Library('shareMaven') _
// currentBuild.description = "Branch or Tag is $GIT_BRANCH"

currentBuild.description = "Choice action is ###$action###"

// Build History 显示分支或者Tag
manager.addShortText("$env.gitTag")

node('jenkins_slave_01') {

  def mvnHome = tool 'MAVEN'
  def jdkHome = tool 'JAVA_HOME'
  env.PATH = "${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"
  
  if (rollBack == "TRUE") {
    pass
  }
  

  else {
    if (action == "only_build") {
      stage("仅构建") {
        sh "echo '选择动作为：#################仅仅构建###############'\n"
      }
      stage("拉取代码") {
        gitCodeCheckout()
      }
      stage("编译构建") {
        mvnPackage()
      }
      stage("构建镜像") {
        dockerBuild()
      }

    }

    if (action == "only_deploy") {
      stage("仅部署"){
        sh "echo '选择动作为：#################仅仅部署###############'\n"
      }
      stage("容器部署"){
        deployContainer()
      }
    }

    if (action == "deploy"){
      stage("发布开始"){
        sh "echo '选择动作为：#################构建发布部署###############'\n"
      }
      stage("拉取代码") {
        gitCodeCheckout()
      }
      stage("编译构建") {
        mvnPackage()
      }
      stage("构建镜像") {
        dockerBuild()
      }
      stage("容器部署"){
        deployContainer()
      }
    }


  }


}
