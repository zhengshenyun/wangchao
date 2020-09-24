userNameList=(hjmrunning) ;

for userName in ${userNameList[*]};  

 do    

   #echo $userName '  ALL=(ALL)      NOPASSWD: ALL,!/bin/su' > /etc/sudoers.d/$userName ;
   echo $userName '  ALL=(ALL)      NOPASSWD: ALL' > /etc/sudoers.d/$userName ;

   more /etc/sudoers.d/$userName ;   

 done

