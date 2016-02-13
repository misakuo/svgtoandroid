##SVG2VectorDrawable
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SVG2VectorDrawable-brightgreen.svg?style=flat)](http://www.android-arsenal.com/details/1/3137)    
[English](https://github.com/misakuo/svgtoandroid/blob/master/readme_en.md)
***
###简介
一个Intellij Platform插件，通过其可以完成从*.svg文件到Android VectorDrawable的转换。  
###特性
 - 图形化界面
 - 自动解析当前Project，如果当前Project为Android Project，则会列出所有Module   
 - 支持将SVG解析为多种dpi的VectorDrawable  
 
###属性对应
|SVG Attribute|VectorDrawable Attribute| 
|:-:|:-:| 
|id|android:name| 
|fill|android:fillColor|
|fill-opacity|android:fillAlpha|
|stroke|android:strokeColor|
|stroke-opacity|android:strokeAlpha|
|stroke-width|android:strokeWidth|
|stroke-linejoin|android:strokeLineJoin|
|stroke-miterlimit|android:strokeMiterLimit|
|stroke-linecap|android:lineCap|

###使用
####安装  
本插件支持Intellij IDEA和Android Studio，需要JDK版本1.6及以上  
#####通过jar文件安装
[从此处](https://github.com/misakuo/svgtoandroid/raw/master/SVG2VectorDrawable.jar) 下载`SVG2VectorDrawable.jar` 文件，从IDE中打开 Preferences -> Plugins -> Install plugin from disk... 选择 SVG2VectorDrawable.jar ，添加后重启IDE即可在工具栏上看到插件图标
#####通过插件仓库安装
在IDE中打开Preferences -> Plugins -> Browse Repositories，搜索SVG2VectorDrawable，安装插件并重启IDE
####图形界面  
![img1](https://raw.githubusercontent.com/misakuo/svgtoandroid/master/imgs/1.png)
####用法
- 点击`···`按钮，选择SVG源文件
- 在第一个下拉选框中选中要生成xml文件的module，在第二个选框中选择生成VectorDrawable对应的分辨率，在module中已存在的分辨率目录为黑色字体，未存在的目录为灰色字体，如果选中不存在的目录，则插件会自动生成该目录  
- 填入生成xml文件的文件名，默认为vector_drawable_ + SVG文件的名称
- 点击`Generate`，插件会生成xml并在编辑器中打开（如果文件名对应的xml已存在，则会覆盖原来的内容）  

###Todos  
- 支持SVG的transform属性

欢迎提交Issue和PR
***
Reference: [svg2vectordrawable](https://github.com/Ashung/svg2vectordrawable)

