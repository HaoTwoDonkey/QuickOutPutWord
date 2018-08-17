package cn.com.servyou.yjscbg.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author : hao
 * @project : daieweb
 * @description : doc 模板bean
 * @time : 2018/8/9 11:16
 */
@Data
public class DocTemplate {

    /**
     * 模板代码
     */
    private String mbdm;
    /**
     * 模板名称
     */
    private String mbmc;
    /**
     * 模板路径
     */
    private String mblj;
    /**
     * 模板类别
     */
    private String mblb;
    /**
     * 是否含有图片
     */
    private String haveImg;
    /**
     * 模板配置代码
     */
    private String mbpzdm;
    /**
     * 模板bean
     */
    private String mbbean;
    /**
     * 是否异步执行
     */
    private String ybzx;
    /**
     * 模板的配置项
     */
    private List<DocTemplateConfig> configs;
}
