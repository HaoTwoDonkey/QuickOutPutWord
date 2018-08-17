package cn.com.servyou.yjscbg.services;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/14 10:34
 */
@Log4j
@Service("yjscbgAnyscService")
public class YjScbgAnyscService {

    @Autowired
    private YjScbgService yjscbgService;

    /**
     * 异步执行 生成报告
     *
     * @param param
     * @throws InterruptedException
     */
    @Async
    public void createDoc(Map<String, Object> param) throws InterruptedException {
        log.info("开始异步生成报告。。。。。。。");
        yjscbgService.doCreateDoc(param);
        log.info("报告生成完毕，开始更新状态");
        param.put("BGSCZT", "1");
        yjscbgService.updateDocZt(param);
    }

}



