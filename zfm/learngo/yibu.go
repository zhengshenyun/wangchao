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


func startproduct(a chan<- int) {
        for i:=0;i<=10;i++ {
                a <- i
                fmt.Printf("product %d\n",i)
        }
}

func startcustom(a <-chan int) {
        for i:=0;i<=10;i++ {
                b := <- a
                fmt.Printf("custom %d\n",b)
        }
}


func main() {
	//go start()           ################     这样是有序的
	//go start1()          ################     这样是有序的
	//time.Sleep(time.Duration(20)*time.Second)
	//for i:=0;i<=100;i++ {
	//	go start2(i)        ##########   这样的话是无序的
	//}
	//time.Sleep(time.Duration(1)*time.Second)
	c := make(chan int)
        go startproduct(c)
        go startcustom(c)
        time.Sleep(time.Duration(1)*time.Second)

	//以此为分界线 ====================================
	c := make(chan int)    ####################   这种就不需要再像上面那样再time.Sleep了
        go startcustom(c)
        for i:=0;i<=10;i++ {
                c <- i
                fmt.Printf("product %d\n",i)
        }

}
