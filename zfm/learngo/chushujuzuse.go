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
	go pro(a)	
	fmt.Printf("is %v\n",a)
	fmt.Println("2")
	b := <- a	
	fmt.Printf("res is %v\n",b)
}
