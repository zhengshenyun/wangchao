#!/usr/bin/env groovy
manager.addShortText("$env.Branch_name")

node('master') {
            // pull gitcode
    
  wrap([$class: 'BuildUser']) {
      def user_user = env.BUILD_USER_ID
      def project_name = env.Project_name
            currentBuild.description = "${env.CI_CD}--${project_name}--${user_user}"
  }

  stage("开始部署"){
    if(env.CI_CD == "完整发布"){     
       sh "echo start -----------------"
        def    tasks = [:]
        def    len_Project_name = env.Project_name.split(",").size()
        def    list_Project_name = env.Project_name.split(",")
            for (int i = 0; i < len_Project_name; i++) {
                def index = i+0
                def Task_name = list_Project_name[index]
                    tasks["Task ${Task_name}"] = {
                        stage("${Task_name}") { 
                            echo "Task ${Task_name}"
                            sh "echo ${Task_name}"
                            sh "rm -rf /data/jenkins-workspace-tager/${Task_name}/mar_targer"
                            //build(job:"${Task_name}",param1:"${env.Branch_name}")
                            build job: "${Task_name}", parameters: [gitParameter(name: "BRANCH", value: "${env.Branch_name}")]
                        }
                    }
                }
                parallel(tasks)
    }else {
       println "migrate${env.Env}"
       sh "echo start -----------------"
        def    tasks = [:]
        def    len_Project_name = env.Project_name.split(",").size()
        def    list_Project_name = env.Project_name.split(",")
            for (int i = 0; i < len_Project_name; i++) {
                def index = i+0
                def Task_name = list_Project_name[index]
                    sh "mkdir /data/jenkins-workspace-tager/${Task_name}/mar_targer -p"
                    tasks["Task ${Task_name}"] = {
                        stage("${Task_name}") { 
                            echo "Task ${Task_name}"
                            sh "echo ${Task_name}"
                            //start work
                            try {
                                build job: "${Task_name}", parameters: [gitParameter(name: "BRANCH", value: "${env.Branch_name}")]
                                sh "rm -rf /data/jenkins-workspace-tager/${Task_name}/mar_targer"
                            } catch (err) {
                                sh "rm -rf /data/jenkins-workspace-tager/${Task_name}/mar_targer"
                                error "bye bye"
                            }
                        }
                    }
                }
                parallel(tasks)
       // magrate 开关
       //sh "cd /data/jenkins-workspace/${project_name};/usr/local/gradle-4.7/bin/gradle migrate${env.Env}"
       //sh "/usr/bin/ansible-playbook /etc/ansible/${project_name}.yml"
    }
        
    }
}
