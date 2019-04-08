package kk

import (
	"fmt"
	"os"
	"bufio"
	"io"
)

func read(a string) {
	b := []rune(a)
	fmt.Println(b)
}

// 一行一行读取文件

func LineRead(filename string) []string {
	f, err := os.Open(filename)
	if err != nil {
		panic(err)
	}
	defer f.Close()

	rd := bufio.NewReader(f)
	resline := []string{}
	for {
		line, err := rd.ReadString('\n') //以'\n'为结束符读入一行

		if err != nil || io.EOF == err {
			break
		}
		fmt.Println(line)
		resline = append(resline,line)
	}
	return resline
}
