package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
	"encoding/json"
)

type num1 struct {
	Code int
	Data num2	
}

type num2 struct {
	Ip	string
	Country	string
	Area	string
	Region	string
	City	string
	County	string
	Isp	string
	Country_id	string
	Area_id	string
	Region_id string
	City_id string
	County_id string
	Isp_id string
}

func main(){
	client := &http.Client{}
	url := "http://ip.taobao.com/service/getIpInfo.php?ip=222.67.228.195"
    	reqest, err := http.NewRequest("GET",url, nil)
	if err != nil {
		fmt.Println("错误")
	}
	response,err := client.Do(reqest)
	fmt.Println(response.StatusCode,err)
	if response.StatusCode != 200 {
		fmt.Println("请求失败")
	} else {
		body, _ := ioutil.ReadAll(response.Body)
		var num1_ok1 num1
		fmt.Println(string(body))
		err := json.Unmarshal(body,&num1_ok1)
		if err != nil {
        		fmt.Println("error:", err)
    		}
		fmt.Println(num1_ok1)
	}	
}

/*--------------------------------------------
jsoniter.Get    简单的解析json
simpleJson          #############这个比较实用*/

--------------------------------------------------
func main(){
	client := &http.Client{}
	url := "http://ip.taobao.com/service/getIpInfo.php?ip=3.3.3.3"
    	reqest, err := http.NewRequest("GET",url, nil)
	if err != nil {
		fmt.Println("错误")
	}
	response,err := client.Do(reqest)
	fmt.Println(response.StatusCode,err)
	if response.StatusCode != 200 {
		fmt.Println("请求失败")
	} else {
		body, _ := ioutil.ReadAll(response.Body)
		var ffuck fuckyou
		fmt.Println(string(body))
		err := json.Unmarshal(body,&ffuck)
		if err != nil {
        		fmt.Println("error:", err)
    		}
		//fmt.Println(ffuck)
		bbb := ffuck.(map[string]interface{})                     //  这种也是可以的
		fmt.Printf("%v\n",ffuck.(map[string]interface{}))	  //  这种也是可以的
		for k,v := range bbb["data"].(map[string]interface{}) {   //  这种也是可以的
			//fmt.Printf("k是 %v   v是 %v\n",k,v)
				fmt.Printf("k是 %v   v是 %v\n",k,v)
		}
		
	}	
}

