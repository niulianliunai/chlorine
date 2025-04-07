package com.chlorine.file.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileRenamer {
    public static void main(String[] args) {
        // 1. 指定要修改的文件夹路径
        String folderPath = "/Users/chenlong/Downloads/昭和海报";
        File folder = new File(folderPath);

        // 2. 获取文件夹中的所有文件（排除子目录）
        File[] files = folder.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            System.out.println("文件夹为空或不存在");
            return;
        }

        // 3. 按文件名排序（可根据需求修改排序方式）
        Arrays.sort(files, Comparator.comparing(File::getName));

        // 4. 遍历文件进行重命名
        int counter = 1;
        for (File file : files) {
            // 提取原文件扩展名
            String originalName = file.getName();
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalName.substring(dotIndex);
            }

            // 生成新文件名（两位数字格式）
            String newName = String.format("%02d", counter) + extension;
            File newFile = new File(folderPath, newName);

            // 执行重命名操作
            if (file.renameTo(newFile)) {
                System.out.println("重命名成功: " + originalName + " → " + newName);
                counter++;
            } else {
                System.out.println("重命名失败: " + originalName);
            }
        }
    }
}
