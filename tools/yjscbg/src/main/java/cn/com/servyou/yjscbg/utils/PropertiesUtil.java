package cn.com.servyou.yjscbg.utils;

import cn.com.jdls.foundation.util.StringUtil;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.Properties;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/6 11:17
 */
public class PropertiesUtil {

    private static final Logger log = Logger.getLogger(PropertiesUtil.class);
    private static Properties prop = null;

    static {
        try {
            prop = new Properties();
/*            String path = PropertiesUtil.class.getResource("/").getPath();
            path = path.substring(1, path.indexOf("classes"));
            InputStream inputStream = new FileInputStream(path + "yjscbg_config.properties");*/
            InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("yjscbg_config.properties");
            prop.load(new InputStreamReader(inputStream, "GBK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getValue(String key) {
        String value = prop.getProperty(key);
        if(StringUtil.isNullString(value)){
            log.debug("获取到的【"+key+"】的值为空，请检查！");
        }
        return value;
    }


}
