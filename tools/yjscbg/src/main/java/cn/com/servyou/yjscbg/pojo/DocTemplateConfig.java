package cn.com.servyou.yjscbg.pojo;

import lombok.Data;

/**
 * @author : hao
 * @project : daieweb
 * @description :  模板配置bean
 * @time : 2018/8/9 11:16
 */
@Data
public class DocTemplateConfig {
    /**
     * 模板配置代码
     */
    private String mbpzdm;
    /**
     * 配置项代码
     */
    private String pzxdm;
    /**
     * 配置项名称
     */
    private String pzxmc;
    /**
     * 配置项 namespace
     */
    private String namespace;
    /**
     * 配置项 selectKey
     */
    private String selectkey;
    /**
     * 数据源name
     */
    private String sjyname;
    /**
     * 结果集code
     */
    private String jgjcode;
    /**
     * 结果集类型 0 集合 1普通字段值
     */
    private String jgjlx;
    /**
     * 结果集对应多code
     */
    private String morecode;
    /**
     * 配置项类型
     */
    private String type;
}
