package main

import (
    "fmt"
)

//自定义错误类型
type ArithmeticError struct {
    error   //实现error接口
}

//重写Error()方法
func (this *ArithmeticError) Error() string {
    return "自定义的error,error名称为算数不合法"
}

//定义除法运算函数
func Devide(num1, num2 int) (rs int, err error) {
    if num2 == 0 {
        //return 0, &ArithmeticError{}
        return 0, ArithmeticError{}.error
    } else {
        return num1 / num2, nil
    }
}
func main() {
    var a, b int
    fmt.Scanf("%d %d", &a, &b)

    rs, err := Devide(a, b)
    if err != nil {
        fmt.Println(err)
    } else {
        fmt.Println("结果是：", rs)
    }
}
