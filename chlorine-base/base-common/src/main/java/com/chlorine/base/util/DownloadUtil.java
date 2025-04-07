package com.chlorine.base.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
public class DownloadUtil {
    // 从 URL 下载图片并将其转换为 MultipartFile
    public static MultipartFile downloadImage(String imageUrl) throws IOException {
        // 通过 URL 连接下载图片
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");  // 设置浏览器代理（避免被防爬虫阻止）
        InputStream inputStream = connection.getInputStream();

        // 获取图片文件名（可以根据实际情况自定义）
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        // 将图片输入流转换为 MultipartFile
        MultipartFile multipartFile = new MockMultipartFile(
                fileName,
                fileName,
                "image/jpeg",
                inputStream.readAllBytes()
        );

        // 关闭流
        inputStream.close();

        return multipartFile;
    }

    // 测试
    public static void main(String[] args) {
        try {
            String imageUrl = "https://mall.leyifan.com/static/html/pc.html#/subPackages/pagesOther/transaction_snapshot/index?product_id=22032530&pwd=5zC7%2FxCRiW3RTTDrBM83Zdm7q8d6TcowVaWu4PtgbLk%3D"; // 替换为商品的图片 URL
            MultipartFile file = downloadImage(imageUrl);
            System.out.println("文件名：" + file.getOriginalFilename());
            System.out.println("文件大小：" + file.getSize());
            // 可以进一步处理这个 MultipartFile 对象
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] downLoadByUrl(String urlStr) {

        try {
            Thread.sleep((long) (1000 * Math.random() + 1000L));
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(600000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            //文件保存位置
            inputStream.close();
            System.out.println("the file: " + url + " download success");

            return getData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr url的路径
     * @throws IOException
     */
    public static void downLoadByUrl(String urlStr, String savePath) {

        try {
            Thread.sleep((long) (1000 * Math.random() + 1000L));
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            //文件保存位置
            File saveDir = new File(savePath.substring(0, savePath.lastIndexOf(File.separator)));
            if (!saveDir.exists()) { // 没有就创建该文件
                saveDir.mkdir();
            }
            File file = new File(savePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);

            fos.close();
            inputStream.close();
            System.out.println("the file: " + url + " download success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


}
