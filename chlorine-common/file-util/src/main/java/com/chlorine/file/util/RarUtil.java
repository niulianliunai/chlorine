package com.chlorine.file.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
@Slf4j
public class RarUtil {

    public void extractor(String rarFilePath, String destDirectory) throws IOException, InterruptedException {
        Path rarFile = Paths.get(rarFilePath);
        Path destDir = Paths.get(destDirectory);

        // 参数验证
        validateParameters(rarFile, destDir);

        // 解压RAR文件
        String[] cmd = {"unrar", "x", rarFilePath, destDirectory};
        ProcessBuilder pb = new ProcessBuilder(cmd);

        // 启动进程
        Process p = pb.start();

        // 读取标准输出
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // 日志记录
        log.info("Executing command: {}", Arrays.toString(cmd));
        log.info("Here is the standard output of the command:");
        String s;
        while ((s = stdInput.readLine()) != null) {
            log.info(s);
        }

        log.info("Here is the standard error of the command (if any):");
        while ((s = stdError.readLine()) != null) {
            log.error(s);
        }

        // 等待进程完成
        int exitCode = p.waitFor();

        // 检查解压是否成功
        if (exitCode == 0) {
            log.info("RAR file has been successfully extracted.");
        } else {
            log.error("Failed to extract RAR file. Exit code: {}", exitCode);
            throw new RuntimeException("Failed to extract RAR file. Exit code: " + exitCode);
        }
    }

    private void validateParameters(Path rarFile, Path destDir) throws IOException {
        if (!Files.exists(rarFile)) {
            throw new IllegalArgumentException("RAR file does not exist: " + rarFile);
        }

        if (!Files.isDirectory(destDir)) {
            throw new IllegalArgumentException("Destination directory does not exist: " + destDir);
        }
    }
}
