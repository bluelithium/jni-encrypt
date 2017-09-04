# 项目说明

为了帮助大家系统性的学习JNI技术，遂诞生了此项目；本项目使用JNI技术来实现对于底层加密算法的封装，**增大黑客破解的技术难度**，欢迎star或fork。

## 协议

请遵守此[开源协议](/LICENSE)。

## JNI环境搭建

本文给出的是在IDEA中的配置，Tools > External Tools工具下，进行javah-jni配置，如下：

```
$JDKPath$/bin/javah.exe
-encoding UTF-8  -classpath . -jni -d $ModuleFileDir$/src/main/jni $FileClass$
$ModuleFileDir$\src\main\java
```

## 项目说明

### keymanager

Java层，主要封装了常见的加密算法，以及相应的JNI接口。

### crossplatformencryptutil

C++层，主要对上层JNI接口的实现。

### JniEncryptUtil_Android

安卓相关测试demo。

## 贡献者

**排名不分先后。**

[yrzx404](https://github.com/yrzx404)、[znlover](https://github.com/bluelithium)

## 后续

后面将会添加ios相关demo测试用例。

