package cn.com.servyou.yjscbg.services.impl;

import cn.com.jdls.foundation.util.StringUtil;


import cn.com.servyou.yjscbg.docproducer.DocTypeProducer;
import cn.com.servyou.yjscbg.exception.YjscbgException;
import cn.com.servyou.yjscbg.mappers.daup.YjScbgMapper;
import cn.com.servyou.yjscbg.parsers.DocParser;
import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.services.YjScbgService;
import cn.com.servyou.yjscbg.utils.SpringContextUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description : һ�����ɱ��� ҵ����
 * @time : 2018/8/3 14:46
 */
@Service("yjscbgService")
@Log4j
public class YjScbgServiceImpl implements YjScbgService {

    @Autowired
    private YjScbgMapper yjScbgMapper;

    /**
     * ��ȡ��������� ���д���
     *
     * @param param
     * @return
     */
    @Override
    public synchronized File doCreateDoc(Map<String, Object> param) {
        DocTemplate docTemplate = (DocTemplate) param.get("docTemplate");
        String parserName = docTemplate.getMbbean();
        DocParser parser = (DocParser) SpringContextUtil.getBean(parserName);
        if (parser == null) {
            log.error("û�ҵ���Ӧbean��" + parserName + "���Ľ�����!");
            throw new YjscbgException("û�ҵ���Ӧbean��" + parserName + "���Ľ�����!", param);
        }
        return parser.createDoc(param);
    }

    /**
     * ��ѯ DocTemplate
     *
     * @param param
     * @return
     */
    @Override
    public DocTemplate queryTemplate(Map<String, Object> param) {
        if (StringUtil.isNullString((String) param.get("MBDM"))) {
            log.error("ģ�����Ϊ�գ������������");
            return null;
        }
        DocTemplate template = yjScbgMapper.queryTemplate(param);

        if (template == null) {
            log.error("����ģ�����δ��ѯ����ģ����Ϣ");
        }
        return template;
    }

    /**
     * �����¼�� ������Ϣ��
     *
     * @param param
     */
    @Override
    public synchronized void savedoc(Map<String, Object> param) {
        if (StringUtil.isNullString((String) param.get("docPath"))) {
            log.error("��������ʧ�ܣ�����·����ʧ");
            throw new YjscbgException("��������ʧ�ܣ�����·����ʧ", param);
        }
        yjScbgMapper.savedoc(param);
        log.info("�ոձ���ı����BGXHΪ��" + param.get("primaryKey") + "��");
    }

    /**
     * ���±�����Ϣ��״̬
     * 0 ��������
     * 1 ������
     * 2 ����ʧ��
     *
     * @param param
     */
    @Override
    public synchronized void updateDocZt(Map<String, Object> param) {
        yjScbgMapper.updateDocZt(param);
    }

    /**
     * ɾ����ʷ������Ϣ���ļ�
     *
     * @param param
     */
    @Override
    @Transactional
    public synchronized void deleteDoc(Map<String, Object> param) {

        List<Map<String, Object>> delList = yjScbgMapper.getDelList(param);

        if (delList.size() == 0) {
            return;
        }
        int delNum = yjScbgMapper.deleteDoc(param);

        if (delNum != delList.size()) {
            log.error("��ɾ��������ʵ��ɾ��������һ��");
            throw new YjscbgException("��ɾ��������ʵ��ɾ��������һ��");
        }

        for (int i = 0; i < delList.size(); i++) {
            String path = (String) delList.get(i).get("BGBCDZ");
            String bgmc = (String) delList.get(i).get("BGMC");
            Boolean result = DocTypeProducer.deleteFile(new File(param.get("path") + path.replace(bgmc, "")), true);
            if (!result) {
                log.error("ɾ���ļ�������ʧ�ܡ�������Ϊ��"+path+"��");
            }
        }
    }

    /**
     * ���ر���
     *
     * @param param BGXH �������
     * @return
     */
    @Override
    public File doLoadDoc(Map<String, Object> param) {
        List<Map<String, Object>> resultList = yjScbgMapper.getDocByBgxh(param);
        if (resultList.size() == 0) {
            throw new YjscbgException("δ��ѯ���ı��档�������Ϊ��" + param.get("BGXH") + "��");
        }
        String filePath = (String) resultList.get(0).get("BGBCDZ");
        File file = new File(param.get("path") + filePath);
        return file;
    }
}
