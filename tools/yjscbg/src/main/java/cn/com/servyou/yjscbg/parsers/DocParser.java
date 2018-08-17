package cn.com.servyou.yjscbg.parsers;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description : 文档解析器接口
 * @time : 2018/8/3 15:03
 */
@Component
public interface DocParser {

    File createDoc(Map<String, Object> param);

    void getDataFromTemplateConfig(Map<String, Object> param);

    void setImgDataFor2007(Map<String, Object> param);

    void setImgDataFor2003(Map<String, Object> param);
}
