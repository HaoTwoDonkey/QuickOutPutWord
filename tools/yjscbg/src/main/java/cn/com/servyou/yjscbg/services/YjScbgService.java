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
    //��������
    File doCreateDoc(Map<String, Object> param);

    //��ѯ��ȡģ��
    DocTemplate queryTemplate(Map<String, Object> param);

    //���뱨����Ϣ����
    void savedoc(Map<String, Object> param);

    //���±���״̬
    void updateDocZt(Map<String, Object> param);

    //ɾ������ ���ݴ����ʱ��ڵ� ɾ����ǰ�����е��ļ�
    void deleteDoc(Map<String, Object> param);

    //���ر���
    File doLoadDoc(Map<String, Object> param);
}
