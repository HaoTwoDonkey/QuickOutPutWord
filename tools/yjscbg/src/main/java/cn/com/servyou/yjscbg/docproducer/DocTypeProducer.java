package cn.com.servyou.yjscbg.docproducer;


import cn.com.servyou.yjscbg.manage.FreeMarkerManage;
import cn.com.servyou.yjscbg.utils.PropertiesUtil;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/3 16:28
 */
@Log4j
public abstract class DocTypeProducer {

    public static FreeMarkerManage markerManage = FreeMarkerManage.getInstance();

    public abstract File create(Map<String, Object> dataMap);

    /**
     * 删除临时文件
     *
     * @param dataMap
     */
    public void deleteTempFile(Map<String, Object> dataMap) {
        String basePath = (String) dataMap.get("path");
        String tempXmlPath = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir");
        deleteFile(tempXmlPath);
        log.info("删除了【" + tempXmlPath + "】下面所有临时XML文件");

        String tempImgPath = basePath + PropertiesUtil.getValue("yjscbg.temp.img.dir");
        deleteFile(tempImgPath);
        log.info("删除了【" + tempImgPath + "】下面所有临时图片文件");
    }

    public static boolean deleteFile(File f, Boolean child) {
        if (!f.exists()) {
            return true;
        } else {
            if (f.isDirectory()) {
                String[] children = f.list();

                for (int i = 0; i < children.length; ++i) {
                    boolean success = deleteFile(new File(f, children[i]), true);
                    if (!success) {
                        return false;
                    }
                }
            }
            if (child) {
                f.delete();
            }
            return true;
        }
    }

    public static boolean deleteFile(String name) {
        return deleteFile(new File(name), false);
    }
}
