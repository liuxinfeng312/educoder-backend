package com.example.educoder_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /uploads/** 到项目根目录下的 uploads 文件夹
        String baseDir = System.getProperty("user.dir");
        String uploadDir = "file:" + baseDir + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }
}