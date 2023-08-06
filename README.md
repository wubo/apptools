Android多渠道打包工具
========

支持跨平台、命令行、多渠道、平均6秒打一个包

1. 多种打包方式
   ========
   
   添加assets打包方式 解决360等加固不能再打包问题<br />
   使用java -cp命令可选择打包方式<br />
   java -cp apptools.jar com.tool.app.Main(AndroidManifest打包方式)<br />
   java -cp apptools.jar com.tool.app.CMain(assets打包方式)<br />
   java -jar apptools.jar 默认assets打包方式

2. 环境要求
   ========
   
   <ul>
   <li>Java SDK</li>
   <li>Android SDK</li>
   </ul>

3. 打包流程
   ========
   
   1.设置当前process 的环境变量，保证 apktool 可以正常工作<br />
   2.执行 apktool d --no-src -f xxxx.apk temp 拆解apk<br />
   3.替换 AndroidManifest.xml 中的 channelFlag字符为指定渠道<br />
   4.执行 apktool b temp unsigned.apk 重新打包apk<br />
   5.执行 zipAlign 生成对齐优化后的 apk 文件<br />
   6.执行 apksigner 生成签名后的 apk 文件<br />
   7.回到 3 替换新的渠道<br />
   8.完成打包<br />

4.工程目录结构
========

源码：<br />

> ├apptool<br />
> &nbsp;&nbsp;├demo<br />
> &nbsp;&nbsp;├tool<br />

命令行：<br />

> ├pro java -jar apptools.jar<br />
> &nbsp;&nbsp;├apptools.jar<br />
> &nbsp;&nbsp;├linux<br />
> &nbsp;&nbsp;├macosx<br />
> &nbsp;&nbsp;├windows<br />
> &nbsp;&nbsp;├map.properties<br />

5.使用教程
========

1.配置map.properties<br />
2.更改自己要打包项目的AndroidManifest.xml(可参考apptool/demo)中的渠道号字符替换为map.properties中配置的channelFlag
指定字符 打好包后放到map.properties配置的指定路径<br />
3.执行命令行：java -jar apptools.jar 或者 java -jar apptools.jar google,baidu,yyb<br />

6.注意事项
========

1.对于AndroidManifest打包方式，如果您的电脑以前使用过apktool工具请删除工具生成老的framework.jar
Windows:C:\Documents and Settings\%current user%\apktool\framework\*
Mac:~/Library/apktool/framework/1.apk

2.请尽量避免java与android环境变量存在空格

3.apksigner命令build-tools最低版本30.0.0