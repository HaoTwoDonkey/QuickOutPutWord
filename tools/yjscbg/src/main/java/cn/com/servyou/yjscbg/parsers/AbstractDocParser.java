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
 * @description : 父类文档解析器
 * @time : 2018/8/3 15:06
 */
public abstract class AbstractDocParser implements DocParser {

    private static final Logger log = Logger.getLogger(AbstractDocParser.class);
    /**
     * PhantomJs 注册器
     */
    protected PhantomJSDriver driver;
    /**
     * Phantomjs 管理
     */
    @Autowired
    protected PhantomJsManage phantomJsManage;

    /**
     * 生成报告的word版本
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
        //如果存在配置项，则执行配置项
        if (docTemplate.getConfigs().size() > 0) {
            getDataFromTemplateConfig(param);
        }
        Timer all = new Timer("全部查询");
        if (!haveImg(docTemplate.getHaveImg())) {
            addDataToTemplate(param);
        } else {
            //启动phantomJs
            Timer timer = new Timer("启动PhantomJs");
            this.driver = phantomJsManage.getDriver();
            log.info(timer.getElapsedTimeInfo());
            //如果模板配置了异步执行，则开启两个线程分别执行两种数据的查询
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
     * 进行数据  同步的
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
     * 进行数据查询  异步的，查询图形和普通数据 在两个线程里
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
     * 如果配置了后台配置了模板配置
     * 执行通过配置项进行数据查询和填充
     * <p>
     * 钩子方法 如果子类也需要从数据库读 也需要自己去代码读就实现这个方法就好了
     *
     * @param param
     */
    @Override
    public void getDataFromTemplateConfig(Map<String, Object> param) {

    }

    /**
     * 获取模板页面URL
     *
     * @param param
     * @return
     */
    public String getTemplatePageUrl(Map<String, Object> param) {
        log.info("[获取到的页面URL为:]" + param.get("urlPri") + getPageUrl());
        return param.get("urlPri") + getPageUrl();
    }

    /**
     * 组织2003版 报告图形数据格式 map的key即为定义模板里Freemarker标签的key 直接放入base64代码
     *
     * @param param
     */
    public abstract void setImgDataFor2003(Map<String, Object> param);

    /**
     * 组织2007版 报告图形数据格式 map的key即为定义模板里Freemarker标签的key 需要保存图片文件，返回文件名称及路径
     *
     * @param param
     */
    public abstract void setImgDataFor2007(Map<String, Object> param);

    /**
     * 组织报告数据 非图形数据 map的key即为定义模板里Freemarker标签的key
     *
     * @param param
     */
    public abstract void addDataToTemplate(Map<String, Object> param);

    /**
     * 获取前端页面URL 指向自定义前端模板页面
     *
     * @return
     */
    protected abstract String getPageUrl();

    /**
     * 通过PhantomJs打开页面
     *
     * @param url
     */
    public void openPage(String url) {
        try {
            this.driver.get(url);
        } catch (Exception e) {
            log.error("打开页面失败---" + e.getMessage());
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
    }

    /**
     * 报告是否含有图形
     *
     * @param haveImg
     * @return
     */
    protected boolean haveImg(String haveImg) {
        return "Y".equals(haveImg) ? true : false;
    }

    /**
     * 获取图片的base64代码
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
            log.error("获取图片的base64代码失败----" + e.getMessage());
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
        return base64.replace("data:image/png;base64,", "");
    }

    /**
     * 获取图片的base64代码 并且将图片存储起来
     *
     * @param functionName
     * @param param
     * @param picName      图片名称英文，  直接在模板里面调用
     * @param picType      图片后缀，注意 模板的图片类型和你生成的要一致
     */
    protected void getImgBase64AndSave(String functionName, Map<String, Object> param, String picName, String picType) {
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        String path = (String) param.get("path");
        try {
            String base64 = (String) driver.executeScript(functionName);
            ImageFileUtil.Base64ToImage(base64.replace("data:image/png;base64,", ""), path + PropertiesUtil.getValue("yjscbg.temp.img.dir") + picName + "." + picType);

            //图片保存起来的同时将 参数填充进去 picFile 和 picNameList
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
            log.error("保存图片失败！");
            this.driver.close();
            this.driver.quit();
            phantomJsManage.setDriver(null);
        }
    }

    /**
     * 执行Js代码
     *
     * @param functionName
     */
    protected void executeJs(String functionName) {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        try {
            driver.executeScript(functionName);
        } catch (Exception e) {
            log.error("执行js脚本失败:" + e.getMessage());
            driver.close();
            driver.quit();
            phantomJsManage.setDriver(null);
        }
    }
}
