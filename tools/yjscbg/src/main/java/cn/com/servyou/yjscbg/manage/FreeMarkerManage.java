package cn.com.servyou.yjscbg.manage;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :   freemarker 解析
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
     * 替换文件 返回文件
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
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
            return f;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("生成word文档失败");
        }
    }

    /**
     * 替换文件 返回文件
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
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
            return f;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("生成word文档失败");
        }
    }

    /**
     * 直接创建文件
     *
     * @param dataMap
     * @param templateName
     * @param filePath
     */
    public static void createTemplateXml(Map<String, Object> dataMap, String templateName, String filePath) {
        try {
            Template template = configuration.getTemplate(templateName);

            //输出文件
            File outFile = new File(filePath);

            //如果输出目标文件夹不存在，则创建
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));


            //生成文件
            template.process(dataMap, out);

            //关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换遍历  返回文本
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
            //生成文件
            template.process(dataMap, swriter);
            ret_str = swriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_str;
    }
}
