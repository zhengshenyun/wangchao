// Build History 显示分支或者Tag
manager.addShortText("$env.GIT_BRANCH")


node('jenkins_slave_01') {

  def mvnHome = tool 'MAVEN'
  def jdkHome = tool 'JAVA_HOME'
  env.PATH = "${jdkHome}/bin:${mvnHome}/bin:${env.PATH}"

  def project_name = "${env.JOB_NAME}".toLowerCase().split('/')[2]

  if (ROLLBACK == "TRUE") {
    pass
  }


  else {
    if (ACTION == "only_build") {
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

    if (ACTION == "only_deploy") {
      stage("仅部署"){
        sh "echo '选择动作为：#################仅仅部署###############'\n"
      }
      stage("容器部署"){
        deployContainer()
      }
    }

    if (ACTION == "deploy"){
      stage("发布开始"){
        sh "echo '选择动作为：#################构建发布部署###############'\n"
      }
      stage("拉取代码") {
        gitCodeCheckout()
      }
      stage("编译构建") {
        mvnPackage()

        if (project_name in ["itration-education-service"]) {
                println("构建的是非容器 不需要构建镜像")
        }
        else {
                stage("构建镜像") {
                dockerBuild()
                }
                stage("容器部署"){
                deployContainer()
                }
        }
      }
    }
  }
}

