package cn.com.servyou.yjscbg.utils;

import cn.com.jdlssoft.rbac.util.StringUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : hao
 * @project : daieweb
 * @description :  ͼƬ��base64�ַ����Ĺ�����
 * @time : 2018/7/24 15:45
 */
public class ImageFileUtil {

    /**
     * ����ͼƬת�� base64�ַ���
     *
     * @param imgFile
     * @return
     */
    public static String ImageToBase64ByLocal(String imgFile) {// ��ͼƬ�ļ�ת��Ϊ�ֽ������ַ��������������Base64���봦��

        InputStream in = null;
        byte[] data = null;
        // ��ȡͼƬ�ֽ�����
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ���ֽ�����Base64����
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// ����Base64��������ֽ������ַ���
    }

    /**
     * ����ͼƬת��base64�ַ���
     *
     * @param imgURL
     * @return
     */
    public static String ImageToBase64ByOnline(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // ����URL
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];
            // ��������
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // �����ݶ�ȡ�ڴ���
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // �ر���
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ���ֽ�����Base64����
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }

    /**
     * base64�ַ���ת����ͼƬ
     *
     * @param imgStr
     * @param imgFilePath
     * @return
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // ���ֽ������ַ�������Base64���벢����ͼƬ

        if (StringUtil.isEmpty(imgStr)) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64����
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// �����쳣����
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public static void main(String[] args) {
        String url = "C:/1.jpg";
        String str = ImageFileUtil.ImageToBase64ByLocal(url);
        System.out.println(str);
        ImageFileUtil.Base64ToImage(str, "C:/2.jpg");
    }

}
