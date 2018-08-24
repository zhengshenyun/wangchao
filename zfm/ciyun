#!/usr/bin/python
#encoding=utf8
import jieba
import matplotlib as mpl
mpl.use('Agg')
import matplotlib.pyplot as plt
from wordcloud import WordCloud
import io
#定义一个空字符串
final = ""
#文件夹位置
filename = r"./nidaye.txt"
 
#打开文件夹，读取内容，并进行分词
data_file = io.open(filename, "r",encoding="utf8")
for line in data_file.readlines():
    word = jieba.cut(line)
    for i in word:
        final = final + i +" "
#        print(final)
data_file.close()

word_pic = WordCloud(font_path = r'/usr/share/fonts/lyx/msyh.ttc',scale=1.5,collocations=False,width=5000,height=4000).generate(final)
#word_pic = WordCloud(width = 2000,height = 1000).generate(final)
plt.imshow(word_pic)
#去掉坐标轴
plt.axis('off')
#保存图片到相应文件夹
plt.savefig(r'./6.png',dpi=600)   #dpi越大越清晰 但是消耗资源越多
