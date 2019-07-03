#!/usr/bin/env groovy
@Library('shareMaven') _
node {

  stage ('选择动作') {
    try {
      // action 选择，有deploy和rollback两种动作
      def action = choiceAction ()
      if (action == 'deploy') {
        def config=""
//        def gitTag=""
//        def gitRepo=""
        stage("加载配置"){
          //下载配置
          yamlCheckout{}
//          sh "echo ${env.JOB_NAME}"
//          sh "echo ${env.JOB_BASE_NAME}"
//          config = readYaml file: './'+"${env.JOB_NAME}"+'/'+"${env.JOB_BASE_NAME}"+'.yaml'
          config = readYaml file: './'+"${env.JOB_NAME}"+'.yaml'
        }
        // 在docker内部代码检出、执行测试、执行包构建
//        docker.image("${env.dockerMavenImage}").inside("${env.dockerMavenRunOpts}") {
          stage("检出源码") {
            gitCodeCheckout(config)
          }
          stage("编译构建") {
//            mvnPackage("${this.env.mavenPackageOpts}")
            mvnPackage(config)
          }
//        }
        // 收集打包产物
        stage('收集产物') {
          collection(config)
//          collection {
//            projectName = "${this.env.projectName}"
//            packageName = "${this.env.appTarget}"
//          }
        }
        // docker 镜像构建
        stage('镜像构建') {
          dockerBuild(config)
//          dockerBuild {
//            projectName = "${this.env.projectName}"
//          }
        }
        // 部署操作
        stage('部署生产') {
          deployContainer(config)
//          deployContainer {}
        }
      } else {
        // 版本回滚操作，针对镜像的版本回滚，会调用共享库类的几个stage操作
        stage('版本回滚') {
          rollbackContainer {
            getRegistryTagList= '/data/jenkins_etcd/getRegistryTagList.py'
          }
        }
      }
    } catch (exc) {
      sendEmail {
        emailRecipients= "${this.env.projectRecipientList}"
        error exc
      }
    }
  }
}