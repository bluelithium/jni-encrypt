package cn.javatiku.keymanager.utils;


import cn.javatiku.keymanager.Constants;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {

    private static final String VIPARA = "0102030405060708";


    /**
     * android  java  python通用加密方法
     *
     * @param strKey 为16 24 32位 =>对应的加密位数为128 192 256
     * @param data   明文数据
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String strKey, byte[] data)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(Constants.CHARSET));
        SecretKeySpec key = new SecretKeySpec(strKey.getBytes(Constants.CHARSET), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(data);
        return encryptedData;
    }

    /**
     * android  java  python通用解密方法
     *
     * @param strKey    为16 24 32位 =>对应的加密位数为128 192 256
     * @param encrypted 密文数据
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(String strKey, byte[] encrypted)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(Constants.CHARSET));
        SecretKeySpec key = new SecretKeySpec(strKey.getBytes(Constants.CHARSET), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte[] decryptedData = cipher.doFinal(encrypted);
        return decryptedData;
    }

    /**
     * 加密
     *
     * @param key 密钥
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
     * @param key 密钥
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
