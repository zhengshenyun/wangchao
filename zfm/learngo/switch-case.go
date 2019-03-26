-----------------------------------这是一种-----------------
switch i { 
    case 0: 
        fmt.Printf("0") 
    case 1: 
        fmt.Printf("1") 
    case 2: 
        fallthrough 
    case 3: 
        fmt.Printf("3") 
    case 4, 5, 6: 
        fmt.Printf("4, 5, 6") 
    default: 
        fmt.Printf("Default") 
} 
-----------------------------------这又是一种-----------------------
switch { 
    case 0 <= Num && Num <= 3: 
        fmt.Printf("0-3") 
    case 4 <= Num && Num <= 6: 
        fmt.Printf("4-6") 
    case 7 <= Num && Num <= 9: 
        fmt.Printf("7-9") //http://www.cnblogs.com/osfipin/
} 
