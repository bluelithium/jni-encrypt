package cn.javatiku.keymanager.demo;


import cn.javatiku.keymanager.utils.DESUtil;
import org.junit.Before;
import org.junit.Test;

public class DESUtilTest {

    private String message, desKey;

    @Before
    public void setup() {
        message = "这是一个测试字符串!";// 要加密的字符串;
        desKey = "12345678";
    }

    /**
     * AES加解密数据
     *
     * @throws Exception
     */
    @Test
    public void aesEncrypt() throws Exception {
        System.out.println("密钥：" + desKey);
        String jiami = DESUtil.encrypt4Base64(desKey, message);
        System.out.println("加密前：" + message);
        System.out.println("加密后：" + jiami);
        String jiemi = DESUtil.decrypt4Base64(desKey, jiami);
        System.out.println("解密后：" + jiemi);
    }

}
