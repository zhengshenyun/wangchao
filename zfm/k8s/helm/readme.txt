#!/bin/bash
https://www.cnblogs.com/ding2016/p/10821970.html            ########安装

https://www.cnblogs.com/tylerzhou/p/11136107.html           ########实例


我来总结一下 就是helm只要有一个chart就可以安装了  所以吧软件装到k8s集群里面很简单  亦或者你可以把你的一个整个应用放到一个chart里面 也可以方便管理 只需要一个命令就好了

helm传递参数 ----------------------- 重要   value.yaml --------------------------------

1）借助 envsubst ，假设 values.yaml 中有下面的配置

resources:
  limits:
    cpu: ${LIMITS_CPU}
通过下面的命令就可以实现通过环境变量传值

export LIMITS_CPU=1 && envsubst < values.yaml | helm install cnblogs-web -f - .
2）不用环境变量，通过 helm install 的 --set 参数修改 values.yaml 中的配置。

比如 values.yaml 中有下面的配置

resources:
  limits:
    cpu: 2
通过下面的命令就可以将上面的 cpu 值改为 1

helm install --set resources.limits.cpu=1 cnblogs-web .
