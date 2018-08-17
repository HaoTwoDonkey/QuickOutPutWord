package cn.com.servyou.yjscbg.dao;


import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.pojo.DocTemplateConfig;
import cn.com.servyou.yjscbg.utils.SpringContextUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/9 11:42
 */
@Component("docDefaultDaoImpl")
public class DocDefaultDaoImpl implements DocInterfaceDao {

    protected static final Logger log = Logger.getLogger(DocDefaultDaoImpl.class);

    /**
     * ��ѯ���ݲ���������ݵ�Map��
     *
     * @param param
     * @param docTemplate
     */
    @Override
    public void queryAndSetData(Map<String, Object> param, DocTemplate docTemplate) {

        List<DocTemplateConfig> configs = docTemplate.getConfigs();
        int length = configs.size();
        if (length == 0) {
            log.info("��ģ�������������������ݲ�ѯ");
            return;
        }
        /*  Ч��������...
        for (DocTemplateConfig config:configs) {

        }*/
        for (int i = 0; i < length; i++) {
            DocTemplateConfig config = configs.get(i);
            String sjyName = config.getSjyname();
            log.info("��ȡ��������Դ����" + sjyName);
            DocDataSource<Object> dataSource = (DocDataSource<Object>) SpringContextUtil.getBean(sjyName);
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            try {
                result = dataSource.execute(param, config);
            } catch (Exception e) {
                log.error("ִ�������" + config.getPzxmc() + "��ʱ������ˣ�ԭ������" + e.getMessage());
                setDefaultValue(param, config);
                continue;
            }

            if (result.size() == 0) {
                log.error("ִ�������" + config.getPzxmc() + "��ʱ��δ������ݣ�����Ĭ��ֵ");
                setDefaultValue(param, config);
                continue;
            }
            //���ò�ѯ�����������������ֵ
            setValueToContext(result, param, config);

        }


    }

    /**
     * ����ѯ�Ľ������������ֵ
     *
     * @param result
     * @param param
     * @param config
     */
    private void setValueToContext(List<Map<String, Object>> result, Map<String, Object> param, DocTemplateConfig config) {
        String moreCode = config.getMorecode();
        //���Ƕ��������ҽ�������Ǽ���
        if ("N".equals(moreCode) && "0".equals(config.getJgjlx())) {
            param.put(config.getJgjcode(), result);
        } else {
            //ȡ�������mapֱ�ӷ������
            Map<String, Object> jgj = result.get(0);
            param.putAll(jgj);
        }
    }

    /**
     * �ݴ��Դ��� Ϊ���ó������������ȥ
     *
     * @param param
     * @param config
     */
    private void setDefaultValue(Map<String, Object> param, DocTemplateConfig config) {
        String moreCode = config.getMorecode();
        if ("Y".equals(moreCode)) {
            String[] keys = config.getJgjcode().split(",");
            for (int i = 0; i < keys.length; i++) {
                param.put(keys[i], "");
                log.info("key��" + keys[i] + "������Ĭ��ֵΪ��");
            }
        } else {
            if ("1".equals(config.getJgjlx())) {
                param.put(config.getJgjcode(), "");
                log.info("key��" + config.getJgjcode() + "������Ĭ��ֵΪ��");
            } else {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                param.put(config.getJgjcode(), list);
                log.info("key��" + config.getJgjcode() + "������Ĭ��ֵΪ������");
            }
        }
    }
}
