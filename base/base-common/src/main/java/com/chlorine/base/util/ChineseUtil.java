package com.chlorine.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseUtil {
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    public static boolean containsChinese(String text) {
        Matcher matcher = CHINESE_PATTERN.matcher(text);
        return matcher.find();
    }

    public static String extractChinese(String text) {
        Matcher matcher = CHINESE_PATTERN.matcher(text);
        StringBuilder chineseBuffer = new StringBuilder();
        while (matcher.find()) {
//            matcher.appendReplacement(chineseBuffer, "");
            chineseBuffer.append(matcher.group(0));
        }
//        matcher.appendTail(chineseBuffer);
        return chineseBuffer.toString();
    }
}
