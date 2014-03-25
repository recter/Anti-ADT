## RDT(Rect`s ADT) v1.2 ##
ADT个人定制版, 支持AIR4.0

## 定制内容 ##
* 自定义AIR主activity的launchMode

		说明：改为默认的launchMode：android:launchMode="standard"

* 去除包名前缀"air."

		说明：默认为设置环境变量“AIR_NOANDROIDFLAIR=true”可去掉，

* 去除android资源国际化限制.

		说明：修改aapt编译参数。

* 彻底解决android资源ID找不到的问题.

		AIR4.0中官方已经解决这个问题,所以RDT去掉此功能!


* ANE中加入多jar库引用,去除ANE中合并jar的步骤.

		说明：把SDK的所有jar库放到Android-ARM/RDT/下

* ANE中彻底解决资源ID错乱问题.(不是找不到ID,是ID数值偏差,在ANE中偶有发生).

* 可打包任意文件进APK根目录，以应对类似移动MM 联想支付等问题.

		说明：把需要打包进APK根目录的文件放到Android-ARM/ROOT下

* 更改程序风格，实现默认全部activity全屏（适用于游戏项目）

		说明：修改程序<application>默认风格。

* 尽最大限度让AIR for android项目在打包上无限接近原生android项目.

## 配置说明:

把`Anti-ADT/RDT4.0/RDT.bat` 放到 `Adobe Flash Builder 4.6\sdks\AIR4\bin 下`

把`Anti-ADT/RDT4.0/RDT4.jar` 放到 `Adobe Flash Builder 4.6\sdks\AIR4\lib 下`

## 使用说明
* 支持：WIN+Mac+AIRSDK4.0

## ANE配置说明(以联想为例子)
联想有一堆jar库，这里取消ANE步骤中的合并`jar`；
联想SDK需要把`bin`文件夹打包进去`APK`根目录，这里按照下面配置

* 把所有`jar库`文件放到此处：`lenovo/buildANE/android-ARM/RDT`
* 在`RDT.XML`中配置资源路径：`lenovo/buildANE/android-ARM/RDT/RDT.XML`
* 把`bin`文件夹放到：`lenovo/buildANE/android-ARM/ROOT`下 

## 关于
* 所有案例均以联想SDK做模版
* 关于Rect：[传送门](http://www.shadowkong.com)
