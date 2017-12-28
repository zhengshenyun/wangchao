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

function sscurl {
for i in `cat yuming.txt`;do
    #echo "-----------------正在ping $ip.$i "
    {
    Rcode=`curl -o /dev/null --connect-timeout 2 -m 3  -s -w %{http_code} $i`
    if (($Rcode==200));then
        echogreen "$i   是正常的 返回码是 $Rcode"
    else
        echored "$i   是不正常的 返回码是 $Rcode"
    fi
    } &
done
}

sscurl
wait
echo "=============================================over=============================================="
