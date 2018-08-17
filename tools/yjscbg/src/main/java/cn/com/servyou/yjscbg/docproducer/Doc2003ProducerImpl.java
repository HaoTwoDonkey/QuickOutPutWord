package cn.com.servyou.yjscbg.docproducer;

import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.utils.PropertiesUtil;

import java.io.File;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description : 2003版 报告生成处理
 * @time : 2018/8/3 16:31
 */
public class Doc2003ProducerImpl extends DocTypeProducer {

    @Override
    public File create(Map<String, Object> dataMap) {

        String basePath = (String) dataMap.get("path");
        String output_path = basePath + PropertiesUtil.getValue("yjscbg.doc.dir");
        String loc = output_path + System.currentTimeMillis();
        File dir = new File(loc);
        if (dir.exists() && dir.isDirectory()) {
            loc = loc + "new";
        } else {
            dir.mkdir();
        }
        loc = loc + "/";
        String name = loc + dataMap.get("TITLE") + ".doc";
        DocTemplate docTemplate = (DocTemplate) dataMap.get("docTemplate");
        File file = markerManage.createDoc(name, docTemplate.getMblj(), dataMap);
        //将生产文件的信息返回去
        dataMap.put("docPath", (name).replace(basePath, ""));
        dataMap.put("docName", dataMap.get("TITLE") + ".doc");
        return file;
    }
}
