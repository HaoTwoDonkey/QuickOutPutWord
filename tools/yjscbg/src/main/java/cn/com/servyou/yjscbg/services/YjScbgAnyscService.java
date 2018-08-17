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
     * �첽ִ�� ���ɱ���
     *
     * @param param
     * @throws InterruptedException
     */
    @Async
    public void createDoc(Map<String, Object> param) throws InterruptedException {
        log.info("��ʼ�첽���ɱ��档������������");
        yjscbgService.doCreateDoc(param);
        log.info("����������ϣ���ʼ����״̬");
        param.put("BGSCZT", "1");
        yjscbgService.updateDocZt(param);
    }

}



