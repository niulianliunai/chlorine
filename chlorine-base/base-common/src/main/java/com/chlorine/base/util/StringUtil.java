package com.chlorine.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    // 驼峰转下划线
    public static String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String extractUrl(String str) {
//        String code = "leftVal = (screen.width - 1040)/2;topVal = (screen.height - 900)/2;window.open('http://zhejiang.chinatax.gov.cn/art/2024/1/15/art_24411_6071.html','_blank','width=1040,height=900,toolbars=yes,resizable=yes,scrollbars=yes,left='+leftVal+',top='+topVal+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no);return false";

        // 正则表达式，用于匹配URL
        String urlPattern = "'(https?:\\/\\/[^']+)'";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(str);

        // 查找匹配的URL
        while (matcher.find()) {
            String url = matcher.group(1); // 提取匹配的URL（第一个捕获组）
            System.out.println("Extracted URL: " + url);
            return url;
        }
        return null;
    }
    public static List<Integer> splitIntegers(String string, String delimiter) {
        try {
            if (string == null || string.isEmpty() || delimiter == null) {
                return new ArrayList<>();
            }
            List<Integer> res = new ArrayList<>();
            if (!string.contains(delimiter)) {
                res.add(Integer.valueOf(string));
                return res;
            }
            String[] split = string.split(delimiter);
            for (String s : split) {
                res.add(Integer.valueOf(s));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    public static List<String> split(String string, String delimiter) {
        if (string == null || string.isEmpty() || delimiter == null) {
            return new ArrayList<>();
        }
        List<String> res = new ArrayList<>();
        if (!string.contains(delimiter)) {
            res.add(string);
            return res;
        }
        String[] split = string.split(delimiter);
        res.addAll(Arrays.asList(split));
        return res;
    }

    public static String getNumberFromString(String string) {
        String str = string; //"I23love234you3423java";
        str = str.trim();
        StringBuilder str2 = new StringBuilder();  // 此处可以也使用StringBuffer
        if (!"".equals(str)) {
            int count = 0;
            for (int i = 0; i < str.length(); i++) {
                if ((str.charAt(i) >= 48 && str.charAt(i) <= 57) || str.charAt(i) == '.') {
                    String s = String.valueOf(str.charAt(i));
                    str2.append(s);
                    count++;
                } else if (count > 0) {
                    break;
                }
            }
        }
        return str2.toString();
    }

}
