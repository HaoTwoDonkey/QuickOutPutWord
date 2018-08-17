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
 * @description : 一键生成报告 业务类
 * @time : 2018/8/3 14:46
 */
@Service("yjscbgService")
@Log4j
public class YjScbgServiceImpl implements YjScbgService {

    @Autowired
    private YjScbgMapper yjScbgMapper;

    /**
     * 获取具体解析类 进行创建
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
            log.error("没找到对应bean【" + parserName + "】的解析器!");
            throw new YjscbgException("没找到对应bean【" + parserName + "】的解析器!", param);
        }
        return parser.createDoc(param);
    }

    /**
     * 查询 DocTemplate
     *
     * @param param
     * @return
     */
    @Override
    public DocTemplate queryTemplate(Map<String, Object> param) {
        if (StringUtil.isNullString((String) param.get("MBDM"))) {
            log.error("模板代码为空！请检查请求参数");
            return null;
        }
        DocTemplate template = yjScbgMapper.queryTemplate(param);

        if (template == null) {
            log.error("根据模板代码未查询到该模板信息");
        }
        return template;
    }

    /**
     * 保存记录到 报告信息表
     *
     * @param param
     */
    @Override
    public synchronized void savedoc(Map<String, Object> param) {
        if (StringUtil.isNullString((String) param.get("docPath"))) {
            log.error("报告生成失败，生成路径丢失");
            throw new YjscbgException("报告生成失败，生成路径丢失", param);
        }
        yjScbgMapper.savedoc(param);
        log.info("刚刚保存的报告的BGXH为【" + param.get("primaryKey") + "】");
    }

    /**
     * 更新报告信息表状态
     * 0 正在生成
     * 1 已生成
     * 2 生成失败
     *
     * @param param
     */
    @Override
    public synchronized void updateDocZt(Map<String, Object> param) {
        yjScbgMapper.updateDocZt(param);
    }

    /**
     * 删除历史报告信息及文件
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
            log.error("待删除数量和实际删除数量不一致");
            throw new YjscbgException("待删除数量和实际删除数量不一致");
        }

        for (int i = 0; i < delList.size(); i++) {
            String path = (String) delList.get(i).get("BGBCDZ");
            String bgmc = (String) delList.get(i).get("BGMC");
            Boolean result = DocTypeProducer.deleteFile(new File(param.get("path") + path.replace(bgmc, "")), true);
            if (!result) {
                log.error("删除文件过程中失败。。报告为【"+path+"】");
            }
        }
    }

    /**
     * 下载报告
     *
     * @param param BGXH 报告序号
     * @return
     */
    @Override
    public File doLoadDoc(Map<String, Object> param) {
        List<Map<String, Object>> resultList = yjScbgMapper.getDocByBgxh(param);
        if (resultList.size() == 0) {
            throw new YjscbgException("未查询到改报告。报告序号为【" + param.get("BGXH") + "】");
        }
        String filePath = (String) resultList.get(0).get("BGBCDZ");
        File file = new File(param.get("path") + filePath);
        return file;
    }
}
