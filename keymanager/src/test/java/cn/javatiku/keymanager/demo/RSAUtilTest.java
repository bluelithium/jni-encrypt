package cn.javatiku.keymanager.demo;

import cn.javatiku.keymanager.utils.RSAUtil;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSAUtilTest {

    private byte[] data;

    @Before
    public void setup() {
        data = "这是一个测试字符串!".getBytes();// 要加密的字符串;
    }

    @Test
    public void generateKeyPairTest() throws Exception {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        System.err.println(keyPair.getPublic().toString());
        System.err.println(keyPair.getPrivate().toString());
    }

    @Test
    public void genKeyPair2FileSystemTest() throws Exception {
        RSAUtil.genKeyPair2FileSystem();
    }

    @Test
    public void getKeyPairTest() throws Exception {
        RSAPublicKey rsap = (RSAPublicKey) RSAUtil.getKeyPair4FileSystem().getPublic();
        System.err.println(rsap.toString());
        System.err.println("modulus:" + rsap.getModulus().toString(16));
        System.err.println("public exponent:" + rsap.getPublicExponent().toString(16));
        RSAPrivateKey rsapr = (RSAPrivateKey) RSAUtil.getKeyPair4FileSystem().getPrivate();
        System.err.println(rsapr.toString());
        System.err.println("modulus:" + rsapr.getModulus().toString(16));
        System.err.println("private exponent:" + rsapr.getPrivateExponent().toString(16));
    }

    @Test
    public void rsaProcess4Base64Test() throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) RSAUtil.getKeyPair4FileSystem().getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) RSAUtil.getKeyPair4FileSystem().getPrivate();
        String enData4Base64 = RSAUtil.encrypt4Base64(publicKey, data);
        System.err.println("加密结果：" + enData4Base64);
        String deDataStr = RSAUtil.decrypt4Base64(privateKey, enData4Base64);
        System.err.println("解密结果：" + deDataStr);
    }

    @Test
    public void decryptRequestParamTest() {
        String param = "3d979f75dcc6358fe26d5677c6c6d630766126bb23e4340414e1359324276570232401a88a3a98415afadacfb899ce913f41675cd11e4000eca56a3f9a088b8f0e546d07c54abfb138baa407f7ae3e652d7a2111df6bc78c35198df7b830717dda38bfde6072b8692e12e8cc2ee0e7e1d7fec906453f51b39bcfaf93adc97d34";
        System.err.println("请求参数js加密：" + param);
        String result = RSAUtil.decryptRequestParam(param);
        System.err.println("请求参数解密：" + result);
    }
}
