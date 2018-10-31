#base image
FROM centos    #指定所创建镜像的基础镜像 
MAINTAINER from zichen		#指定维护者信息
WORKDIR /usr/local/src		#配置工作目录 下面的所有命令都是在这个目录完成 所以写相对路径就要注意了
RUN yum install -y wget		#运行命令

RUN wget http://nginx.org/download/nginx-1.8.1.tar.gz  
RUN tar -zxvf nginx-1.8.1.tar.gz
WORKDIR nginx-1.8.1      ###配置工作目录 下面的所有命令都是在这个目录完成 所以写相对路径就要注意了
RUN yum -y install gcc-c++ make pcre-devel zlib-devel bind
RUN ./configure --prefix=/usr/local/nginx --with-pcre
RUN make
RUN make install
RUN /usr/local/nginx/sbin/nginx
#RUN echo "daemon off;" >> /usr/local/nginx/conf/nginx.conf
ADD run.sh /usr/local/sbin/run.sh   ##赋值指定的<src>路径下的内容到容器中的<dest>路径下，<src>可以为URL；如果为tar文件，会自动解压到<dest>路径下
RUN chmod 755 /usr/local/sbin/run.sh
CMD ["/bin/bash","/usr/local/sbin/run.sh"]   ##指定启动容器时默认执行的命令
EXPOSE 22 80 433	##声明镜像内服务所监听的端口
