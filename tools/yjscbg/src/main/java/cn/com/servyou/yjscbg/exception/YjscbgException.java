package cn.com.servyou.yjscbg.exception;

import cn.com.servyou.yjscbg.mappers.daup.YjScbgMapper;
import cn.com.servyou.yjscbg.utils.SpringContextUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :   һ�����ɱ��� �Զ����쳣
 * @time : 2018/8/14 14:50
 */
@Component
@Log4j
public class YjscbgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /* ������к���ע����࣬ �����ö�������new������ע��ķ�ʽ�������������spring ����
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

    //�׳��쳣��ͬʱ�����쳣
    public YjscbgException(String message, Map<String, Object> param) {
        super(message);
        if (param.containsKey("primaryKey")) {
            param.put("BGSCZT", "2");
            log.error("������Ϊ��" + param.get("primaryKey") + "���Ĵ�����ϢΪ��" + message);
            YjScbgMapper yjScbgMapper = (YjScbgMapper) SpringContextUtil.getBean("yjScbgMapper");
            yjScbgMapper.updateDocZt(param);
        }
    }

}
