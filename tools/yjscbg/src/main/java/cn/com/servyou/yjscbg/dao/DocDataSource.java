package cn.com.servyou.yjscbg.dao;


import cn.com.servyou.yjscbg.pojo.DocTemplateConfig;

import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description : 一键生成报告数据源接口 后续扩展直接实现此接口即可
 * @time : 2018/8/9 12:38
 */
public interface DocDataSource<T> {


    List<Map<String, T>> execute(Map<String, Object> param, DocTemplateConfig docTemplateConfig);

}
