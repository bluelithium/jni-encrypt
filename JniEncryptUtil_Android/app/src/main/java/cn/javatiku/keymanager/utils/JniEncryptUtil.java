package cn.javatiku.keymanager.utils;

/**
 * @author yrzx404<br/>
 * @description: 全平台加密jni实现<br/>
 * @date 2016/8/29 12:36 <br/>
 */
public class JniEncryptUtil {

    /**
     * 解密字符串
     *
     * @param encryptStr 密文
     * @return
     */
    public static native byte[] decryptString(byte[] encryptStr);

    /**
     * 加密字符串
     *
     * @param painStr 明文
     * @return
     */
    public static native byte[] encryptString(byte[] painStr);

    /**
     * 生成URL签名
     *
     * @param version   api版本
     * @param url  api+api中附带的参数字符串(排序后的字符串)
     * @param timestamp 时间戳
     * @param token     根据id获取缓存服务器中的私钥
     * @return
     */
    public static native String generateUrlSign(byte[] version, byte[] url, byte[] timestamp, byte[] token);

    /**
     * 获取版本号
     *
     * @return
     */
    public static native String getVersion();
}