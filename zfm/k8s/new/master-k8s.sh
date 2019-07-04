#!/bin/bash

##this is a create k8s-master

###### 初始化系统一些参数

function chushihua {
	# 所有主机：基本系统配置 
	# 关闭Selinux/firewalld
	systemctl stop firewalld
	systemctl disable firewalld
	setenforce 0
	sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
	 
	# 关闭交换分区
	swapoff -a
	#yes | cp /etc/fstab /etc/fstab_bak
	#cat /etc/fstab_bak |grep -v swap > /etc/fstab
	 
	# 设置网桥包经IPTables，core文件生成路径
	echo """
vm.swappiness = 0
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
""" >> /etc/sysctl.conf
	modprobe br_netfilter
	sysctl -p
	sleep 2
	echo "----------------------------初始化系统参数完成---------------------------"
	sleep 1
}

###### 安装docker

function startdocker {
	yum install -y yum-utils device-mapper-persistent-data lvm2
	yum-config-manager \
    	--add-repo \
   	 https://download.docker.com/linux/centos/docker-ce.repo
	yum install -y --setopt=obsoletes=0 \
  	docker-ce-18.09.4-3.el7
	systemctl start docker
	systemctl enable docker
	mkdir /etc/docker
	cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2",
  "storage-opts": [
    "overlay2.override_kernel_check=true"
  ]
}
EOF
	mkdir -p /etc/systemd/system/docker.service.d
	systemctl daemon-reload
	systemctl restart docker
	sleep 2
	echo "------------------------------docker安装完成------------------------------"
	sleep 1
}


###### 安装k8s组件
function startk8smaster {
	cat <<EOF > /etc/yum.repos.d/kubernetes.repo   ######### cat EOF 模式
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
	yum install -y kubelet-1.14.0 kubeadm-1.14.0 kubectl-1.14.0
	sleep 2
        echo "------------------------------k8s基本软件安装完成------------------------------"
        sleep 1
}

######    安装k8s的一些镜像

function anzhuangimg {
	KUBE_VERSION=v1.14.0
	KUBE_PAUSE_VERSION=3.1
	ETCD_VERSION=3.3.10
	DNS_VERSION=1.3.1
	username=registry.cn-hangzhou.aliyuncs.com/google_containers
	images=(
	        kube-proxy-amd64:${KUBE_VERSION}
	        kube-scheduler-amd64:${KUBE_VERSION}
	        kube-controller-manager-amd64:${KUBE_VERSION}
	        kube-apiserver-amd64:${KUBE_VERSION}
	        pause:${KUBE_PAUSE_VERSION}
	        etcd-amd64:${ETCD_VERSION}
	        coredns:${DNS_VERSION}
	        )
	for image in ${images[@]}
	do
	NEW_IMAGE=`echo ${image}|awk '{gsub(/-amd64/,"",$0);print}'`
	echo ${NEW_IMAGE}
	docker pull ${username}/${image}
	docker tag ${username}/${image} k8s.gcr.io/${NEW_IMAGE}
	docker rmi ${username}/${image}
	done
	systemctl enable kubelet.service
	######  下面2行  2个ip  一定要看清  不同的机器不一样
	kubeadm init   --kubernetes-version=v1.14.0   --pod-network-cidr=10.244.0.0/16  --apiserver-advertise-address=10.254.253.20  --token-ttl 0  --ignore-preflight-errors=Swap
	kubeadm join 10.254.253.20:6443 --token n0kt4i.i60jw3d7veicguux --discovery-token-ca-cert-hash sha256:ef6c423dd699d3a0cd0620ba466573d9764f59d038377fc02e23cd71cd67034f
	mkdir -p $HOME/.kube
	cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
	chown $(id -u):$(id -g) $HOME/.kube/config
	###### 下面那个yaml文件是有的  现在暂且用这样
	kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/a70459be0084506e4ec919aa1c114638878db11b/Documentation/kube-flannel.yml
}
chushihua
startdocker
startk8smaster
anzhuangimg
