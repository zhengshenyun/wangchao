#!/usr/bin/groovy

def call() {
  def remoteHost = '172.20.20.30'
  def remoteSshPort = 8888
  def remoteUser = 'root'
  def binaryBaseDir = '/opt/binary'


  //jenkins slave上生成的二进制包位置
  def moduleName = "${env.JOB_NAME}".toLowerCase().split('/')[3]
  def localBinaryDir = "${env.WORKSPACE}/${moduleName}/target"

  // 远端二进制包存放路径

  def projectDir = "${env.JOB_NAME}".toLowerCase()
  def binaryDir = "${binaryBaseDir}/${projectDir}"
  println(binaryDir)

  sh (script: "ssh -p ${remoteSshPort} ${remoteUser}@${remoteHost} 'if [ ! -d ${binaryDir} ]; then mkdir -p ${binaryDir};else exit 0;fi'")
  sh (script: "scp -P ${remoteSshPort} ${localBinaryDir}/*.war  ${remoteUser}@${remoteHost}:${binaryDir}")
}
