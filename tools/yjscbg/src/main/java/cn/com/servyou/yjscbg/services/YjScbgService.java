package cn.com.servyou.yjscbg.services;


import cn.com.servyou.yjscbg.pojo.DocTemplate;

import java.io.File;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/3 14:45
 */
public interface YjScbgService {
    //创建报告
    File doCreateDoc(Map<String, Object> param);

    //查询获取模板
    DocTemplate queryTemplate(Map<String, Object> param);

    //插入报告信息表保存
    void savedoc(Map<String, Object> param);

    //更新报告状态
    void updateDocZt(Map<String, Object> param);

    //删除报告 根据传入的时间节点 删除以前的所有的文件
    void deleteDoc(Map<String, Object> param);

    //下载报告
    File doLoadDoc(Map<String, Object> param);
}
