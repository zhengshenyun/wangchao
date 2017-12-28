##!/bin/bash
#########################################################################
# File Name: ping.sh
# Author: wangchao
# mail: 
# Created Time: 2017-10-27 21:07:36
#########################################################################

echored ()
{         
    echo -ne "\033[31m"$1"\033[0m\n"
}         
echogreen ()
{         
    echo -ne "\033[32m"$1"\033[0m\n"
}         
echoyellow ()
{         
    echo -ne "\033[33m"$1"\033[0m\n"
}


ip="172.16.0"

function ssping(){
for i in `seq 1 254`;do
    #echo "-----------------正在ping $ip.$i "
    {
    ping -c 2 -w 5 $ip.$i>/dev/null
    if (($?==0));then
        echogreen "$ip.$i   是正常的"
    else
        echored "$ip.$i   是不正常的"
    fi
    } &
done
}

ssping
wait
echo "=============================================over=============================================="
