package kk

import (
   _	"fmt"
	"errors"
   _ 	"reflect"
)


type Leixingcar interface{
	Kai(s string) (a string,err error)
}

type Newcarer struct {
	chepai int
	leixing string
	price int
	chezhu	string
}

func (self *Newcarer)Kai(chezhuname string) (a string,err error) {
	if len([]rune(chezhuname)) >3 {
		self.chezhu = chezhuname
		a = self.chezhu
		err = nil
		return
	}else {
		a = chezhuname
		err = errors.New("车主名称写错了")
		return
	}
}

/*func main() {
	a := Newcarer{}
	aa,err := a.Kai("王")
	fmt.Println(aa,err)
	var f Leixingcar
	f = &Newcarer{}
	fmt.Println(f.Kai("王超"))
}*/
