package cn.javatiku.keymanager.security;

import cn.javatiku.keymanager.utils.JniEncryptUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yrzx404<br/>
 * @description: 全平台加密工具<br/>
 * @date 2016/8/29 12:48 <br/>
 */
public class PlatformEncryptManager {

    /**
     * url过期时间 2h
     */
    public static final long URL_EXPIRE_TIME = 2 * 60 * 60 * 1000;


    static {
        try {
            Class.forName("cn.javatiku.keymanager.bootstrap.SDK4Bootstrap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取当前jni版本
     *
     * @return
     */
    public static String getVersion() throws Exception {
        return JniEncryptUtil.getVersion();
    }

    /**
     * 解密字符串
     *
     * @param encryptStr 加密字符串
     * @return 解密过后的字符串
     */
    public static String decryptString(String encryptStr) throws Exception {
        return new String(JniEncryptUtil.decryptString(encryptStr.getBytes()), "utf-8");

    }

    /**
     * 加密字符串
     *
     * @param painStr 明文字符串
     * @return 加密字符串
     */
    public static String encryptString(String painStr) throws Exception {
        return new String(JniEncryptUtil.encryptString(painStr.getBytes()), "utf-8");
    }

    /**
     * ------------------ 生成URL签名 --------------------
     * <p>
     * 通过对url和参数加密生成sign，例如获取用户的feed的url是
     * http://www.test.com/api/home/feed.html
     * <p>
     * api url为 http://www.test.com/api/home/feed.html
     * 传递的参数为 param=array("v"=>"1.0","t"=>"1456666666","id"=>1,"page"=>20)
     * <p>
     * app在传递给服务器的参数中，加上使用generateSign生成的sign，
     * 所以发送给服务器的url应为
     * http://www.test.com/api/home/feed.html?v=1.0&t=1456666666&sign=95aa9066d5801815a57bbe537280406b5516cb2a&id=1&page=20
     * <p>
     * 当发现这个时间戳离现在的时间已经很久了，就判断这个url已经失效了。
     * <p>
     * 服务器根据同样的算法生成sign，
     * 对比app传过来的sign和服务器生成的sign，就知道url在传输的过程中是否有被改动
     * <p>
     * 时间戳怎么保证app的时间和服务器的时间同步？
     * 在app每次启动时和服务器同步时间，然后在app内建一个时钟，时间戳在这个app的内部时钟获取，防止用户修改了手机的时间导致时间不一致。
     *
     * @param api       api 如：http://www.test.com/api/home/feed.html
     * @param params    api中附带的参数 如：array("v"=>"1.0","t"=>"1456666666","id"=>1,"page"=>20)
     * @param token     根据id获取缓存服务器中的私钥
     * @return
     */
    public static String generateUrlSign(String api, Map<String, String> params, String token) throws Exception {
        TreeMap<String, String> treeMap = new TreeMap(params);
        String version = null;
        String timestamp = null;
        StringBuffer sb = new StringBuffer(api);
        sb.append("?");
        for (String key : treeMap.keySet()) {
            if (key.toString().equals("v")) {
                version = treeMap.get("v");
            }else if(key.toString().equals("V")){
                version = treeMap.get("V");
            }else if(key.toString().equals("t")){
                timestamp = treeMap.get("t");
            }else if(key.toString().equals("T")){
                timestamp = treeMap.get("T");
            }else{
                sb.append(key + "=" + treeMap.get(key));
                sb.append("&");
            }

        }
        sb.deleteCharAt(sb.length() - 1);
        String url = sb.toString();
        return JniEncryptUtil.generateUrlSign(version.getBytes(), url.getBytes(), timestamp.getBytes(), token.getBytes());
    }

    /**
     * 检测URL签名是否正确
     *
     * @param api       api的url
     * @param params    url中附带的参数[除sign以外的参数]
     * @param token     根据id获取缓存服务器中的私钥
     * @param sign      url中sign参数
     * @return
     * @throws Exception
     */
    public static boolean isValidUrlSign(String api, Map<String, String> params, String token, String sign) throws Exception {
        String timestamp = null;
        for (String key : params.keySet()) {
            if(key.toString().equals("t")){
                timestamp = params.get("t");
            }else if(key.toString().equals("T")){
                timestamp = params.get("T");
            }
        }
        if (!NumberUtils.isNumber(timestamp)) {
            throw new Exception("url is invalid,the time stamp format is not correct.");
        }
        if (Long.valueOf(timestamp) + URL_EXPIRE_TIME < new Date().getTime()) {
            throw new Exception("url is invalid,time expired.");
        }
        String rightSign = PlatformEncryptManager.generateUrlSign(api, params,token);
        if (!sign.equals(rightSign)) {
            throw new Exception("url is invalid, signature is invalid.");
        }
        return true;
    }
}
