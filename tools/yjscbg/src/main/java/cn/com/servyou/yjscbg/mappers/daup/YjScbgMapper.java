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
    //��ѯ��ȡģ��
    DocTemplate queryTemplate(Map<String, Object> param);

    //���뱨����Ϣ����
    Integer savedoc(Map<String, Object> param);

    //���±���״̬
    void updateDocZt(Map<String, Object> param);

    //��ȡ��ɾ�����ļ��б�
    List<Map<String, Object>> getDelList(Map<String, Object> param);

    //ɾ���ļ� ɾ����Ϣ������
    int deleteDoc(Map<String, Object> param);

    //��ѯ������ݱ������
    List<Map<String, Object>> getDocByBgxh(Map<String, Object> param);
}
