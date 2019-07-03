#!/usr/bin/env groovy
@Library('shareMaven') _
// currentBuild.description = "Branch or Tag is $GIT_BRANCH"

currentBuild.description = "Choice action is ###$action###"

// Build History 显示分支或者Tag
manager.addShortText("$env.gitTag")

node('jenkins_slave_01') {

  def mvnHome = tool 'MAVEN'
  def jdkHome = tool 'JDK1.7'
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
      stage("文件传输") {
        b2bBinary()
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
      stage("包包传输") {
        b2bFiletransfer()
      }
      stage("加载配置"){
        def WORKSPACE = '/opt/yamlDir'
        // yamlCheckOut()
        sh (script: """scp -P 8888 ${WORKSPACE}/TEST/B2b/b2bconfig.yaml root@172.20.20.30:/opt/jenkins_slave/b2bconfig.yaml""",returnStdout: true)
        // config = readYaml file: "${env.WORKSPACE}"+'/TEST/B2b/b2bconfig.yaml'
      } 
      // stage("加载配置"){
      //   customWorkspace '/opt/yamDir'
      //   yamlCheckOut()
      //   sh (script: """scp -P 8888 ${env.WORKSPACE}/TEST/B2b/b2bconfig.yaml root@172.20.20.30:/opt/jenkins_slave/b2bconfig.yaml""",returnStdout: true)
      //   // config = readYaml file: "${env.WORKSPACE}"+'/TEST/B2b/b2bconfig.yaml'
      // }

      node('huawei_test_slave_02'){
        
        stage("开始部署"){
          config = readYaml file: '/opt/jenkins_slave/b2bconfig.yaml'
          println(config)
          deployWarFile(config)
        }  
      }
      
    }


  }


}
