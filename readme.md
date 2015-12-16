##SVG2VectorDrawable
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
###用法
