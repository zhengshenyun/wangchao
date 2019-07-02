#base image
FROM 172.16.128.247:5000/python/flask.0.0.1 
WORKDIR /usr/local

#RUN echo "daemon off;" >> /usr/local/nginx/conf/nginx.conf
ADD app.py /usr/local/app.py
RUN chmod 755 /usr/local/app.py
CMD ["/usr/bin/python","/usr/local/app.py"]
EXPOSE 11111
