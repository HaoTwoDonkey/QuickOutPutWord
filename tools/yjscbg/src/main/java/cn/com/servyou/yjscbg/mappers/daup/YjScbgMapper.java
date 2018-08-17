package cn.com.servyou.yjscbg.mappers.daup;

import cn.com.servyou.yjscbg.pojo.DocTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :
 * @time : 2018/8/13 10:31
 */
@Repository("yjScbgMapper")
public interface YjScbgMapper {
    //查询获取模板
    DocTemplate queryTemplate(Map<String, Object> param);

    //插入报告信息表保存
    Integer savedoc(Map<String, Object> param);

    //更新报告状态
    void updateDocZt(Map<String, Object> param);

    //获取待删除的文件列表
    List<Map<String, Object>> getDelList(Map<String, Object> param);

    //删除文件 删除信息表数据
    int deleteDoc(Map<String, Object> param);

    //查询报告根据报告序号
    List<Map<String, Object>> getDocByBgxh(Map<String, Object> param);
}
