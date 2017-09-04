package cn.javatiku.keymanager.demo;

import cn.javatiku.keymanager.security.PlatformEncryptManager;

import java.util.HashMap;
import java.util.Map;

public class PlatformEncryptManagerTest {

    private static String api = "http://www.test.com/api/home/feed.html";
    private static Map<String, String> params;
    private static String timestamp = System.currentTimeMillis()+"";
    private static String token = "296931c21a898f9c49d62f99668130";
    private static String mingwen = "使用参考 \n" +
            "DJTK设计规范 \n" +
            "登录逻辑说明";
    private static String miwen = "qiCXw0J5F3dNDeQ/DYVJwAixlTLqBikjpxB0a2GqLrjp4OeKkIxPMqYoFzSkWjS43gqNCd+VfPbuWxXCkG0YmQ==";

    static {
        params = new HashMap<>();
        params.put("v","1.0");
        params.put("t",timestamp);
        params.put("id", "1");
        params.put("page", "20");
    }

    public static void main(String[] args) throws Exception {
        TestUrlSign();
    }


    public static void TestGetVersion() throws Exception {
        System.err.println(PlatformEncryptManager.getVersion());
    }

    public static void TestEncryptString() throws Exception {
        System.err.println(PlatformEncryptManager.encryptString(mingwen));
    }

    public static void TestDecryptString() throws Exception {
        System.err.println(PlatformEncryptManager.decryptString(miwen));
    }

    public static void TestUrlSign() throws Exception{
        for(int i = 1; i< 1000; i++) {
            String sign = PlatformEncryptManager.generateUrlSign(api, params, token);
            System.err.println("sign:"+sign);
            System.err.println("判断签名："+PlatformEncryptManager.isValidUrlSign(api, params, token, sign));
        }
    }
}
