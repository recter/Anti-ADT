
aapt 工具解析  
by Rect
github.com/recter  
www.shadowkong.com  



无资源国际化:
aapt package -f -m -J out -S res -I D:\ANE\android\android-2.1_r01-windows\platforms\android-17\android.jar -M AndroidManifest.xml

加了-z之后强制资源国际化:
aapt package -f -m -z -J out -S res -I D:\ANE\android\android-2.1_r01-windows\platforms\android-17\android.jar -M AndroidManifest.xml 
 
打包command.txt文件进anime.apk中:
aapt a anime.apk command.txt