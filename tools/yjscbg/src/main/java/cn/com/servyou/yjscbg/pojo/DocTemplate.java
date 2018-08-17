package cn.com.servyou.yjscbg.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author : hao
 * @project : daieweb
 * @description : doc ģ��bean
 * @time : 2018/8/9 11:16
 */
@Data
public class DocTemplate {

    /**
     * ģ�����
     */
    private String mbdm;
    /**
     * ģ������
     */
    private String mbmc;
    /**
     * ģ��·��
     */
    private String mblj;
    /**
     * ģ�����
     */
    private String mblb;
    /**
     * �Ƿ���ͼƬ
     */
    private String haveImg;
    /**
     * ģ�����ô���
     */
    private String mbpzdm;
    /**
     * ģ��bean
     */
    private String mbbean;
    /**
     * �Ƿ��첽ִ��
     */
    private String ybzx;
    /**
     * ģ���������
     */
    private List<DocTemplateConfig> configs;
}
