package cn.com.servyou.yjscbg.docproducer;


import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author : hao
 * @project : daieweb
 * @description :  2007版 报告生成处理
 * @time : 2018/8/3 16:29
 */
public class Doc2007ProducerImpl extends DocTypeProducer {

    private static final Logger log = Logger.getLogger(Doc2007ProducerImpl.class);

    @Override
    public File create(Map<String, Object> dataMap) {

        String basePath = (String) dataMap.get("path");

        DocTemplate docTemplate = (DocTemplate) dataMap.get("docTemplate");
        //获取模板的路径
        String baseTemplatePath = StringUtils.substringBeforeLast(docTemplate.getMblj(), "/");

        String haveImg = docTemplate.getHaveImg();
        //用于存储有图片的xml.rels文件的路径
        String relsXmlPath = "";
        if ("Y".equals(haveImg)) {
            /**
             * 获取rels模板并解析 生成临时rels文件...
             */
            String templateRels_output_path = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir") + System.currentTimeMillis() + ".xml.rels";
            String templateRels = baseTemplatePath + "/document.xml.rels";
            markerManage.createTemplateXml(dataMap, templateRels, templateRels_output_path);
            //临时保存
            relsXmlPath = templateRels_output_path;
            /**
             * 获取rels文件内容，获取节点ID，放到参数里
             * 图片name 同relsID对应后 无需这一步
             *
             */
            /*String xmlConfigFile_content = FreeMarkerManage.getFreemarkerContent(dataMap, templateRels);
            Document document = null;
            try {
                document = DocumentHelper.parseText(xmlConfigFile_content);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Element rootElt = document.getRootElement(); // 获取根节点
            Iterator iter = rootElt.elementIterator();// 获取根节点下的子节点head
            List<String> warn_img_list = new ArrayList();
            // 遍历Relationships节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String id = recordEle.attribute("Id").getData().toString();
                String target = recordEle.attribute("Target").getData().toString();
                if (target.indexOf("media") == 0) {
                    warn_img_list.add(id);
                }
            }
            dataMap.put("warn_img_list", warn_img_list);*/
        }

        /**
         * 获取doc模板并解析 生成临时doc文件...
         */
        String templateDoc = baseTemplatePath + "/document.xml";
        String templateDoc_output_path = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir") + System.currentTimeMillis() + ".xml";
        markerManage.createTemplateXml(dataMap, templateDoc, templateDoc_output_path);
        File xmlFile = new File(templateDoc_output_path);

        //获取模板的文件
        String base_path = Doc2007ProducerImpl.class.getClassLoader().getResource(docTemplate.getMblj()).getPath();
        File docxFile = new File(base_path);
        if (!docxFile.exists()) {
            try {
                docxFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //解压缩过程
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(docxFile);

            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            String output_path = basePath + PropertiesUtil.getValue("yjscbg.doc.dir");
            String output_file_name = dataMap.get("TITLE") + ".docx";

            String loc = output_path + System.currentTimeMillis();
            File dir = new File(loc);
            if (dir.exists() && dir.isDirectory()) {
                loc = loc + "new";
            } else {
                dir.mkdir();
            }
            loc = loc + "/";
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(loc + output_file_name));

            int len = -1;
            byte[] buffer = new byte[1024];
            //复制图片过程
            Map<String, String> picFiles = (Map<String, String>) dataMap.get("picFiles");
            if (picFiles != null && !picFiles.isEmpty()) {
                for (String fileName : picFiles.keySet()) {
                    ZipEntry next = new ZipEntry("word" + File.separator + "media" + File.separator + fileName);
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(picFiles.get(fileName));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }

            len = -1;
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                // 把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                zipout.putNextEntry(new ZipEntry(next.toString()));
                if ("word/document.xml".equals(next.toString())) {
                    InputStream in = new FileInputStream(xmlFile);
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                } else if (next.toString().indexOf("document.xml.rels") > 0 && "Y".equals(haveImg)) {
                    File xmlConfigFile = new File(relsXmlPath);
                    InputStream in = new FileInputStream(xmlConfigFile);
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                } else {
                    while ((len = is.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    is.close();
                }
            }
            zipout.close();
            //最终生成文件保存在服务器上吗
            File file = new File(loc + output_file_name);
            //将生产文件的信息返回去
            dataMap.put("docPath", (loc + output_file_name).replace(basePath, ""));
            dataMap.put("docName", output_file_name);
            //删除临时文件  暂时屏蔽
            deleteTempFile(dataMap);
            return file;
        } catch (IOException e) {
            log.error("生成替换文件时候出错" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
