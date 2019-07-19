#!/usr/bin/env groovy
@Library('shareMaven') _


// Build History 显示分支或者Tag

manager.addShortText("$env.gitTag")

println '-----------------------------start HW-UAT env-----------------------------'

node('jenkins_slave_01') {
  wrap([$class: 'BuildUser']) {
      def user_user = env.BUILD_USER_ID
            currentBuild.description = "Choice action is ####${user_user}###"
  }

  def mvnHome = tool 'MAVEN'
  def project_second_name =  "${env.JOB_NAME}".toLowerCase().split('/')[2]


   //   stage("发布开始"){
   //     sh "echo '选择动作为：#################start deploy###############'\n"
   //   }

      stage("拉取代码") {
        // 从yaml文件中获取git地址 和远端服务器地址
        config = readYaml file: '/opt/yamlDir/HW-UAT/hw-uat.yaml'
        giturl = config.project."${project_second_name}".giturl
        ip_hosts = config.project."${project_second_name}".host
        gitroot = config.project."${project_second_name}".gitroot
        def jdk_version =  config.project."${project_second_name}".jdkversion
        jdkHome = tool "${jdk_version}"
        env.PATH = "${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"
        hwuatgitCodeCheckout(giturl)
      }

      stage("编译构建") {
        def mvncmdenv = config.project."${project_second_name}".mvncmd
        hwuatmvnPackage(mvncmdenv)
      }

      stage("包包传输") {
         package_type = config.project."${project_second_name}".packaging
         sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)
         sh "echo sed file"
         sh "ssh  root@172.20.20.30 \"sed -i '4s:ADD.*:ADD ${env.JOB_BASE_NAME} /opt/tomcat/webapps/:' /superking/${package_type}/Dockerfile\""   // 修改dokerfile脚本
         sh "ssh  root@172.20.20.30 \"sed -i '2s:nohup.*:nohup java -jar /opt/tomcat/webapps/${env.JOB_BASE_NAME} \\&:' /superking/${package_type}/run.sh\""   // 修改run.sh脚本
         docker_name = sh(script:"date +\"%Y-%m-%d-%H-%M-%S\"", returnStdout: true).trim()
         println docker_name
         docker_nikename = "${env.JOB_BASE_NAME}".toLowerCase()
         sh "echo build container"
         sh "ssh  root@172.20.20.30 \"docker build -t ${docker_nikename}-${docker_name} /superking/${package_type}/\"" //打成镜像文件
         sh "echo  change docker tag name"
         sh "ssh  root@172.20.20.30 \"docker tag  ${docker_nikename}-${docker_name} 172.20.20.30:5000/${docker_nikename}-${docker_name}\"" //打成镜像文件
         sh "echo  push container to registry"
         sh "ssh  root@172.20.20.30 \"docker push 172.20.20.30:5000/${docker_nikename}-${docker_name}\"" //打成镜像文件
      }

      stage("huawei_test_slave_02-部署") {
         sh "echo  start origin"
      }

      node('huawei_test_slave_02'){
        stage("开始部署远程部署"){
            host_neirong = "${ip_hosts}".split("\\|")
            host_len = "${ip_hosts}".split("\\|").size()
            for (int i = 0; i<host_len; i++){
                ip_host =  host_neirong[i].split("#")[1]
                println "后端机器ip地址为 ${ip_host}"
                sh "echo  start pull container"
                sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker pull 172.20.20.30:5000/${docker_nikename}-${docker_name}"
//              sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker run -itd --name ${docker_nikename}-${docker_name} 172.20.20.30:5000/${docker_nikename}-${docker_name}"
//              sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker ps"
//              sh "echo  =====================finish===================="
            }
        }
      }
   }

