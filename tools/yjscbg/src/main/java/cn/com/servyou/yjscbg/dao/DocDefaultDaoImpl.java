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
     * 查询数据并且填充数据到Map中
     *
     * @param param
     * @param docTemplate
     */
    @Override
    public void queryAndSetData(Map<String, Object> param, DocTemplate docTemplate) {

        List<DocTemplateConfig> configs = docTemplate.getConfigs();
        int length = configs.size();
        if (length == 0) {
            log.info("该模板无配置项，无需进行数据查询");
            return;
        }
        /*  效率是最差的...
        for (DocTemplateConfig config:configs) {

        }*/
        for (int i = 0; i < length; i++) {
            DocTemplateConfig config = configs.get(i);
            String sjyName = config.getSjyname();
            log.info("获取到的数据源名称" + sjyName);
            DocDataSource<Object> dataSource = (DocDataSource<Object>) SpringContextUtil.getBean(sjyName);
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            try {
                result = dataSource.execute(param, config);
            } catch (Exception e) {
                log.error("执行配置项【" + config.getPzxmc() + "】时候出错了，原因如下" + e.getMessage());
                setDefaultValue(param, config);
                continue;
            }

            if (result.size() == 0) {
                log.error("执行配置项【" + config.getPzxmc() + "】时候未查出数据！给与默认值");
                setDefaultValue(param, config);
                continue;
            }
            //设置查询结果集给参数容器赋值
            setValueToContext(result, param, config);

        }


    }

    /**
     * 将查询的结果集给参数赋值
     *
     * @param result
     * @param param
     * @param config
     */
    private void setValueToContext(List<Map<String, Object>> result, Map<String, Object> param, DocTemplateConfig config) {
        String moreCode = config.getMorecode();
        //不是多结果集并且结果类型是集合
        if ("N".equals(moreCode) && "0".equals(config.getJgjlx())) {
            param.put(config.getJgjcode(), result);
        } else {
            //取结果集的map直接放入参数
            Map<String, Object> jgj = result.get(0);
            param.putAll(jgj);
        }
    }

    /**
     * 容错性处理！ 为了让程序继续进行下去
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
                log.info("key【" + keys[i] + "】设置默认值为空");
            }
        } else {
            if ("1".equals(config.getJgjlx())) {
                param.put(config.getJgjcode(), "");
                log.info("key【" + config.getJgjcode() + "】设置默认值为空");
            } else {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                param.put(config.getJgjcode(), list);
                log.info("key【" + config.getJgjcode() + "】设置默认值为空数组");
            }
        }
    }
}
