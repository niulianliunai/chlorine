package com.chlorine.base.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class FileUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

//    public static void exportFile(JSONObject data, String name, HttpServletResponse response) {
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE+";charset=utf-8");
//
//        // 禁用缓存
//        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
//        response.setDateHeader("Expires", 0); // Proxies.
//        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
//
//        // 将对象转换为JSON字符串并写入响应输出流
//        try (Writer writer = response.getWriter()) {
//            objectMapper.writeValue(writer, data);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static String readFile(MultipartFile multipartFile) {
//        try {
//            byte[] bytes = multipartFile.getBytes();
//            return new String(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
