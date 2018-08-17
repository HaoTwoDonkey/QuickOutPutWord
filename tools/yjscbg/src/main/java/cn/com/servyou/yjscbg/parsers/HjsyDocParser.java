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
 * @description : ��ҵ����˰���˰Դ���� ֻ����Ϊ���Ӳο�
 * ������ȫ�Ǵ������ѯ��������
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
        param.put("TITLE", "��ҵ����˰���˰Դ�仯�������");
        param.put("ND", "2018");
        param.put("DESCRIBETION", "2015�������ܶ����100��Ԫ��ͬ������10%�������ܶ����80��ͬ������15%��2016�������ܶ����120��Ԫ��ͬ������20%�������ܶ����90��ͬ������12.5%��2017�������ܶ����150��Ԫ��ͬ������25%�������ܶ����100������11.11%������ͼ��");

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 2015; i < 2019; i++) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put("CXND", i + "��");
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
