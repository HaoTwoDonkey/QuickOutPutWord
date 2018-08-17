package cn.com.servyou.yjscbg.dao;

import cn.com.servyou.yjscbg.pojo.DocTemplateConfig;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description :sjck数据接口
 * @time : 2018/8/9 12:44
 */
@Component("datasourceSjck")
public class DocDataSourceSjckImpl<T> implements DocDataSource<T> {

    @Autowired
    @Qualifier("sqlSessionFactorySjck")
    protected SqlSessionFactory sqlSessionFactorysjck;

    private SqlSession sqlSession = null;

    /**
     * 获取sqlSession.
     *
     * @return
     */
    public SqlSession getSqlSession() {
        if (sqlSession == null) {
            sqlSession = sqlSessionFactorysjck.openSession();
        }
        return sqlSession;
    }

    @Override
    public List<Map<String, T>> execute(Map<String, Object> param, DocTemplateConfig docTemplateConfig) {
        return getSqlSession().selectList(docTemplateConfig.getNamespace() + "." + docTemplateConfig.getSelectkey(), param);
    }
}
