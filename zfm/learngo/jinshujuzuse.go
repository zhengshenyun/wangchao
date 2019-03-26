package main

import (
	"fmt"
)

func pro(start <-chan int){
	fmt.Println("1")
	read := <- start
	fmt.Printf("read is %v\n",read)
}

func main() {
	a := make(chan int)    //  这一种是阻塞     a := make(chan int,2)  这一种就不阻塞
	fmt.Printf("is %v\n",a)
	go pro(a)	
	fmt.Printf("is %v\n",a)
	fmt.Println("2")
	a <- 1	
	fmt.Printf("is %v\n",a)
}

