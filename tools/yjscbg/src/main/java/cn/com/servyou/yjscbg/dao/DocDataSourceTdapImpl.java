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
 * @description : tdap���ݽӿ�
 * @time : 2018/8/9 12:46
 */
@Component("datasourceTdap")
public class DocDataSourceTdapImpl<T> implements DocDataSource<T> {

    @Autowired
    @Qualifier("sqlSessionFactory")
    protected SqlSessionFactory sqlSessionFactorytdap;

    private SqlSession sqlSession = null;

    /**
     * ��ȡsqlSession.
     *
     * @return
     */
    public SqlSession getSqlSession() {
        if (sqlSession == null) {
            sqlSession = sqlSessionFactorytdap.openSession();
        }
        return sqlSession;
    }

    @Override
    public List<Map<String, T>> execute(Map<String, Object> param, DocTemplateConfig docTemplateConfig) {
        return getSqlSession().selectList(docTemplateConfig.getNamespace() + "." + docTemplateConfig.getSelectkey(), param);
    }
}
