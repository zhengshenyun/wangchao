package kk

import (
    "fmt"
    "reflect"
)

var RR = "wangchao is a good boy"

type User struct {
    Id   int
    Name string
    Age  int
}

func (u *User) Hello() {
    fmt.Println("Hello world!")
    u.Id = 10
    b := &u.Id
    fmt.Println(b)
}
func Info(o interface{}) {
    t := reflect.TypeOf(o)         //反射使用 TypeOf 和 ValueOf 函数从接口中获取目标对象信息
    fmt.Println("Type:", t.Name()) //调用t.Name方法来获取这个类型的名称

    v := reflect.ValueOf(o) //打印出所包含的字段
    fmt.Printf("所有字段为%#v\n",v)
    fmt.Println("Fields:")
    for i := 0; i < t.NumField(); i++ { //通过索引来取得它的所有字段，这里通过t.NumField来获取它多拥有的字段数量，同时来决定循环的次数
        f := t.Field(i)               //通过这个i作为它的索引，从0开始来取得它的字段
        val := v.Field(i).Interface() //通过interface方法来取出这个字段所对应的值
        fmt.Printf("%6s:%v =%v\n", f.Name, f.Type, val)
    }
    for i := 0; i < t.NumMethod(); i++ { //这里同样通过t.NumMethod来获取它拥有的方法的数量，来决定循环的次数
        m := t.Method(i)
        fmt.Printf("%6s:%v\n", m.Name, m.Type)

    }
}
