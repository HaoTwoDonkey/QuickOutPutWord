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
 * @description :  demo ����Ҫ�Ӻ�̨��ȡ����  Ҳ��Ҫ������ƴ������
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
     * ����Ĭ�ϲ�ѯ
     *
     * @param param
     */
    @Override
    public void getDataFromTemplateConfig(Map<String, Object> param) {
        docDefaultDaoImpl.queryAndSetData(param, (DocTemplate) param.get("docTemplate"));
    }

    @Override
    public void addDataToTemplate(Map<String, Object> param) {
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
