package main 

import (
	"fmt"
	"kk"
)


func main() {
	resline := kk.LineRead("ff")
	fmt.Println(resline)
	for _,v := range resline {
		fmt.Printf("res is %v",v)	
	}
}
