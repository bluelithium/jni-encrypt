package cn.javatiku.keymanager.utils;


import cn.javatiku.keymanager.Constants;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtil {

    private static final String VIPARA = "01020304";

    /**
     * 加密
     *
     * @param key  密钥
     * @param data 明文
     * @return 密文
     * @throws Exception
     */
    public static byte[] encrypt(String key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(Constants.CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(VIPARA.getBytes(Constants.CHARSET));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param key  密钥
     * @param data 密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] decrypt(String key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(Constants.CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(VIPARA.getBytes(Constants.CHARSET));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(data);
        return retByte;
    }

    /**
     * 加密
     *
     * @param key  密钥
     * @param data 明文
     * @return 密文(base64格式)
     * @throws Exception
     */
    public static String encrypt4Base64(String key, String data)
            throws Exception {
        byte[] encryptedData = encrypt(key, data.getBytes(Constants.CHARSET));
        return Base64.encodeBase64String(encryptedData);
    }

    /**
     * 解密
     *
     * @param key  密钥
     * @param data 密文(base64格式)
     * @return 明文
     * @throws Exception
     */
    public static String decrypt4Base64(String key,
                                        String data) throws Exception {
        byte[] encrypted = Base64.decodeBase64(data);
        byte[] decryptedData = decrypt(key, encrypted);
        return new String(decryptedData, Constants.CHARSET);
    }
}
