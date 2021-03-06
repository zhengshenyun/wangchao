package main
 
import (
    "fmt"
    "net/http"
    "reflect"
    "strings"
)
 
func hello(w http.ResponseWriter, req *http.Request) {
    w.Write([]byte("Hello"))
}
 
type Handlers struct {
}
 
func (h *Handlers) ResAction(w http.ResponseWriter, req *http.Request) {
    fmt.Println("res")
    w.Write([]byte("res"))
}
 
func say(w http.ResponseWriter, req *http.Request) {
    pathInfo := strings.Trim(req.URL.Path, "/")
    fmt.Println("pathInfo:", pathInfo)
 
    parts := strings.Split(pathInfo, "/")
    fmt.Println("parts:", parts)
 
    var action = "ResAction"
    fmt.Println(strings.Join(parts, "|"))
    if len(parts) > 1 {
        fmt.Println("22222222")
        action = strings.Title(parts[1]) + "Action"
    }
    fmt.Println("action:", action)
    handle := &Handlers{}
    controller := reflect.ValueOf(handle)
    method := controller.MethodByName(action)
    r := reflect.ValueOf(req)
    wr := reflect.ValueOf(w)
    method.Call([]reflect.Value{wr, r})
}
 
func main() {
//    http.HandleFunc("/hello", hello)
    http.Handle("/handle/", http.HandlerFunc(say))
    fmt.Println("现在开始启动http框架")
    http.ListenAndServe(":8001", nil)
    //select {} //阻塞进程
}
