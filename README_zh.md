**[English](README.md)** | 简体中文

# Android 多渠道打包工具

这是一个高效的 Android 多渠道打包工具，具有以下特性：

- **跨平台支持**：无论你使用的是 Windows、Mac 还是 Linux，这个工具都能够完美运行。
- **命令行操作**：通过命令行界面，你可以轻松地进行打包操作，无需复杂的图形界面。
- **多渠道打包**：支持一次性为多个渠道打包，大大提高了打包效率。
- **快速打包**：平均每6秒就能打一个包，让你在短时间内完成大量的打包任务。
- **服务器部署**：这个工具可以部署到服务器上，用于动态生成邀请包和渠道包，满足你的各种需求。

这个工具将会是你 Android 开发过程中的得力助手，帮助你提高工作效率，优化工作流程。

## 多种打包方式

我们提供了两种打包方式，包括添加assets打包方式和AndroidManifest打包方式，以解决360等加固不能再打包的问题。你可以使用以下命令选择打包方式：

```bash
java -cp apptools.jar com.tool.app.Main  # AndroidManifest打包方式
java -cp apptools.jar com.tool.app.CMain  # assets打包方式
java -jar apptools.jar  # 默认assets打包方式
```

## 环境要求

- Java SDK
- Android SDK

## 打包流程

1. 设置当前process 的环境变量，保证 apktool 可以正常工作
2. 执行 `apktool d --no-src -f xxxx.apk temp` 拆解apk
3. 替换 AndroidManifest.xml 中的 channelFlag字符为指定渠道
4. 执行 `apktool b temp unsigned.apk` 重新打包apk
5. 执行 `zipAlign` 生成对齐优化后的 apk 文件
6. 执行 `apksigner` 生成签名后的 apk 文件
7. 回到步骤3，替换新的渠道
8. 完成打包

## 工程目录结构

源码目录：

```
apptool
├── demo
└── tool
```

命令行目录：

```
pro java -jar apptools.jar
├── apptools.jar
├── linux
├── macosx
├── windows
└── map.properties
```

## 使用教程

1. 配置map.properties
2. 更改自己要打包项目的AndroidManifest.xml(可参考apptool/demo)中的渠道号字符替换为map.properties中配置的channelFlag指定字符，打好包后放到map.properties配置的指定路径
3. 执行命令行：`java -jar apptools.jar` 或者 `java -jar apptools.jar google,baidu,yyb`

## 注意事项

1. 对于AndroidManifest打包方式，如果您的电脑以前使用过apktool工具请删除工具生成老的framework.jar
   
   ```
   Windows:C:\Documents and Settings\%current user%\apktool\framework\*
   Mac:~/Library/apktool/framework/1.apk
   ```

2. 请尽量避免java与android环境变量存在空格

3. apksigner命令build-tools最低版本30.0.0