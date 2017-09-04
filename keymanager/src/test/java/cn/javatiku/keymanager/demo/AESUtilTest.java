package cn.javatiku.keymanager.demo;


import cn.javatiku.keymanager.utils.AESUtil;
import org.junit.Before;
import org.junit.Test;

public class AESUtilTest {

    private String message, aesKey;

    @Before
    public void setup() throws Exception {
        message = "这是一个测试字符串!";// 要加密的字符串;
        aesKey = "1234567890123456";
    }

    /**
     * AES加解密数据
     *
     * @throws Exception
     */
    @Test
    public void aesEncrypt() throws Exception {
        System.out.println("密钥：" + aesKey);
        String jiami = AESUtil.encrypt4Base64(aesKey, message);
        System.out.println("加密前：" + message);
        System.out.println("加密后：" + jiami);
        String jiemi = AESUtil.decrypt4Base64(aesKey, jiami);
        System.out.println("解密后：" + jiemi);
    }

}
