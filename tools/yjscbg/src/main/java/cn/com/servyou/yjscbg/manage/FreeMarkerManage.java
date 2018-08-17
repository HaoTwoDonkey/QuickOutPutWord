package cn.com.servyou.yjscbg.manage;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :   freemarker ����
 * @time : 2018/8/3 17:44
 */
public class FreeMarkerManage {

    private static Configuration configuration = null;

    private static class FreeMarkerTemplateInstance {
        static FreeMarkerManage FreeMarkerManage = new FreeMarkerManage();
    }

    public static FreeMarkerManage getInstance() {
        return FreeMarkerTemplateInstance.FreeMarkerManage;
    }

    private FreeMarkerManage() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(FreeMarkerManage.class, "/");
    }

    /**
     * �滻�ļ� �����ļ�
     *
     * @param dataMap
     * @return
     */
    public File createDoc(Map<String, Object> dataMap) {
        try {
            String name = dataMap.get("title") + ".doc";
            File f = new File(name);
            String templateName = (String) dataMap.get("template");
            Template t = configuration.getTemplate(templateName);
            // ����ط�����ʹ��FileWriter��Ϊ��Ҫָ���������ͷ������ɵ�Word�ĵ�����Ϊ���޷�ʶ��ı�����޷���
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
            return f;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("����word�ĵ�ʧ��");
        }
    }

    /**
     * �滻�ļ� �����ļ�
     *
     * @param dataMap
     * @return
     */
    public File createDoc(String fileName, String templateName, Map<String, Object> dataMap) {
        try {
            // String name = dataMap.get("title") + ".doc";
            File f = new File(fileName);
            //String templateName = (String) dataMap.get("template");
            Template t = configuration.getTemplate(templateName);
            // ����ط�����ʹ��FileWriter��Ϊ��Ҫָ���������ͷ������ɵ�Word�ĵ�����Ϊ���޷�ʶ��ı�����޷���
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
            return f;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("����word�ĵ�ʧ��");
        }
    }

    /**
     * ֱ�Ӵ����ļ�
     *
     * @param dataMap
     * @param templateName
     * @param filePath
     */
    public static void createTemplateXml(Map<String, Object> dataMap, String templateName, String filePath) {
        try {
            Template template = configuration.getTemplate(templateName);

            //����ļ�
            File outFile = new File(filePath);

            //������Ŀ���ļ��в����ڣ��򴴽�
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            //��ģ�������ģ�ͺϲ������ļ�
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));


            //�����ļ�
            template.process(dataMap, out);

            //�ر���
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �滻����  �����ı�
     *
     * @param dataMap
     * @param templateName
     * @return
     */
    public static String getFreemarkerContent(Map dataMap, String templateName) {
        String ret_str = "";
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter swriter = new StringWriter();
            //�����ļ�
            template.process(dataMap, swriter);
            ret_str = swriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_str;
    }
}
