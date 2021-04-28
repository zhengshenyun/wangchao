#!/usr/bin/env groovy
node('test-cocos') {
    stage('delete old dir') {
        bat "cd /d D:/JenkinsNode && rd /s /q  aha-cocos-build-tools"
        bat "cd /d D:/JenkinsNode && rd /s /q ${Project_name_02}"
    }
}
node("master") {
    stage("拉取代码") {
    def Git_url_obj = readYaml file: '/var/jenkins_home/git_data/COCOS-NEW/test_all_cocos_pipeline.yaml'
    def Git_url = Git_url_obj.Env.test.project."${Project_name_02}".giturl
    dir("/var/jenkins_home/workspace/cocos-new/${Project_name}") {
            git  credentialsId: '10086', url: "git@git.util.ahaschool.com:ahakid/aha-cocos-build-tools.git", branch: env.Tool_branch_name
          }
    sh "rm -rf /var/jenkins_home/workspace/cocos-new/aha-cocos-build-tools/.git"
    dir("/var/jenkins_home/workspace/cocos-new/${Project_name_02}") {
            git  credentialsId: '10086', url: Git_url, branch: env.Branch_name_02
          }
    }
    sh "rm -rf /var/jenkins_home/workspace/cocos-new/${Project_name_02}/.git"
    stage("远程传递代码") {
        sh "ssh hjmrunning@172.20.0.247 \"sshpass -p 'Rs2eU7udgfy3d' scp -r /data/jenkins/workspace/cocos-new/${Project_name} Administrator@172.20.0.129:D:/JenkinsNode\""
        sh "ssh hjmrunning@172.20.0.247 \"sshpass -p 'Rs2eU7udgfy3d' scp -r /data/jenkins/workspace/cocos-new/${Project_name_02} Administrator@172.20.0.129:D:/JenkinsNode\""
    }
}

node("test-cocos") {
    stage("开始编译") {
        bat "java -version"
        bat "cd /d D:/JenkinsNode/aha-cocos-build-tools && npm install && node ./build/build.js ${Project_name_02} ${Is_not_debug} ${env.BUILD_NUMBER} ${Is_input_ahacocos}"
    }
}
node("master") {
    stage("拉取包"){
       sh "ssh hjmrunning@172.20.0.247 \"sshpass -p 'Rs2eU7udgfy3d' scp -r Administrator@172.20.0.129:D:/JenkinsNode/aha-cocos-build-tools/OutPackage/${Project_name_02}/${env.BUILD_NUMBER}.zip /data/jenkins/workspace/cocos-new-zip/\""
    }
    stage("push package") {
        sh "ssh hjmrunning@172.20.0.247 scp /data/jenkins/workspace/cocos-new-zip/${env.BUILD_NUMBER}.zip ${Env_name}:/opt/hjm/gamepbl/current/"
        sh "ssh hjmrunning@172.20.0.247 ansible ${Env_name} -m shell -a \\\"unzip -o /opt/hjm/gamepbl/current/${env.BUILD_NUMBER}.zip -d /opt/hjm/gamepbl/current/\\\""
    }
    stage("info") {
        package_name = "${Env_name}"
        url_name = package_name.split('_')[0]
        println "大包:https://game-${url_name}.d.ahaschool.com/${env.BUILD_NUMBER}.zip"
        println "小包:https://game-${url_name}.d.ahaschool.com/${Project_name_02}/aha-${Project_name_02}-${env.BUILD_NUMBER}.zip"
    }
}
