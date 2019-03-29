package main

import (
	"errors"
	"fmt"
	"kk"
)

type wanwan struct {
	name   string
	old    int
	sex    string
	school map[int]int
}

func (s *wanwan) Shangjinianji(banji, nianji string) (school string, err error) {
	s.name = "王超"
	school = "王超" + banji + nianji
	if len([]rune(nianji)) > 1 {
		err = nil
	} else {
		err = errors.New("你的语法有错误")
	}
	return
}


func Shangxuele(jinianji int, name *kk.User) {
	fmt.Printf("%v---开始上学了---他的年级是----%v\n",name,jinianji)
	name.Hello()	
}

func main() {
	initschool := map[int]int{}
	start1 := wanwan{"没有名字", 1, "没有性别", initschool}
	fmt.Printf("初始化的时候为 %v\n", start1)
	res, err := start1.Shangjinianji("含山一中二班", "含山一中三年级")
	if err != nil {
		fmt.Printf("您输入的参数有误 %v\n", err)
		return
	}
	fmt.Printf("名字有了 %v\n", res)
	//Shangxuele(4,"王张三")
	importstart := &kk.User{1,"王超大爷",28}
	Shangxuele(4,importstart)
	fmt.Printf("%v\n",kk.RR)
	carcar := kk.Newcarer{}
	resres,err := carcar.Kai("王")
	if err != nil {
		fmt.Println(err)
		//return
	}else {
		fmt.Println(resres)
	}
	fmt.Println("--------------------")
	var sss kk.Leixingcar
	sss = &kk.Newcarer{}
	resresres,err := sss.Kai("猪宝宝就是我儿子啊")
	if err != nil {
                fmt.Println(err)
                //return
        }else {
                fmt.Println(resresres)
        }
}
