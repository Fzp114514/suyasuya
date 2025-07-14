package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 通过 @Value 注解从配置文件中读取路径
    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    @Value("${upload.video.path}")
    private String videoUploadPath;

    @Value("${upload.cover.path}")
    private String coverUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 资源映射路径
         * addResourceHandler：访问映射路径
         * addResourceLocations：资源绝对路径
         */
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:///" + avatarUploadPath + "/");
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:///" + videoUploadPath + "/");
        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:///" + coverUploadPath + "/");
    }
}