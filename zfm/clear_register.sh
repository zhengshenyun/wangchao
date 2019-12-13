#!/bin/bash
for url in `curl -sX GET http://172.20.20.30:5000/v2/_catalog | jq '.repositories|.[]'`;do
	newurl=`echo $url | sed 's/^.//g'| sed 's/.$//g'`
	echo $newurl
	docker exec  32a4a12421a7 rm -rf /var/lib/registry/docker/registry/v2/repositories/$newurl
done
