package cn.com.servyou.yjscbg.parsers;

import cn.com.servyou.yjscbg.parsers.AbstractDocParser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author : hao
 * @project : daieweb
 * @description : 企业所得税汇缴税源报告 只是作为例子参考
 * 数据完全是从这里查询并且填充的
 * @time : 2018/8/6 16:08
 */
@Component("hjsyDocParser")
public class HjsyDocParser extends AbstractDocParser {
    @Override
    public void setImgDataFor2003(Map<String, Object> param) {
        executeJs("showImg()");
        String base64 = getImgBase64("return returnEchartImg(barChart)");
        param.put("barChart", base64);
    }

    @Override
    public void setImgDataFor2007(Map<String, Object> param) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executeJs("showImg()");
        getImgBase64AndSave("return returnEchartImg(barChart)", param, "barChart", "png");
    }

    @Override
    public void addDataToTemplate(Map<String, Object> param) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        param.put("TITLE", "企业所得税汇缴税源变化情况报告");
        param.put("ND", "2018");
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
