## RDT(Rect`s ADT) v1.1 ##
ADT个人定制版, 实现下面全部内容

## 定制内容 ##
* 自定义AIR主activity的launchMode

		说明：改为默认的launchMode：android:launchMode="standard"

* 去除包名前缀"air."

		说明：默认为设置环境变量“AIR_NOANDROIDFLAIR=true”可去掉，

* 去除android资源国际化限制.

		说明：修改aapt编译参数。

* 彻底解决android资源ID找不到的问题.

		说明：增加资源库复制，输入自定路径可生成相应的路径ID文件。

		具体做法为在Android-ARM下建RDT/RDT.xml。然后配置<resourceid>属性

		详细的请看demo。

* 解决ANE中引用so库调试版发行版不一致的问题.

		说明：统一改为armeabi-v7a下。

* ANE中加入多jar库引用,去除ANE中合并jar的步骤.

		说明：把SDK的所有jar库放到Android-ARM/RDT/下

* ANE中彻底解决资源ID错乱问题.(不是找不到ID,是ID数值偏差,在ANE中偶有发生).

* 可打包任意文件进APK根目录，以应对类似移动MM 联想支付等问题.

		说明：把需要打包进APK根目录的文件放到Android-ARM/ROOT下

* 更改程序风格，实现默认全部activity全屏（适用于游戏项目）

		说明：修改程序<application>默认风格。

* 修改默认raw下的文件,用以解决部分ANE需要在raw文件夹下操作音频文件的内存冲突

* 尽最大限度让AIR for android项目在打包上无限接近原生android项目.

## 配置说明:

把`Anti-ADT/RDT/RDT.bat` 放到 `Adobe Flash Builder 4.6\sdks\AIR3.5\bin 下`

把`Anti-ADT/RDT/RDT.jar` 放到 `Adobe Flash Builder 4.6\sdks\AIR3.5\lib 下`

## 使用说明
* 参照如下命令行：[传送门](lenovo/buildAPK/lenovo_apk.bat)
* 目前只支持：WIN+AIRSDK3.5

## ANE配置说明(以联想为例子)
联想有一堆jar库，这里取消ANE步骤中的合并`jar`；
联想SDK需要把`bin`文件夹打包进去`APK`根目录，这里按照下面配置

* 把所有`jar库`文件放到此处：`lenovo/buildANE/android-ARM/RDT`
* 在`RDT.XML`中配置资源路径：`lenovo/buildANE/android-ARM/RDT/RDT.XML`
* 把`bin`文件夹放到：`lenovo/buildANE/android-ARM/ROOT`下 

## 关于
* 所有案例均以联想SDK做模版
* 关于Rect：[传送门](http://www.shadowkong.com)
