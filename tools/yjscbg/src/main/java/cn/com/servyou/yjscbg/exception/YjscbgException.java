package cn.com.servyou.yjscbg.exception;

import cn.com.servyou.yjscbg.mappers.daup.YjScbgMapper;
import cn.com.servyou.yjscbg.utils.SpringContextUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :   一键生成报告 自定义异常
 * @time : 2018/8/14 14:50
 */
@Component
@Log4j
public class YjscbgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /* 如果类中含有注解的类， 创建该对象不能用new必须用注入的方式，否则就脱离了spring 容器
    @Autowired
    private QysdsYjScbgMapper qysdsYjScbgMapper;*/

    public YjscbgException() {
        super();
    }

    public YjscbgException(String message) {
        super(message);
    }

    public YjscbgException(String message, Throwable cause) {
        super(message, cause);
    }

    //抛出异常的同时保存异常
    public YjscbgException(String message, Map<String, Object> param) {
        super(message);
        if (param.containsKey("primaryKey")) {
            param.put("BGSCZT", "2");
            log.error("报告编号为【" + param.get("primaryKey") + "】的错误信息为：" + message);
            YjScbgMapper yjScbgMapper = (YjScbgMapper) SpringContextUtil.getBean("yjScbgMapper");
            yjScbgMapper.updateDocZt(param);
        }
    }

}
