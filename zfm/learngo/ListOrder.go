package main

import (
	"fmt"
	"time"
)

func Read(a []int) {
	for _,v := range a {
		fmt.Printf("i am Read %d\n",v)
	//	time.Sleep(time.Second)
	}
}

func Write(a []int) {
	for _,v := range a {
		fmt.Printf("i am Write %d\n",v)
		//time.Sleep(time.Second)
	}
}


func Stringjiexi(a string) {
	for _,i1 := range []byte(a) {
                fmt.Printf("%s\n",string(i1))
        }
}

func Stringhanzi(a string) {
	for _,ii := range a {
                fmt.Printf("%s\n",string(ii))
        }    
}

func main() {
	Stringjiexi("fuck you")
	Stringhanzi("你大爷的")
	a := []int{}
	b := []int{}
	for i:=1;i<=50;i++ {
		a = append(a,i)
	}
	for ii:=51;ii<=100;ii++ {
		b = append(b,ii)
}
	go Read(a)
	go Write(b)
	time.Sleep(time.Duration(2)*time.Second)
}	
