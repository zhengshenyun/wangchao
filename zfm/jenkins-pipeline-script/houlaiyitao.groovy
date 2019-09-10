#!/usr/bin/env groovy
@Library('shareMaven') _


// Build History 显示分支或者Tag

manager.addShortText("$env.BRANCH_NAME")

println '-----------------------------start HW-UAT env-----------------------------'

node('jenkins_slave_01') {
  wrap([$class: 'BuildUser']) {
      def user_user = env.BUILD_USER_ID
            currentBuild.description = "Choice action is ####${user_user}###"
  }

  def mvnHome = tool 'MAVEN'
  def project_second_name =  "${env.JOB_NAME}".toLowerCase().split('/')[2]
  def project_first_name =  "${env.JOB_NAME}".toLowerCase().split('/')[1]
  def project_zero_name = "${env.JOB_NAME}".toLowerCase().split('/')[0]
  def build_id = "${env.BUILD_ID}"

  // 临时打印出变量

  //print "333333333333333" + project_second_name
  //print "111111111111111" + project_zero_name
  //print "222222222222222" + project_first_name


   //   stage("发布开始"){
   //     sh "echo '选择动作为：#################start deploy###############'\n"
   //   }

      stage("拉取代码") {
        // 从yaml文件中获取git地址 和远端服务器地址

    if (project_zero_name == "hw-prod") {
        config = readYaml file: '/opt/yamlDir/HW-PROD/hw-prod.yaml'
    } else if (project_zero_name == "hw-test") {
        config = readYaml file: '/opt/yamlDir/HW-TEST/hw-test.yaml'
    } else {
        config = readYaml file: '/opt/yamlDir/HW-UAT/hw-uat.yaml'
    }

        jenkins_slave = config.jenkinslave
        env_name = config.env
        giturl = config.project."${project_first_name}"."${project_second_name}".giturl
        ip_hosts = config.project."${project_first_name}"."${project_second_name}".host
        print "333333333" + ip_hosts
        use_docker = config.project."${project_first_name}"."${project_second_name}".use_docker
        port_des = config.project."${project_first_name}"."${project_second_name}".port
        gitroot = config.project."${project_first_name}"."${project_second_name}".gitroot
        branch_name = config.project."${project_first_name}"."${project_second_name}".branch
        start_cmds = config.project."${project_first_name}"."${project_second_name}".start_cmd
        origin_path = config.project."${project_first_name}"."${project_second_name}".origin_path
        def jdk_version =  config.project."${project_first_name}"."${project_second_name}".jdkversion
        jdkHome = tool "${jdk_version}"
        env.PATH = "${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"
        hwuatgitCodeCheckout(giturl,branch_name)
      }

      stage("编译构建") {
        def mvncmdenv = config.project."${project_first_name}"."${project_second_name}".mvncmd
        hwuatmvnPackage(mvncmdenv)
      }

      stage("包包传输") {
         package_type = config.project."${project_first_name}"."${project_second_name}".packaging
         tgzdata = sh(script:"date +\"%Y-%m-%d\"", returnStdout: true).trim()
         tmp_name = "${env.JOB_BASE_NAME}"

         //println "${tmp_name}".tokenize('.')

         // 通过yaml文件匹配传输路径   开发乱的一逼
         if ("tgz" in tmp_name.tokenize('.')) {
         sh (script: """scp -P 8888 ${env.WORKSPACE}/target/${tgzdata}/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)
         return
         }else {
         try {
                 //sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)
                 //sh (script: """scp -P 8888  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)
            // 原来的东西 但是想让她走else 所以改了if的判断
               if (project_zero_name == "hw-prod1111") {
                  sh (script: """scp -P 60022 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.18.20.30:/superking/${package_type}""",returnStdout: true)
               } else {
                  if (project_first_name == 'edu') {
                        if (package_type == 'war') {
                                sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}/ROOT.war""",returnStdout: true)
                                //sh (script: """scp -P 8888  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
                                sh (script: """scp -P 22  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/warfuck/ROOT.war""",returnStdout: true)
                        } else {
                                 sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
                                //sh (script: """scp -P 8888  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
                        }
                  } else {
                         sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
                         //sh (script: """scp -P 8888  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
                  }
               }


         } catch (err) {

                 //sh (script: """scp -P 22 ${env.WORKSPACE}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)
         //sh (script: """scp -P 8888  ${env.WORKSPACE}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/superking/${package_type}""",returnStdout: true)

           if (project_zero_name == "hw-prod") {
                  sh (script: """scp -P 60022 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.18.20.30:/prodsuperking/${package_type}""",returnStdout: true)
               } else {
                  // uat 环境的war包传输逻辑
                  sh (script: """scp -P 22 ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}/ROOT.war""",returnStdout: true)
                  sh (script: """scp -P 8888  ${env.WORKSPACE}/${gitroot}/target/${env.JOB_BASE_NAME} root@172.20.20.30:/prodsuperking/${package_type}""",returnStdout: true)
               }

         }
         if (use_docker == "false") {
                return
                }
         }
         sh "echo sed file"
         //  ######以前下面是可以的  后面他的war包后面必须是ROOT.war包名字 所以改为下面
         if (package_type == 'war') {
         // sh "ssh  root@172.20.20.30 \"sed -i '4s:ADD.*:ADD ${env.JOB_BASE_NAME} /opt/tomcat/webapps/:' /superking/${package_type}/Dockerfile\""   // 修改dokerfile脚本
                //------------------此处他打的包要的镜像不一样 所以做如下改动--------------------------------
                if (project_second_name in ["education-government","education-provider"]) {
                sh "ssh  root@172.20.20.30 \"sed -i '4s:ADD.*:ADD ROOT.war /opt/tomcat/webapps/:' /prodsuperking/${package_type}/Dockerfile\""   // 修改war类型 dokerfile脚本
                } else {
                         sh "ssh  root@172.20.20.30 \"sed -i '4s:ADD.*:ADD ROOT.war /opt/tomcat/webapps/:' /prodsuperking/warfuck/Dockerfile\""   // 修改war类型 dokerfile脚本
                        }
         } else {
                sh "ssh  root@172.20.20.30 \"sed -i '4s:ADD.*:ADD ${env.JOB_BASE_NAME} /opt/tomcat/webapps/:' /prodsuperking/${package_type}/Dockerfile\""   // 修改jar类型 dokerfile脚本
         }
         // jar包启动的时候需要的参数
         sh "ssh  root@172.20.20.30 \"sed -i '2s:nohup.*:nohup java -jar /opt/tomcat/webapps/${env.JOB_BASE_NAME}>/dev/null \\&:' /prodsuperking/${package_type}/run.sh\""   // 修改run.sh脚本
         sh "ssh  root@172.20.20.30 \"sed -i '2s:nohup.*:nohup java -jar /opt/tomcat/webapps/${env.JOB_BASE_NAME}>/dev/null \\&:' /prodsuperking/warfuck/run.sh\""   // 修改run.sh脚本
         docker_name = sh(script:"date +\"%Y-%m-%d-%H-%M-%S\"", returnStdout: true).trim()
         println docker_name
         docker_nikename = "${env.JOB_BASE_NAME}".toLowerCase()
         sh "echo build container"
         //  -------------------------------------build的时候 也是做一下判断 因为目录文件有2个  一个 war  一个warfuck----------------------------------------------
         if (project_second_name in ["education-government","education-provider"] || package_type == "jar") {
             sh "ssh  root@172.20.20.30 \"docker build -t ${docker_nikename}-${build_id} /prodsuperking/${package_type}/\"" //打成镜像文件  war目录下是 education-government 和 education-provider 需要打toncat-session镜像
         } else {
                 sh "ssh  root@172.20.20.30 \"docker build -t ${docker_nikename}-${build_id} /prodsuperking/warfuck/\"" //打成镜像文件
                }
         sh "echo  change docker tag name"
         if (project_second_name in ["education-pro-app","education-edu-app"]) {
                 sh "ssh  root@172.20.20.30 \"docker tag  ${docker_nikename}-${build_id} 172.20.20.30:5000/${project_second_name}-${build_id}\"" //打成镜像文件
         } else {
                sh "ssh  root@172.20.20.30 \"docker tag  ${docker_nikename}-${build_id} 172.20.20.30:5000/${docker_nikename}-${build_id}\"" //打成镜像文件
         }
         sh "echo  push container to registry"
         if (project_second_name in ["education-pro-app","education-edu-app"]){
                sh "ssh  root@172.20.20.30 \"docker push 172.20.20.30:5000/${project_second_name}-${build_id}\"" //打成镜像文件
         } else {
                sh "ssh  root@172.20.20.30 \"docker push 172.20.20.30:5000/${docker_nikename}-${build_id}\"" //打成镜像文件
         }
      }

      stage("huawei_test_slave_02-部署") {
         sh "echo  start origin"
      }


      node(jenkins_slave){

        stage("开始部署远程部署"){
            host_neirong = "${ip_hosts}".split("\\|")
            host_len = "${ip_hosts}".split("\\|").size()

            for (int i = 0; i<host_len; i++){
                ip_host =  host_neirong[i].split("#")[1]
                println "后端机器ip地址为 ${ip_host}"

                if (use_docker == "true") {
                sh "echo  start pull container"
                if (project_second_name in ["education-pro-app","education-edu-app"]) {
                        sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker pull 172.20.20.30:5000/${project_second_name}-${build_id}"
                } else {
                        sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker pull 172.20.20.30:5000/${docker_nikename}-${build_id}"
                }
                sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} 'test -d /opt/${gitroot} || mkdir -p /opt/${gitroot}'"

                // 调用yaml文件 截取出宿主机端口和docker容器端口
                owner_port = "${port_des}".tokenize(':')[0]
                container_port =  "${port_des}".tokenize(':')[1]
                if (project_second_name in ["education-pro-app","education-edu-app"]){
                        container_id = sh(script:"sshpass -p 'Ssic@2018' ssh root@${ip_host} \"docker ps | grep ${project_second_name} |grep -v grep| awk '{print \$1}' \"", returnStdout: true).trim()
                        container_id_last = "${container_id}".tokenize(' ')[0]
                } else {
                container_id = sh(script:"sshpass -p 'Ssic@2018' ssh root@${ip_host} \"docker ps | grep ${docker_nikename} |grep -v grep| awk '{print \$1}' \"", returnStdout: true).trim()
                container_id_last = "${container_id}".tokenize(' ')[0]
                }
                try {
                        sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker stop $container_id_last"
                        sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker rm $container_id_last"
                } catch (err) {
                        println "Failled: ${err}"
                        }
                if (project_second_name in ["education-pro-app","education-edu-app"]) {
                        // 改变以前的时间戳格式 改成用build_id
                        //sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker run -itd --name ${docker_nikename}-${project_second_name}-${docker_name} -v /etc/localtime:/etc/localtime:ro -v /etc/hosts:/etc/hosts -p ${owner_port}:${container_port} 172.20.20.30:5000/${docker_nikename}-${docker_name}"
                        sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker run -itd ${start_cmds} --name ${docker_nikename}-${project_second_name} -v /opt/logs:/opt/logs -v /etc/localtime:/etc/localtime:ro -v /etc/hosts:/etc/hosts -p ${owner_port}:${container_port} 172.20.20.30:5000/${project_second_name}-${build_id}"
                } else {
                                //sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker run -itd --name ${docker_nikename}-${docker_name} -v /opt/data_nfs:/opt/data_nfs -v /etc/localtime:/etc/localtime:ro -v /etc/hosts:/etc/hosts -p ${owner_port}:${container_port} 172.20.20.30:5000/${docker_nikename}-${docker_name}"
                                sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker run -itd ${start_cmds} --name ${docker_nikename}  -v /opt/logs:/opt/logs -v /opt/data_nfs:/opt/data_nfs -v /etc/localtime:/etc/localtime:ro -v /etc/hosts:/etc/hosts -p ${owner_port}:${container_port} 172.20.20.30:5000/${docker_nikename}-${build_id}"
                }
                sh "sshpass -p 'Ssic@2018' ssh root@${ip_host} docker ps"
                sh "echo  =====================finish===================="
                } else {
                        origin_path_tmp = "${origin_path}".split("\\|")
                        origin_path_count = "${origin_path}".split("\\|").size()
                        for  (int ii = 0; ii<origin_path_count; ii++){
                        single_path_name = origin_path_tmp[ii]
                        try {
                          //sh "sshpass -p 'Ssic@2018' scp -P 60022 /superking/${package_type}/${env.JOB_BASE_NAME} root@${ip_host}:${single_path_name}"
                          //sh "sshpass -p 'Ssic@2018' ssh -p 60022 root@${ip_host} tar zxvf ${single_path_name}${env.JOB_BASE_NAME} -C ${single_path_name}"

                           if (env_name == "prod" || env_name == "uat") {

                               if ("${ip_host}" == "172.18.12.153") {
                                     sh "sshpass -p 'Ssic@2018' scp -P 60022 /superking/${package_type}/${env.JOB_BASE_NAME} root@${ip_host}:${single_path_name}"
                                     sh "sshpass -p 'Ssic@2018' ssh -p 60022 root@${ip_host} tar zxvf ${single_path_name}${env.JOB_BASE_NAME} -C ${single_path_name}"
                               } else {
                                    sh "sshpass -p '123asd()_L:\"\' scp -P 60022 /superking/${package_type}/${env.JOB_BASE_NAME} root@${ip_host}:${single_path_name}"
                                    sh "sshpass -p '123asd()_L:\"\' ssh -p 60022 root@${ip_host} tar zxvf ${single_path_name}${env.JOB_BASE_NAME} -C ${single_path_name}"
                               }

                   } else {

                     println "99999999999999999999 ${ip_host}"

                      if ("${ip_host}" == "172.20.10.10") {
                           sh "sshpass -p '123asd()_L:\"\' scp -P 60022 /superking/${package_type}/${env.JOB_BASE_NAME} root@${ip_host}:${single_path_name}"
                                   sh "sshpass -p '123asd()_L:\"\' ssh -p 60022 root@${ip_host} tar zxvf ${single_path_name}${env.JOB_BASE_NAME} -C ${single_path_name}"
                      } else {
                           sh "sshpass -p 'Ssic@2018' scp -P 60022 /superking/${package_type}/${env.JOB_BASE_NAME} root@${ip_host}:${single_path_name}"
                                   sh "sshpass -p 'Ssic@2018' ssh -p 60022 root@${ip_host} tar zxvf ${single_path_name}${env.JOB_BASE_NAME} -C ${single_path_name}"
                      }



                   }


                         } catch (err) {
                                 println "Failled: ${err}"
                                }
                        }
                    }
            }
        }
      }
   }


