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
 * @description : driverjs 驱动管理 保持单例，防止初始化多次，开启多个进程占用内存
 * @time : 2018/7/25 9:49
 */

@Component
@Log4j
public class PhantomJsManage {

    // phantomjs 无界面浏览器驱动
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
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        String osname = System.getProperties().getProperty("os.name");
        if (osname.equals("Linux")) {//判断系统的环境win or Linux
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PropertiesUtil.getValue("yjscbg.phantomjs.driver.dir"));
        } else {
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PropertiesUtil.getValue("yjscbg.phantomjs.driver.dir"));
        }
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return driver;
    }

    /**
     * 异步进行图形的查询
     * @param param
     * @param docParser
     * @return
     */
    @Async
    public Future<String> getImgDataAsync(Map<String, Object> param, AbstractDocParser docParser) {
        Timer timer = new Timer("进行图形数据获取及展现");
        if (this.driver == null) {
            this.driver = getDriver();
        }
        String url = docParser.getTemplatePageUrl(param);
        docParser.openPage(url);
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        log.info("浏览器已经启动。。。下面进行异步图形数据获取");
        if ("2007".equals(docParser.getDocType())) {
            docParser.setImgDataFor2007(param);
        } else {
            docParser.setImgDataFor2003(param);
        }
        return new AsyncResult<String>(timer.getElapsedTimeInfo());
    }

    /**
     * 异步进行普通数据的查询
     * @param param
     * @param abstractDocParser
     * @return
     */
    @Async
    public Future<String> getNormalDataAsync(Map<String, Object> param, AbstractDocParser abstractDocParser) {
        Timer timer = new Timer("进行普通数据查询及填充");
        abstractDocParser.addDataToTemplate(param);
        return new AsyncResult<String>(timer.getElapsedTimeInfo());
    }
}
