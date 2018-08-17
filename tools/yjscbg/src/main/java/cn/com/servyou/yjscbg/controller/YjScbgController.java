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
 * @description : 一键生成报告控制器
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
     * 生成文件 非异步  直接响应请求
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
     * 异步请求，异步生成文件
     * <p>
     * 更新信息表状态为正在生成
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
        //获取文档名字及后缀
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
            throw new RuntimeException("生成失败");
        }
    }

    /**
     * 删除历史报告
     *
     * @param param   JZRQ 截止日期
     * @param request
     */
    @RequestMapping("doDeleteDoc")
    public void doDeleteDoc(@RequestParam Map<String, Object> param, HttpServletRequest request) {

        if (!param.containsKey("JZRQ")) {
            throw new YjscbgException("未获取截止日期参数,请确保含有JZRQ参数！");
        }

        param.put("path", request.getSession().getServletContext().getRealPath(""));

        yjscbgService.deleteDoc(param);
    }

    /**
     * 下载报告
     *
     * @param param    BGXH 报告序号
     * @param request
     * @param response
     */
    @RequestMapping("doLoadDoc")
    public void doLoadDoc(@RequestParam Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {

        if (!param.containsKey("BGXH")) {
            throw new YjscbgException("未获取报告序号参数,请确保含有BGXH参数！");
        }
        param.put("path", request.getSession().getServletContext().getRealPath(""));

        File file = yjscbgService.doLoadDoc(param);

        OutputFileToBrowser(file, response);
    }

    /**
     * 将文件输出到浏览器 及时下载
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
            // 设置浏览器以下载的方式处理该文件
            // 设置文件名编码解决文件名乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes(), "iso-8859-1"));

            out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("导出失败", e);
            }
        }
    }

    /**
     * 校验参数  同时查询出 DocTemplate
     *
     * @param param
     * @param request
     * @return
     */
    private Boolean voilateParam(Map<String, Object> param, HttpServletRequest request) {
        param.put("swjgdm", request.getSession().getAttribute("qx_swjg_dm"));
        param.put("swrydm", request.getSession().getAttribute("current_swry_dm"));
        //默认生成2007版docx
        if (StringUtil.isNullString((String) param.get("MBLB"))) {
            param.put("MBLB", "2007");
        }
        DocTemplate template = yjscbgService.queryTemplate(param);
        if (template == null) {
            log.error("根据模板代码未查询到该模板信息");
            return false;
        }
        //如果没传Title参数 默认给一个
        if (!param.containsKey("TITLE")) {
            param.put("TITLE", System.currentTimeMillis());
        }

        param.put("docTemplate", template);
        param.put("path", request.getSession().getServletContext().getRealPath(""));
        param.put("urlPri", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());
        return true;
    }


}
