package cn.javatiku.keymanager.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yrzx404<br/>
 * @description: jni加密启动器<br/>
 * @date 2016/8/29 10:32 <br/>
 */
public class SDK4Bootstrap {

    static {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("windows");

        try {
            ClassLoader ioe = Thread.currentThread().getContextClassLoader();
            Properties prop = new Properties();
            InputStream in = ioe.getResourceAsStream("lib.properties");
            prop.load(in);
            in.close();
            String libName;
            if (isWindows) {
                libName = prop.getProperty("libname.windows");
            } else {
                libName = prop.getProperty("libname.linux");
            }

            String path = SDK4Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String libAndroidNamePath = path + File.separator + libName;
            libAndroidNamePath = libAndroidNamePath.replace("%20", " ");

            try {
                System.err.println("libAndroidNamePath:" + libAndroidNamePath);
                System.load(libAndroidNamePath);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace(System.err);
            }
        } catch (IOException e) {
            System.err.println("Failed to parse properties file: lib.properties");
            e.printStackTrace();
        }
    }
}
