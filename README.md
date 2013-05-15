Android多渠道打包工具
========

支持跨平台、命令行、多渠道、平均6秒打一个包

1. 环境要求
========
<ul>
  <li>Java SDK</li>
  <li>Android SDK</li>
</ul>
2.打包流程
========
1.设置当前process 的环境变量，保证 apktool 可以正常工作<br />
2.执行 apktool d --no-src -f xxxx.apk temp 拆解apk<br />
3.替换 AndroidManifest.xml 中的 channelFlag字符为指定渠道<br />
4.执行 apktool b temp unsigned.apk 重新打包apk<br />
5.执行 jarsigner 生成签名后的 apk 文件<br />
6.执行 zipAlign 生成对齐优化后的 apk 文件<br />
7.回到 3 替换新的渠道<br />
8.完成打包<br />


3.工程目录结构
========
源码：<br />

> ├apktool<br />
> &nbsp;&nbsp;├src<br />
> &nbsp;&nbsp;├bin<br />
> &nbsp;&nbsp;├.classpath<br />
> &nbsp;&nbsp;├.project<br />
> &nbsp;&nbsp;├linux<br />
> &nbsp;&nbsp;├macosx<br />
> &nbsp;&nbsp;├windows<br />
> &nbsp;&nbsp;├map.properties<br />
 
命令行：<br />
> ├pro java -jar apptools.jar<br />
> &nbsp;&nbsp;├apptools.jar<br />
> &nbsp;&nbsp;├linux<br />
> &nbsp;&nbsp;├macosx<br />
> &nbsp;&nbsp;├windows<br />
> &nbsp;&nbsp;├map.properties<br />


4.使用教程
========
1.配置map.properties<br />
2.更改自己要打包项目的AndroidManifest.xml(可参考apps demo)中的渠道号字符替换为map.properties中配置的channelFlag
指定字符 打好包后放到map.properties配置的指定路径<br />
3.执行命令行：java -jar apptools.jar 或者 java -jar apptools.jar google,baidu,yyh<br />

5.注意事项
========
1.如果您的电脑以前使用过apktool工具请删除工具生成老的framework.jar 路径(windows平台):
c:\Documents and Settings\%current user%\apktool\framework\*

2.请尽量避免java与android环境变量存在空格

3.不支持jar包中包含有资源文件的apk项目，受apktool工具本身功能限制(如有jar包源码，可尝试把源码建立成Android项目，把资源放进assets以Android方式加载打包成jar. android模式的jar中有assets资源,打包时会自动把assets合并进项目)

4.如出现aapt命令问题，请解决环境变量入径问题 如：Android SDK Tools 版本为22时需在Android SDK Manager中安装Android SDK Build-tools 然后添加路径%ANDROID_SDK_HOME%\build-tools\17.0.0;到Path
