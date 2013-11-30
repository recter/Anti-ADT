Anti-ADT
========

分析ADT原理用到的测试文件与demo

## 解密ADT第三篇-java编译源码

* java工具命令
 
>用法: java [-options] class [args...]
> 
> (执行类)
>
>   或  java [-options] -jar jarfile [args...]
>
> (执行 jar 文件)
>
> 其中选项包括:
> 
>    -d32	  使用 32 位数据模型 (如果可用)
> 
>    -d64	  使用 64 位数据模型 (如果可用)
> 
>    -client	  选择 "client" VM
> 
>    -server	  选择 "server" VM
> 
>    -hotspot	  是 "client" VM 的同义词 [已过时]
> 
>  默认 VM 是 client.
> 
>    -cp <目录和 zip/jar 文件的类搜索路径>
> 
>    -classpath <目录和 zip/jar 文件的类搜索路径>
> 
>  用 ; 分隔的目录, JAR 档案
> 
>   和 ZIP 档案列表, 用于搜索类文件。
> 
>    -D<名称>=<值>
> 
>  设置系统属性
> 
>    -verbose:[class|gc|jni]
> 
>  启用详细输出
> 
>    -version      输出产品版本并退出
> 
>    -version:<值>
> 
>  需要指定的版本才能运行
> 
>    -showversion  输出产品版本并继续
> 
>  -jre-restrict-search | -no-jre-restrict-search
> 
>   在版本搜索中包括/排除用户专用 JRE
> 
>    -? -help      输出此帮助消息
> 
>    -X            输出非标准选项的帮助
> 
>    -ea[:<packagename>...|:<classname>]
> 
>    -enableassertions[:<packagename>...|:<classname>]
> 
>  按指定的粒度启用断言
> 
>    -da[:<packagename>...|:<classname>]
> 
>    -disableassertions[:<packagename>...|:<classname>]
> 
>  禁用具有指定粒度的断言
> 
>   -esa | -enablesystemassertions
> 
>  启用系统断言
> 
>    -dsa | -disablesystemassertions
> 
> 禁用系统断言
> 
>    -agentlib:<libname>[=<选项>]
> 
>  加载本机代理库 <libname>, 例如 -agentlib:hprof
> 
>  另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help
> 
>    -agentpath:<pathname>[=<选项>]
> 
>  按完整路径名加载本机代理库
> 
>    -javaagent:<jarpath>[=<选项>]
> 
>  加载 Java 编程语言代理, 请参阅 java.lang.instrument
> 
>   -splash:<imagepath>
> 
>  使用指定的图像显示启动屏幕
>
> 有关详细信息, 请参阅 http://www.oracle.com/technetwork/java/javase/documentation/index.html。

* javac 工具命令

>用法: javac <options> <source files>
>
> 其中, 可能的选项包括:
> 
>  -g                         生成所有调试信息
> 
>  -g:none                    不生成任何调试信息
> 
>  -g:{lines,vars,source}     只生成某些调试信息
> 
>  -nowarn                    不生成任何警告
> 
>  -verbose                   输出有关编译器正在执行的操作的消息
> 
>  -deprecation               输出使用已过时的 API 的源位置
> 
>  -classpath <路径>            指定查找用户类文件和注释处理程序的位置
> 
>  -cp <路径>                   指定查找用户类文件和注释处理程序的位置
> 
>  -sourcepath <路径>           指定查找输入源文件的位置
> 
>  -bootclasspath <路径>        覆盖引导类文件的位置
> 
>  -extdirs <目录>              覆盖所安装扩展的位置
> 
>  -endorseddirs <目录>         覆盖签名的标准路径的位置
> 
>  -proc:{none,only}          控制是否执行注释处理和/或编译。
> 
>  -processor <class1>[,<class2>,<class3>...] 要运行的注释处理程序的名称; 绕过默认的搜索进程
> 
>  -processorpath <路径>        指定查找注释处理程序的位置
> 
>  -d <目录>                    指定放置生成的类文件的位置
> 
>  -s <目录>                    指定放置生成的源文件的位置
> 
>  -implicit:{none,class}     指定是否为隐式引用文件生成类文件
> 
>  -encoding <编码>             指定源文件使用的字符编码
> 
>  -source <发行版>              提供与指定发行版的源兼容性
> 
>  -target <发行版>              生成特定 VM 版本的类文件
> 
>  -version                   版本信息
> 
>  -help                      输出标准选项的提要
> 
>  -A关键字[=值]                  传递给注释处理程序的选项
> 
>  -X                         输出非标准选项的提要
> 
>  -J<标记>                     直接将 <标记> 传递给运行时系统
> 
>  -Werror                    出现警告时终止编译
> 
>  @<文件名>                     从文件读取选项和文件名


* 编译`.java`文件

`javac HelloWorld.java`

* 运行`.class`

`java HelloWorld`

## 作者

本文章 由  [rect](http://www.shadowkong.com/) 所写。
