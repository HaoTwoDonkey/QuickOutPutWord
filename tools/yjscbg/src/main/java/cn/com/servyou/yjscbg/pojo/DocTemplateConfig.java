package cn.com.servyou.yjscbg.pojo;

import lombok.Data;

/**
 * @author : hao
 * @project : daieweb
 * @description :  ģ������bean
 * @time : 2018/8/9 11:16
 */
@Data
public class DocTemplateConfig {
    /**
     * ģ�����ô���
     */
    private String mbpzdm;
    /**
     * ���������
     */
    private String pzxdm;
    /**
     * ����������
     */
    private String pzxmc;
    /**
     * ������ namespace
     */
    private String namespace;
    /**
     * ������ selectKey
     */
    private String selectkey;
    /**
     * ����Դname
     */
    private String sjyname;
    /**
     * �����code
     */
    private String jgjcode;
    /**
     * ��������� 0 ���� 1��ͨ�ֶ�ֵ
     */
    private String jgjlx;
    /**
     * �������Ӧ��code
     */
    private String morecode;
    /**
     * ����������
     */
    private String type;
}
