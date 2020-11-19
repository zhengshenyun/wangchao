package main

import (
    "crypto/md5"
    "fmt"
    "html/template"
    "io"
    "log"
    "net/http"
    "os"
    "strconv"
    "time"
)

var uploadTemplate = template.Must(template.ParseFiles("index.html"))

func indexHandle(w http.ResponseWriter, r *http.Request) {
	if err := uploadTemplate.Execute(w, nil); err != nil {
		log.Fatal("Execute: ", err.Error())
		return
	}
}


func upload(w http.ResponseWriter, r *http.Request) {
    fmt.Println("method:", r.Method)
    if r.Method == "GET" {
        //begin这里开始计算一个时间戳用于填充到模板中的隐藏标签中
        currenttime := time.Now().Unix()
        h := md5.New()
        io.WriteString(h, strconv.FormatInt(currenttime, 10))
        token := fmt.Sprintf("%x", h.Sum(nil))
        //end
        t, _ := template.ParseFiles("upload.html")
        t.Execute(w, token)
    } else {
        r.ParseMultipartForm(32 << 20)
        file, handler, err := r.FormFile("uploadfile")
        if err != nil {
            fmt.Println(err)
            return
        }
        defer file.Close()
        fmt.Fprintf(w, "%v", handler.Header)
        f, err := os.OpenFile("/data/download/superking/"+handler.Filename, os.O_WRONLY|os.O_CREATE, 0666)
        if err != nil {
            fmt.Println(err)
            return
        }
        defer f.Close()
        io.Copy(f, file)
    }
}
func main() {
    http.HandleFunc("/", indexHandle)
    http.HandleFunc("/upload", upload)       //设置访问的路由
    err := http.ListenAndServe(":8021", nil) //设置监听的端口
    if err != nil {
        log.Fatal("ListenAndServe: ", err)
    }
}

