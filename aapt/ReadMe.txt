aapt 工具解析
by Rect
github.com/recter 
www.shadowkong.com  
2013/11/39


> p[package]:aapt编译资源头命令;
> 
> -d:包括一个或多个设备资源,由逗号分隔;
> 
> -f:覆盖现有的文件命令,加上后编译生成直接覆盖目前已经存在的R.java;
> 
> -m:使生成的包的目录放在-J参数指定的目录;
> 
> -u:更新现有的包 u = update;
> 
> -v:详细输出,加上此命令会在控制台输出每一个资源文件信息,R.java生成后还有注释,好人道的命令;
> 
> -x:创建扩展资源ID;
> 
> -z:需要本地化的资源属性标记定位,这个命令就是导致写ANE过程中需要资源国际化的罪魁祸首,在ADT中打包android资源恰恰加了这个命令
> 
> -M:AndroidManifest.xml的路径;
> 
> -0:指定一个额外的扩展等,. apk文件将不会存储压缩。之前有朋友发现打包音乐文件在ANE被压缩一次 在APK又被压缩一次,正是由于没指定额外文件所导致,不知道那个朋友现在有没解决这个问题.若修改编译资源文件 则估计他的那个ANE音乐文件就可以用了.
> 
> -g:制定像素迫使图形的灰度,这个命令不是很理解;
> 
> -j:指定包含一个jar或zip文件包,这个命令很特别 深入研究应该能应用到ANE中;
> 
> –debug-mode:指定的是调试模式下的编译资源;
> 
> –min-sdk-versopm VAL:最小SDK版本  如是7以上 则默认编译资源的格式是 utf-8,是不是可以这样理解 如果不是7以上 则不是utf-8,这是不是可以解释为什么在-app.xml中定义的android参数中不可以有中文的原因?看到这个命令 当时遇到的问题还历历在目;
> 
> –target-sdk-version VAL:在androidMainfest中的目标编译SDK版本;
> 
> –app-version VAL:应用程序版本号;
> 
> –app-version-name TEXT:应该程序版本名字;
> 
> –custom-package VAL:生成R.java到一个不同的包中,这个命令可以解决ANE资源去不到的问题,如果加上了则可以去掉我编写的ANE教程中处理资源的所有过程.;
> 
> –rename-mainifest-package PACKAGE:修改APK包名的选项;
> 
> –rename-instrumentation-target-package PACKAGE:重写指定包名的选项;
> 
> –utf16:资源编码修改为更改默认utf – 16编码;
> 
> –auto-add-overlay:自动添加资源覆盖
> 
> –max-res-version:最大资源版本
> 
> -I:指定的SDK版本中android.jar的路径
> 
> -A:assert文件夹的路径；
> 
> -G:一个文件输出混淆器选项,后面加文件逗号隔开.
> 
> -P:指定的输出公共资源,可以制定一个文件 让资源ID输出到那上面;
> 
> -S:指定资源目录 一般是 res
> 
> -F:指定把资源输出到 apk文件中
> 
> -J:指定R.java输出的路径
> 
> raw-file-dir:附加打包进APK的文件,在移动MM的ANE中可以使用这个命令来把资源打包进去,这样就省去了繁杂的一堆命令操作.
## 无资源国际化
aapt package -f -m -J out -S res -I D:\ANE\android\android-2.1_r01-windows\platforms\android-17\android.jar -M AndroidManifest.xml

## 资源国际化
aapt package -f -m -z -J out -S res -I D:\ANE\android\android-2.1_r01-windows\platforms\android-17\android.jar -M AndroidManifest.xml 
 
## 打包command.txt进去APK
aapta anime.apk command.txt
