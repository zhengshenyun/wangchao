#!/usr/bin/python
#coding=utf8

import commands

def Cmd(cmd):
    resCode,resText1 = commands.getstatusoutput(cmd)
    resText = [i for i in resText1.strip().split("\n")]
    return (resCode,resText)

if __name__ == "__main__":
    Cmd("ls")
