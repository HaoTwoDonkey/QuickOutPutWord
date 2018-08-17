package cn.com.servyou.yjscbg.dao;


import cn.com.servyou.yjscbg.pojo.DocTemplate;

import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/9 11:38
 */
public interface DocInterfaceDao {

    void queryAndSetData(Map<String, Object> param, DocTemplate docTemplate);
}
