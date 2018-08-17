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
 * @description :  图片和base64字符串的工具类
 * @time : 2018/7/24 15:45
 */
public class ImageFileUtil {

    /**
     * 本地图片转成 base64字符串
     *
     * @param imgFile
     * @return
     */
    public static String ImageToBase64ByLocal(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 网络图片转成base64字符串
     *
     * @param imgURL
     * @return
     */
    public static String ImageToBase64ByOnline(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 关闭流
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }

    /**
     * base64字符串转换成图片
     *
     * @param imgStr
     * @param imgFilePath
     * @return
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

        if (StringUtil.isEmpty(imgStr)) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
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
