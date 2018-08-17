package cn.com.servyou.yjscbg.manage;

import cn.com.servyou.tdap.timefly.Timer;
import cn.com.servyou.yjscbg.parsers.AbstractDocParser;
import cn.com.servyou.yjscbg.parsers.DemoDocParser;
import cn.com.servyou.yjscbg.parsers.DocParser;
import cn.com.servyou.yjscbg.utils.PropertiesUtil;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author : hao
 * @project : daieweb
 * @description : driverjs �������� ���ֵ�������ֹ��ʼ����Σ������������ռ���ڴ�
 * @time : 2018/7/25 9:49
 */

@Component
@Log4j
public class PhantomJsManage {

    // phantomjs �޽������������
    private PhantomJSDriver driver;

    public synchronized PhantomJSDriver getDriver() {
        if (driver == null) {
            driver = getPhantomJSDriver();
        }
        return driver;
    }

    public void setDriver(PhantomJSDriver phantomJSDriver) {
        driver = null;
    }

    private static PhantomJSDriver getPhantomJSDriver() {
        //���ñ�Ҫ����
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl֤��֧��
        dcaps.setCapability("acceptSslCerts", true);
        //����֧��
        dcaps.setCapability("takesScreenshot", true);
        //css����֧��
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js֧��
        dcaps.setJavascriptEnabled(true);
        //����֧��
        String osname = System.getProperties().getProperty("os.name");
        if (osname.equals("Linux")) {//�ж�ϵͳ�Ļ���win or Linux
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PropertiesUtil.getValue("yjscbg.phantomjs.driver.dir"));
        } else {
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PropertiesUtil.getValue("yjscbg.phantomjs.driver.dir"));
        }
        //�����޽������������
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return driver;
    }

    /**
     * �첽����ͼ�εĲ�ѯ
     * @param param
     * @param docParser
     * @return
     */
    @Async
    public Future<String> getImgDataAsync(Map<String, Object> param, AbstractDocParser docParser) {
        Timer timer = new Timer("����ͼ�����ݻ�ȡ��չ��");
        if (this.driver == null) {
            this.driver = getDriver();
        }
        String url = docParser.getTemplatePageUrl(param);
        docParser.openPage(url);
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        log.info("������Ѿ�������������������첽ͼ�����ݻ�ȡ");
        if ("2007".equals(docParser.getDocType())) {
            docParser.setImgDataFor2007(param);
        } else {
            docParser.setImgDataFor2003(param);
        }
        return new AsyncResult<String>(timer.getElapsedTimeInfo());
    }

    /**
     * �첽������ͨ���ݵĲ�ѯ
     * @param param
     * @param abstractDocParser
     * @return
     */
    @Async
    public Future<String> getNormalDataAsync(Map<String, Object> param, AbstractDocParser abstractDocParser) {
        Timer timer = new Timer("������ͨ���ݲ�ѯ�����");
        abstractDocParser.addDataToTemplate(param);
        return new AsyncResult<String>(timer.getElapsedTimeInfo());
    }
}
