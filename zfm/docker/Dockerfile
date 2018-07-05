#base image
FROM centos 
MAINTAINER from zichen
WORKDIR /usr/local/src
RUN yum install -y wget
RUN wget http://nginx.org/download/nginx-1.8.1.tar.gz
RUN tar -zxvf nginx-1.8.1.tar.gz
WORKDIR nginx-1.8.1
RUN yum -y install gcc-c++ make pcre-devel zlib-devel bind
RUN ./configure --prefix=/usr/local/nginx --with-pcre
RUN make
RUN make install
RUN /usr/local/nginx/sbin/nginx
#RUN echo "daemon off;" >> /usr/local/nginx/conf/nginx.conf
ADD run.sh /usr/local/sbin/run.sh
RUN chmod 755 /usr/local/sbin/run.sh
CMD ["/bin/bash","/usr/local/sbin/run.sh"]
EXPOSE 22 80 433
