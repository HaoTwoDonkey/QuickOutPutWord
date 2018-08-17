package cn.com.servyou.yjscbg.controller;

import cn.com.jdls.foundation.util.StringUtil;
import cn.com.servyou.yjscbg.exception.YjscbgException;
import cn.com.servyou.yjscbg.pojo.DocTemplate;
import cn.com.servyou.yjscbg.services.YjScbgAnyscService;
import cn.com.servyou.yjscbg.services.YjScbgService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author : hao
 * @project : daieweb
 * @description : һ�����ɱ��������
 * @time : 2018/8/3 14:43
 */
@RestController
@RequestMapping("/yjscbg/")
@Log4j
public class YjScbgController {

    @Autowired
    private YjScbgService yjscbgService;

    @Autowired
    private YjScbgAnyscService yjscbgAnyscService;

    /**
     * �����ļ� ���첽  ֱ����Ӧ����
     *
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("doCreateDoc")
    public void doCreateDoc(@RequestParam Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
        if (!voilateParam(param, request)) {
            return;
        }
        File file = yjscbgService.doCreateDoc(param);

        param.put("BGSCZT", "1");
        yjscbgService.savedoc(param);

        OutputFileToBrowser(file, response);

    }

    /**
     * �첽�����첽�����ļ�
     * <p>
     * ������Ϣ��״̬Ϊ��������
     *
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("doCreateDocAsync")
    public void doCreateDocAsync(@RequestParam Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
        if (!voilateParam(param, request)) {
            return;
        }
        param.put("docPath", "noPath");
        param.put("BGSCZT", "0");
        //��ȡ�ĵ����ּ���׺
        if (!param.containsKey("docName")) {
            if ("2003".equals(param.get("MBLB"))) {
                param.put("docName", param.get("TITLE") + ".doc");
            } else {
                param.put("docName", param.get("TITLE") + ".docx");
            }
        }

        yjscbgService.savedoc(param);
        try {
            yjscbgAnyscService.createDoc(param);
        } catch (InterruptedException e) {
            throw new RuntimeException("����ʧ��");
        }
    }

    /**
     * ɾ����ʷ����
     *
     * @param param   JZRQ ��ֹ����
     * @param request
     */
    @RequestMapping("doDeleteDoc")
    public void doDeleteDoc(@RequestParam Map<String, Object> param, HttpServletRequest request) {

        if (!param.containsKey("JZRQ")) {
            throw new YjscbgException("δ��ȡ��ֹ���ڲ���,��ȷ������JZRQ������");
        }

        param.put("path", request.getSession().getServletContext().getRealPath(""));

        yjscbgService.deleteDoc(param);
    }

    /**
     * ���ر���
     *
     * @param param    BGXH �������
     * @param request
     * @param response
     */
    @RequestMapping("doLoadDoc")
    public void doLoadDoc(@RequestParam Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {

        if (!param.containsKey("BGXH")) {
            throw new YjscbgException("δ��ȡ������Ų���,��ȷ������BGXH������");
        }
        param.put("path", request.getSession().getServletContext().getRealPath(""));

        File file = yjscbgService.doLoadDoc(param);

        OutputFileToBrowser(file, response);
    }

    /**
     * ���ļ����������� ��ʱ����
     *
     * @param file
     * @param response
     */
    private void OutputFileToBrowser(File file, HttpServletResponse response) {
        InputStream fin = null;
        OutputStream out = null;
        try {
            fin = new FileInputStream(file);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            // ��������������صķ�ʽ������ļ�
            // �����ļ����������ļ�����������
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes(), "iso-8859-1"));

            out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesToRead = -1;
            // ͨ��ѭ���������Word�ļ�������������������
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } catch (Exception e) {
            throw new RuntimeException("����ʧ��", e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("����ʧ��", e);
            }
        }
    }

    /**
     * У�����  ͬʱ��ѯ�� DocTemplate
     *
     * @param param
     * @param request
     * @return
     */
    private Boolean voilateParam(Map<String, Object> param, HttpServletRequest request) {
        param.put("swjgdm", request.getSession().getAttribute("qx_swjg_dm"));
        param.put("swrydm", request.getSession().getAttribute("current_swry_dm"));
        //Ĭ������2007��docx
        if (StringUtil.isNullString((String) param.get("MBLB"))) {
            param.put("MBLB", "2007");
        }
        DocTemplate template = yjscbgService.queryTemplate(param);
        if (template == null) {
            log.error("����ģ�����δ��ѯ����ģ����Ϣ");
            return false;
        }
        //���û��Title���� Ĭ�ϸ�һ��
        if (!param.containsKey("TITLE")) {
            param.put("TITLE", System.currentTimeMillis());
        }

        param.put("docTemplate", template);
        param.put("path", request.getSession().getServletContext().getRealPath(""));
        param.put("urlPri", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());
        return true;
    }


}
