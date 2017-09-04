package cn.javatiku.keymanager.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;

import cn.javatiku.keymanager.utils.JniEncryptUtil;

import static org.junit.Assert.*;

/**
 * Created by ez on 5/6/2017.
 */
@RunWith(AndroidJUnit4.class)
public class JniEncryptUtilTest {
    static {
        System.loadLibrary("encrypt-lib");
    }

    @Test
    public void TestGetVersion(){
        assertEquals("V 1.0", JniEncryptUtil.getVersion());
    }

    @Test
    public void TestGenerateUrlSign() throws Exception{
        for(int i = 1; i< 1000; i++) {
            assertEquals( JniEncryptUtil.generateUrlSign("v0.1".getBytes(), "www.baidu.com".getBytes(),"12.02".getBytes(), "aaaa".getBytes()),
                    "9CF918258F8B6B651B1860FE5EBED007"  );
        }
    }

    @Test
    public void TestEncrpytAndDecrypt() throws UnsupportedEncodingException {
        String orginal = "abcdefghijklmnopqrstuvwxyz";

        String base64 = new String(JniEncryptUtil.encryptString(orginal.getBytes("utf-8")));
        byte [] ttt = JniEncryptUtil.decryptString(base64.getBytes());
        String result = new String(ttt, "utf-8");

        assertEquals(orginal, result);
    }

}
