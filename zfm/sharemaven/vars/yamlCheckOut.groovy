#!/usr/bin/env groovy


def call(body) {
  
  def gitCredentialsId = "4b8921a7-77d8-43e4-bf67-5cca98bf7168"
  checkout([$class: 'GitSCM', 
  branches: [[name: '*/master']], 
  userRemoteConfigs: [[credentialsId: "${gitCredentialsId}", url: 'https://git.tfit.com/cicd/yaml.git']]])
}
