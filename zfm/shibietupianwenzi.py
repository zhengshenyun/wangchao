#!/usr/bin/python
#encoding=utf8

#pip install Pillow
#pip install pytesser3
#pip install pytesseract
#yum -y install tesseract

import pytesseract 
from PIL import Image 
  
__author__ = 'admin' 
  
im = Image.open(r'./sssss.png') 
print(pytesseract.image_to_string(im))

#===========================>  如果是中文的需要加载识别中文的字体
