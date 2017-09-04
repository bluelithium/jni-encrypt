# 跨平台加解密

## 工程简介

该工程设计的开发目标是实现支持windows，linux以及android平台的加解密jni实现的支持，用以更强的保护app加密强度。

## 生成jni声明文件定义

```
javah -jni -encoding utf-8 包名.类名

eg: javah -jni -encoding utf-8 cn.javatiku.keymanager.utils.JniEncryptUtil
```
在编译项目时，需要注意linux下的jni_md.h声明与windows下不同。

## windows平台编译

该工程基于Windows平台下vs2015开发，原生态支持windows平台，直接使用vs2015导入项目，开发编译该项目即可。

### 遇到的问题记录

1.注意生成的动态链接库应该与jvm版本对应，应该区分x86与x64版本。
2.可以使用attach jvm进程的方式，进行跨语言代码调试。


## Ubuntu平台下编译

在ubuntu下，使用g++进行编译。

### g++编译命令
```
g++ -fPIC -shared -o JniEncrypt.so JniEncrypt/JniEncryptUtil.cpp EncryptCore/aeswapper.cpp EncryptCore/base64.cpp EncryptCore/md5signtool.cpp EncryptCore/aes/aes_core.cpp EncryptCore/aes/cbc128.cpp EncryptCore/md5/md5_dgst.c EncryptCore/md5/md5_one.c
```

### 遇到的问题

- 在使用goto语句时，g++编译器与MSVC编译器不同，不允许goto后有变量声明，也就是所有变量都必须在开头goto跳转前声明，应该是两个编译器语法解析策略不同，
MSVC编译器更聪明一些。
- 当生成的.so文件调用时出现符号未找到时，造成原因与windows下相同，是有些符号只有声明没有实现，但是在linux下，没有被链接进来可以出现在调用时，
而windows下在链接目标文件时，便会检查函数声明是否被实现。

## Android平台下编译

使用Android Studio进行NDK开发，在创建项目时需要勾选对c++的支持。其他方面与常规java开发基本相同。

1.将cpp文件路径加入cpp文件夹下，并且通过在CMakeList.txt中添加源文件路径，便可在app编译时被编译。

为了便于直接将cpp文件拷贝至android测试项目下，这里提供了encrypt_android_env.bat,直接运行，便会将c++相关文件同步至android项目的cpp对应文件夹下。

## 总结

- 跨平台缓存区问题与类型默认转换问题

在使用数组时，一定要注意初始化数组，并且注意跨平台下的数据类型在不同编译器下可能会有差别。例如long的范围是(-2,147,483,648 to 2,147,483,647),
我们之前代码错误将一个大于该范围的值赋值给了long类型，依然能加密解密，但是在linux下却加密解密失败，发现该超出范围的变量在linux下被全部识别，在windows被截断了，导致密钥的不同，从而导致跨平台代码的加密结果不同。

```
long nTemp = 3154298576123032;
修改为
long long nTemp = 3154298576123032;
```

- 使用visual studio 2017 跨平台开发，要确保目标主机安装了以下软件

```
sudo apt-get install openssh-server g++ gdb gdbserver
```

