#!/usr/bin/env groovy


def call(arg,args1){

  def gitCredentialsId = "4b8921a7-77d8-43e4-bf67-5cca98bf7168"

  dir(".") {
    checkout([$class: 'GitSCM',
    branches: [[name: "${args1}"]],
    extensions: [[$class: 'CloneOption', timeout: 10, depth: 1, noTags: false, reference: '', shallow: true]],
    userRemoteConfigs: [[url: "${arg}",credentialsId: "${gitCredentialsId}"]]])
  }
}

