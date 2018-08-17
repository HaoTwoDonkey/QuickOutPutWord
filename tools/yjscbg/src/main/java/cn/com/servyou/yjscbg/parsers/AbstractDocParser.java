package cn.com.servyou.yjscbg.parsers;

import cn.com.servyou.tdap.timefly.Timer;
import cn.com.servyou.yjscbg.docproducer.Doc2003ProducerImpl;
import cn.com.servyou.yjscbg.docproducer.Doc2007ProducerImpl;
import cn.com.servyou.yjscbg.docproducer.DocTypeProducer;
import cn.com.servyou.yjscbg.manage.PhantomJsManage;
import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.utils.ImageFileUtil;
import cn.com.servyou.yjscbg.utils.PropertiesUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author : hao
 * @project : daieweb
 * @description : �����ĵ�������
 * @time : 2018/8/3 15:06
 */
public abstract class AbstractDocParser implements DocParser {

    private static final Logger log = Logger.getLogger(AbstractDocParser.class);
    /**
     * PhantomJs ע����
     */
    protected PhantomJSDriver driver;
    /**
     * Phantomjs ����
     */
    @Autowired
    protected PhantomJsManage phantomJsManage;

    /**
     * ���ɱ����word�汾
     */
    private String docType;

    public String getDocType() {
        return docType;
    }

    @Override
    public File createDoc(Map<String, Object> param) {
        DocTemplate docTemplate = (DocTemplate) param.get("docTemplate");
        docType = docTemplate.getMblb();
        DocTypeProducer docManage = new Doc2007ProducerImpl();
        if ("2003".equals(docType)) {
            docManage = new Doc2003ProducerImpl();
        }
        //��������������ִ��������
        if (docTemplate.getConfigs().size() > 0) {
            getDataFromTemplateConfig(param);
        }
        Timer all = new Timer("ȫ����ѯ");
        if (!haveImg(docTemplate.getHaveImg())) {
            addDataToTemplate(param);
        } else {
            //����phantomJs
            Timer timer = new Timer("����PhantomJs");
            this.driver = phantomJsManage.getDriver();
            log.info(timer.getElapsedTimeInfo());
            //���ģ���������첽ִ�У����������̷ֱ߳�ִ���������ݵĲ�ѯ
            if ("Y".equals(docTemplate.getYbzx())) {
                doAsyncQueryData(param);
            } else {
                doQueryData(param);
            }
        }

        log.info(all.getElapsedTimeInfo());
        return docManage.create(param);
    }

    /**
     * ��������  ͬ����
     * @param param
     */
    public void doQueryData(Map<String, Object> param) {

        addDataToTemplate(param);
        openPage(getTemplatePageUrl(param));
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        if ("2007".equals(docType)) {
            setImgDataFor2007(param);
        } else {
            setImgDataFor2003(param);
        }
    }

    /**
     * �������ݲ�ѯ  �첽�ģ���ѯͼ�κ���ͨ���� �������߳���
     * @param param
     */
    public void doAsyncQueryData(Map<String, Object> param) {

        Future<String> normalFutrue = phantomJsManage.getNormalDataAsync(param, this);
        Future<String> imgFuture = phantomJsManage.getImgDataAsync(param, this);
        while (true) {
            if (normalFutrue.isDone() && !normalFutrue.isCancelled() && imgFuture.isDone() && !imgFuture.isCancelled()) {
                try {
                    log.info(normalFutrue.get());
                    log.info(imgFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * ��������˺�̨������ģ������
     * ִ��ͨ��������������ݲ�ѯ�����
     * <p>
     * ���ӷ��� �������Ҳ��Ҫ�����ݿ�� Ҳ��Ҫ�Լ�ȥ�������ʵ����������ͺ���
     *
     * @param param
     */
    @Override
    public void getDataFromTemplateConfig(Map<String, Object> param) {

    }

    /**
     * ��ȡģ��ҳ��URL
     *
     * @param param
     * @return
     */
    public String getTemplatePageUrl(Map<String, Object> param) {
        log.info("[��ȡ����ҳ��URLΪ:]" + param.get("urlPri") + getPageUrl());
        return param.get("urlPri") + getPageUrl();
    }

    /**
     * ��֯2003�� ����ͼ�����ݸ�ʽ map��key��Ϊ����ģ����Freemarker��ǩ��key ֱ�ӷ���base64����
     *
     * @param param
     */
    public abstract void setImgDataFor2003(Map<String, Object> param);

    /**
     * ��֯2007�� ����ͼ�����ݸ�ʽ map��key��Ϊ����ģ����Freemarker��ǩ��key ��Ҫ����ͼƬ�ļ��������ļ����Ƽ�·��
     *
     * @param param
     */
    public abstract void setImgDataFor2007(Map<String, Object> param);

    /**
     * ��֯�������� ��ͼ������ map��key��Ϊ����ģ����Freemarker��ǩ��key
     *
     * @param param
     */
    public abstract void addDataToTemplate(Map<String, Object> param);

    /**
     * ��ȡǰ��ҳ��URL ָ���Զ���ǰ��ģ��ҳ��
     *
     * @return
     */
    protected abstract String getPageUrl();

    /**
     * ͨ��PhantomJs��ҳ��
     *
     * @param url
     */
    public void openPage(String url) {
        try {
            this.driver.get(url);
        } catch (Exception e) {
            log.error("��ҳ��ʧ��---" + e.getMessage());
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
    }

    /**
     * �����Ƿ���ͼ��
     *
     * @param haveImg
     * @return
     */
    protected boolean haveImg(String haveImg) {
        return "Y".equals(haveImg) ? true : false;
    }

    /**
     * ��ȡͼƬ��base64����
     *
     * @param functionName return returnEchartImg(rhEcharts)
     * @return
     */
    protected String getImgBase64(String functionName) {
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        String base64 = "";
        try {
            base64 = (String) driver.executeScript(functionName);
        } catch (Exception e) {
            log.error("��ȡͼƬ��base64����ʧ��----" + e.getMessage());
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
        return base64.replace("data:image/png;base64,", "");
    }

    /**
     * ��ȡͼƬ��base64���� ���ҽ�ͼƬ�洢����
     *
     * @param functionName
     * @param param
     * @param picName      ͼƬ����Ӣ�ģ�  ֱ����ģ���������
     * @param picType      ͼƬ��׺��ע�� ģ���ͼƬ���ͺ������ɵ�Ҫһ��
     */
    protected void getImgBase64AndSave(String functionName, Map<String, Object> param, String picName, String picType) {
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        String path = (String) param.get("path");
        try {
            String base64 = (String) driver.executeScript(functionName);
            ImageFileUtil.Base64ToImage(base64.replace("data:image/png;base64,", ""), path + PropertiesUtil.getValue("yjscbg.temp.img.dir") + picName + "." + picType);

            //ͼƬ����������ͬʱ�� ��������ȥ picFile �� picNameList
            Map<String, String> picFiles;
            List<String> picNameList;
            if (param.containsKey("picFiles")) {
                picFiles = (Map<String, String>) param.get("picFiles");
                picNameList = (List<String>) param.get("picNamelist");
            } else {
                picFiles = new HashMap<String, String>();
                picNameList = new ArrayList();
            }
            picNameList.add(picName);
            picFiles.put(picName + "." + picType, path + PropertiesUtil.getValue("yjscbg.temp.img.dir") + picName + "." + picType);
            param.put("picFiles", picFiles);
            param.put("picNamelist", picNameList);
            param.put(picName, picName);

        } catch (Exception e) {
            log.error("����ͼƬʧ�ܣ�");
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
    }

    /**
     * ִ��Js����
     *
     * @param functionName
     */
    protected void executeJs(String functionName) {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        try {
            driver.executeScript(functionName);
        } catch (Exception e) {
            log.error("ִ��js�ű�ʧ��:" + e.getMessage());
            driver.close();
            driver.quit();
            phantomJsManage.setDriver(null);
        }
    }
}
