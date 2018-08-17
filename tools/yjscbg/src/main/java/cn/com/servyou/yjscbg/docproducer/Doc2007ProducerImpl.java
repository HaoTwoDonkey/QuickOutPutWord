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
 * @description :  2007�� �������ɴ���
 * @time : 2018/8/3 16:29
 */
public class Doc2007ProducerImpl extends DocTypeProducer {

    private static final Logger log = Logger.getLogger(Doc2007ProducerImpl.class);

    @Override
    public File create(Map<String, Object> dataMap) {

        String basePath = (String) dataMap.get("path");

        DocTemplate docTemplate = (DocTemplate) dataMap.get("docTemplate");
        //��ȡģ���·��
        String baseTemplatePath = StringUtils.substringBeforeLast(docTemplate.getMblj(), "/");

        String haveImg = docTemplate.getHaveImg();
        //���ڴ洢��ͼƬ��xml.rels�ļ���·��
        String relsXmlPath = "";
        if ("Y".equals(haveImg)) {
            /**
             * ��ȡrelsģ�岢���� ������ʱrels�ļ�...
             */
            String templateRels_output_path = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir") + System.currentTimeMillis() + ".xml.rels";
            String templateRels = baseTemplatePath + "/document.xml.rels";
            markerManage.createTemplateXml(dataMap, templateRels, templateRels_output_path);
            //��ʱ����
            relsXmlPath = templateRels_output_path;
            /**
             * ��ȡrels�ļ����ݣ���ȡ�ڵ�ID���ŵ�������
             * ͼƬname ͬrelsID��Ӧ�� ������һ��
             *
             */
            /*String xmlConfigFile_content = FreeMarkerManage.getFreemarkerContent(dataMap, templateRels);
            Document document = null;
            try {
                document = DocumentHelper.parseText(xmlConfigFile_content);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Element rootElt = document.getRootElement(); // ��ȡ���ڵ�
            Iterator iter = rootElt.elementIterator();// ��ȡ���ڵ��µ��ӽڵ�head
            List<String> warn_img_list = new ArrayList();
            // ����Relationships�ڵ�
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
         * ��ȡdocģ�岢���� ������ʱdoc�ļ�...
         */
        String templateDoc = baseTemplatePath + "/document.xml";
        String templateDoc_output_path = basePath + PropertiesUtil.getValue("yjscbg.temp.xml.dir") + System.currentTimeMillis() + ".xml";
        markerManage.createTemplateXml(dataMap, templateDoc, templateDoc_output_path);
        File xmlFile = new File(templateDoc_output_path);

        //��ȡģ����ļ�
        String base_path = Doc2007ProducerImpl.class.getClassLoader().getResource(docTemplate.getMblj()).getPath();
        File docxFile = new File(base_path);
        if (!docxFile.exists()) {
            try {
                docxFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //��ѹ������
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
            //����ͼƬ����
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
                // �����������ļ������������ �����word/document.xml����������
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
            //���������ļ������ڷ���������
            File file = new File(loc + output_file_name);
            //�������ļ�����Ϣ����ȥ
            dataMap.put("docPath", (loc + output_file_name).replace(basePath, ""));
            dataMap.put("docName", output_file_name);
            //ɾ����ʱ�ļ�  ��ʱ����
            deleteTempFile(dataMap);
            return file;
        } catch (IOException e) {
            log.error("�����滻�ļ�ʱ�����" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
