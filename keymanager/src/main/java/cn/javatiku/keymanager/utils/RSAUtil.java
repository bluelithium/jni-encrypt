package cn.javatiku.keymanager.utils;


import cn.javatiku.keymanager.Constants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

/**
 * RSA 1024位加密算法
 */
public class RSAUtil {
    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 提供者的字符串名称
     */
    private static final String PROVIDER = "BC";
    /**
     * 密钥长度，用来初始化
     * 这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
     */
    private static final int KEYSIZE = 1024;

    private static String FILE_RSA_PUBLIC_KEY;
    private static String FILE_RSA_PRIVATE_KEY;

    static {
        try {
            String path = RSAUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            ClassLoader ioe = Thread.currentThread().getContextClassLoader();
            Properties prop = new Properties();
            InputStream in = ioe.getResourceAsStream("lib.properties");
            prop.load(in);
            in.close();
            FILE_RSA_PUBLIC_KEY = path+File.separator+prop.getProperty("rsa.public");
            FILE_RSA_PRIVATE_KEY = path+File.separator+prop.getProperty("rsa.private");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 生成密钥对
     *
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEYSIZE);
        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 生成并保存秘钥对到文件
     *
     * @throws Exception
     */
    public static void genKeyPair2FileSystem() throws Exception {
        KeyPair keyPair = generateKeyPair();
        byte[] pk = keyPair.getPublic().getEncoded();
        byte[] privk = keyPair.getPrivate().getEncoded();
        String strpk = Base64.encodeBase64String(pk);
        String strprivk = Base64.encodeBase64String(privk);
        writeToFile(strpk, FILE_RSA_PUBLIC_KEY);
        writeToFile(strprivk, FILE_RSA_PRIVATE_KEY);
    }

    /**
     * 从保存文件中获取秘钥对
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair4FileSystem() throws Exception {
        String pubKey = readFromFile(FILE_RSA_PUBLIC_KEY);
        String priKey = readFromFile(FILE_RSA_PRIVATE_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
        PublicKey publicKey = keyFactory.generatePublic(pubX509);
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey));
        PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 加密方法:公钥加密
     *
     * @param data 源数据
     * @return 密文（base64格式）
     * @throws Exception
     */
    public static String encrypt4Base64(PublicKey publicKey, byte[] data) throws Exception {
        return Base64.encodeBase64String(encrypt(publicKey, data));
    }


    /**
     * 加密方法:公钥加密
     *
     * @param publicKey   加密的公钥
     * @param data 待加密的明文数据
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
            // 加密块大小为127byte,加密后为128byte;因此共有2个加密块，第一个127byte第二个为1个byte
            int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
                }
                i++;
            }
            return raw;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 解密方法:私钥解密
     *
     * @param privateKey 私钥
     * @param base64Data 密文（base64格式）
     * @return
     * @throws Exception
     */
    public static String decrypt4Base64(PrivateKey privateKey, String base64Data) throws Exception {
        return new String(decrypt(privateKey, Base64.decodeBase64(base64Data)), Constants.CHARSET);
    }

    /**
     * 解密方法:私钥解密
     *
     * @param privateKey 解密的密钥
     * @param data       已经加密的数据
     * @return 解密后的明文
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;

            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 解密前端javascript加密过的密文。
     * 如果为空，表示解密失败。
     *
     * @param param 加密数据
     * @return 解密后的明文
     */
    public static String decryptRequestParam(String param) {
        try {
            byte[] enResult = hexStr2ByteArr(param);
            byte[] deResult = decrypt(RSAUtil.getKeyPair4FileSystem().getPrivate(), enResult);
            return new StringBuilder(new String(deResult)).reverse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 16进制字符串转换为byte数组
     *
     * @param strIn
     * @return
     */
    private static byte[] hexStr2ByteArr(String strIn) {
        if (strIn == null || strIn.equals("")) {
            return null;
        }
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 写入文件
     *
     * @param key
     * @param fileName
     * @throws IOException
     */
    private static void writeToFile(String key, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        IOUtils.write(key, fileWriter);
        IOUtils.closeQuietly(fileWriter);
    }

    /**
     * 读取文件
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static String readFromFile(String fileName) throws IOException, ClassNotFoundException {
        StringBuffer sb = new StringBuffer();
        for (String line : IOUtils.readLines(new FileInputStream(fileName))) {
            sb.append(line);
        }
        return sb.toString();
    }
}
