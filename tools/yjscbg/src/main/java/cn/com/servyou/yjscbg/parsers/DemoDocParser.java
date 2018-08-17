package cn.com.servyou.yjscbg.parsers;

import cn.com.servyou.tdap.web.PagerBean;
import cn.com.servyou.yjscbg.dao.DocInterfaceDao;
import cn.com.servyou.yjscbg.pojo.DocTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :  demo 既需要从后台读取数据  也需要解析类拼凑数据
 * @time : 2018/8/3 15:45
 */
@Component("demoDocParser")
public class DemoDocParser extends AbstractDocParser {

    @Autowired
    private DocInterfaceDao docDefaultDaoImpl;

    @Override
    public void setImgDataFor2003(Map<String, Object> param) {

    }

    @Override
    public void setImgDataFor2007(Map<String, Object> param) {
        executeJs("showImg()");
        getImgBase64AndSave("return returnEchartImg(barChart)", param, "barChart", "png");
    }

    /**
     * 进行默认查询
     *
     * @param param
     */
    @Override
    public void getDataFromTemplateConfig(Map<String, Object> param) {
        docDefaultDaoImpl.queryAndSetData(param, (DocTemplate) param.get("docTemplate"));
    }

    @Override
    public void addDataToTemplate(Map<String, Object> param) {
        param.put("DESCRIBETION", "2015年收入总额完成100万元，同比增长10%，利润总额完成80万，同比增长15%；2016年收入总额完成120万元，同比增长20%，林润总额完成90万，同比增长12.5%；2017年收入总额完成150万元，同比增长25%，利润总额完成100万，增长11.11%。如下图：");
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 2015; i < 2019; i++) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put("CXND", i + "年");
            map.put("SRZE", String.valueOf(Math.random() * 100));
            map.put("TBZZL", Math.random() * 100 + "%");
            map.put("LRZE", String.valueOf(Math.random() * 100));
            map.put("TBZJL", Math.random() * 100 + "%");
            data.add(map);
        }
        param.put("dataList", data);

    }

    @Override
    protected String getPageUrl() {
        return "/tools/yjscbg/pages/yjscbg/qysds_yjscbg_hjsybh.html";
    }
}
