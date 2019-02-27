package main

import (
	"fmt"
	"time"
)

func start() {
	for i:=0;i<=100;i++ {            
		fmt.Printf("start for %d\n",i)
		time.Sleep(time.Duration(1)*time.Second)
	}
}

func start1() {
	for i:=0;i<=100;i++ {
		fmt.Printf("start1 for %d\n",i)
	}
}


func start2(a int) {
	fmt.Printf("start1 for %d\n",a)
}

func main() {
	//go start()           ################     这样是有序的
	//go start1()          ################     这样是有序的
	//time.Sleep(time.Duration(20)*time.Second)
	for i:=0;i<=100;i++ {
		go start2(i)        ##########   这样的话是无序的
	}
	time.Sleep(time.Duration(1)*time.Second)
}
