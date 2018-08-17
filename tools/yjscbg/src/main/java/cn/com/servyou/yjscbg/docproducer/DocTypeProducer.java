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
     * ɾ����ʱ�ļ�
     *
     * @param dataMap
     */
    public void deleteTempFile(Map<String, Object> dataMap) {
        String basePath = (String) dataMap.get("path");
        String tempXmlPath = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir");
        deleteFile(tempXmlPath);
        log.info("ɾ���ˡ�" + tempXmlPath + "������������ʱXML�ļ�");

        String tempImgPath = basePath + PropertiesUtil.getValue("yjscbg.temp.img.dir");
        deleteFile(tempImgPath);
        log.info("ɾ���ˡ�" + tempImgPath + "������������ʱͼƬ�ļ�");
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
