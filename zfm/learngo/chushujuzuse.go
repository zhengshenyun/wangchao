package main

import (
	"fmt"
)

func pro(start chan<- int){
	fmt.Println("1")
	start <- 10
}

func main() {
	a := make(chan int,2)
	fmt.Printf("is %v\n",a)
	go pro(a)		// goroute  一定要紧接着上面  如果在b:=< a  这样就不行  你想啊 已经阻塞了  就没有协程概念了	
	fmt.Printf("is %v\n",a)
	fmt.Println("2")
	b := <- a	
	fmt.Printf("res is %v\n",b)
}
