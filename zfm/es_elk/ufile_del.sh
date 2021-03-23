#!/bin/bash
 
startdate="2020-10-01"
date1=$(date -d "2020-10-01" "+%s")
date2=$(date -d "2021-02-22" "+%s")
date_count=$(echo "$date2 - $date1"|bc)
day_m=$(echo "$date_count"/86400|bc)
for ((sdate=0;sdate<"$day_m";sdate++))
do
   pt_day=$(date -d "$startdate $sdate days" "+%F")
   echo $pt_day
	for i in `cat nginx_sss`;do
   	curl -X DELETE http://10.42.71.37:9200/_snapshot/ues_snapshot/$i$pt_day
	#echo $i
  done
done
