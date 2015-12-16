##SVG2VectorDrawable
[中文](https://github.com/misakuo/svgtoandroid/blob/master/readme.md)
***
###About
An plugin to Intellij Platform, using it to convert svg file to android VerctorDrawable.  
 
###Attributes Contrast
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

###Useage
####Install  
This plugin is supporting to Intellij IDEA and Android Studio, need JDK1.6 and higher.    
#####Installing from jar file
[Click to download](https://github.com/misakuo/svgtoandroid/raw/master/SVG2VectorDrawable.jar) file `SVG2VectorDrawable.jar` , open `Preferences -> Plugins -> Install plugin from disk...` in IDE, choosing `SVG2VectorDrawable.jar` , you can find plugin's icon in toolbar after restart IDE.  
#####Installing from plugin repo
Open `Preferences -> Plugins -> Browse Repositories` in IDE, searching `SVG2VectorDrawable`, install and restart IDE.
####GUI  
![img1](https://raw.githubusercontent.com/misakuo/svgtoandroid/master/imgs/1.png)
####useage
- Click button`···`, choosing a svg file;  
- choosing module that you want to generating xml in first combobox, choosing dpi in second combobox, if the dpi dir (like `drawable-xxhdpi`) is already exists, the fontcolor in combox is BLACK ,else the fontcolor is GRAY. If choosed dir not exists, plugin will creating the dir;    
- Inputing file name for xml, default is `vector_drawable_ + ${svgName}.xml`
- Click`Generate`, plugin will generating xml and open it on editor(if xml file existed before generating,the content will be overwitten)   
 
###Todos  
- supporting `transform` attr of SVG

***
Welcome to commit issue & PR :)

