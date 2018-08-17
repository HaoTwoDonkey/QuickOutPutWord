package cn.com.servyou.yjscbg.parsers;

import cn.com.servyou.yjscbg.dao.DocInterfaceDao;
import cn.com.servyou.yjscbg.pojo.DocTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :   ��һ��Ĭ�ϵ�   Ĭ�ϲ���Ҫ������ ֱ�Ӵ����ݿ��ȡ
 * @time : 2018/8/9 11:12
 */
@Component("defaultDocParser")
public class DefaultDocParser extends AbstractDocParser {

    @Autowired
    private DocInterfaceDao docDefaultDaoImpl;

    @Override
    public void setImgDataFor2003(Map<String, Object> param) {

    }

    @Override
    public void setImgDataFor2007(Map<String, Object> param) {

    }

    @Override
    public void addDataToTemplate(Map<String, Object> param) {

    }

    /**
     * ����Ĭ�ϲ�ѯ
     *
     * @param param
     */
    @Override
    public void getDataFromTemplateConfig(Map<String, Object> param) {
        docDefaultDaoImpl.queryAndSetData(param, (DocTemplate) param.get("docTemplate"));
    }


    @Override
    protected String getPageUrl() {
        return null;
    }
}
