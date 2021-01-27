#!/bin/bash

function es_clean() {
	del_date=`date -d "15 day ago" +"%Y-%m-%d"`
	date1="$1 00:00:00"
	date2="$del_date 00:00:00"
	t1=`date -d "$date1" +%s`
	t2=`date -d "$date2" +%s`
	if [ $t1 -le $t2 ]; then
		echo "$1时间早于$del_date，删除"
		#format_date=`echo $1| sed 's/-/\./g'`
		curl -XDELETE http://10.10.11.134:9200/jaeger-*-$1 -u elastic:F5G5ds78gugy
	fi
}
curl -s http://10.10.11.134:9200/_cat/indices?pretty -u elastic:F5G5ds78gugy | grep -P '[0-9]{4}-.*'| awk '{print $3}' | awk -F "-" '{print  $(NF-2)"-"$(NF-1)"-"$NF}'|sort | uniq | while read line;
	do	es_clean $line
done
