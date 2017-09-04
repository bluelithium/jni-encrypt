package cn.javatiku.keymanager.demo;

import org.junit.Test;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Map;

public class BasicSecretTest {

    /**
     * 生成随机数盐
     */
    @Test
    public void SecureRandomTest(){
        SecureRandom secureRandom = new SecureRandom();
        System.err.println(secureRandom.nextLong());
    }

    /**
     * 打印当前系统所配置的全部安全提供者
     */
    @Test
    public void testProvider(){
        for(Provider provider: Security.getProviders()){
            //打印提供者信息
            System.err.println(provider);
            //遍历提供者set实例
            for(Map.Entry<Object,Object> entry: provider.entrySet()){
                System.err.println("\t"+entry.getKey());
            }
        }
    }
}
