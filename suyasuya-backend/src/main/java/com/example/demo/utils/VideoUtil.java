package com.example.demo.utils;

import com.example.demo.config.FfmpegConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
public class VideoUtil {
    String ffprobePath = FfmpegConfig.getFfprobePath(); // 从配置类获取路径

    /**
     * 获取视频时长（单位：秒）
     * @param videoPath 视频文件绝对路径
     * @return 时长（秒），失败返回 -1
     */
    public static int getVideoDuration(Path videoPath) {
        String ffprobePath = FfmpegConfig.getFfprobePath(); // 从配置类获取路径

        try {
            // 构建 FFmpeg 命令
            ProcessBuilder pb = new ProcessBuilder(
                    ffprobePath,
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoPath.toAbsolutePath().toString()
            );

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()) // 读取标准输出
            )) {
                String durationLine = reader.readLine();
                if (durationLine != null) {
                    return (int) Math.round(Double.parseDouble(durationLine.trim()));
                }
            }

            // 检查命令执行状态
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("FFprobe 异常退出，代码: " + exitCode);
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
            System.err.println("解析视频时长失败: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}