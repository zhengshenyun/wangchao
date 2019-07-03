#!/usr/bin/env groovy


def call(){

  def gitCredentialsId = "4b8921a7-77d8-43e4-bf67-5cca98bf7168"

  dir(".") {
    checkout([$class: 'GitSCM',
    branches: [[name: "${env.gitTag}"]],
    extensions: [[$class: 'CloneOption', timeout: 10, depth: 1, noTags: false, reference: '', shallow: true]],
    userRemoteConfigs: [[url: "${env.gitRepo}",credentialsId: "${gitCredentialsId}"]]])
  }
}