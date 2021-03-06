package main

import "fmt"

type Reader interface {
    Read() int
}

type MyStruct struct {
    X Reader
    Y int
}

type Foo struct {
    a, b int
}

func (f *Foo) Read() int {
    return f.a + f.b
}

func main() {
    ms := &MyStruct{}        //######### 接口接受方法  一定要是内存地址  才能后面调用方法(类似于python的方法)  struct里面有接口类型 则一定要加上& 才能调用struct里面的接口
    fmt.Printf("%T %v\n", ms, ms)

    ms.Y = 3
    fmt.Printf("%T %v\n", ms, ms)

    ms.X = &Foo{8, 9}      //  只有接口实现了struct的方法  或者是空接口 这个接口才可以接受那个struct的初始化 
    fmt.Printf("%T %v\n", ms, ms)
    fmt.Printf("%T %v\n", ms.X, ms.X)
    fmt.Printf("%T %v\n", ms.X.Read(), ms.X.Read())
}
