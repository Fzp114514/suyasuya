package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FfmpegConfig {
    private static String ffprobePath;

    @Value("${video.ffmpeg-path}")
    private String tempFfprobePath;

    @PostConstruct
    public void init() {
        ffprobePath = tempFfprobePath;
    }

    public static String getFfprobePath() {
        return ffprobePath;
    }
}
