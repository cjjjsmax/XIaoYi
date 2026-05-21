package com.example.android.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web配置类
 * 配置静态资源映射和跨域设置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器
     * 将 /images/** 请求映射到 uploads/images/ 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String canonicalPath;
        try {
            canonicalPath = new File("").getCanonicalPath();
        } catch (Exception e) {
            canonicalPath = System.getProperty("user.dir");
        }

        // 与上传接口使用完全相同的路径
        String uploadPath = canonicalPath + "/demo/uploads/images";
        
        // 确保目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        System.out.println("静态资源映射路径: " + uploadPath);
        System.out.println("检测到上传目录存在: " + uploadDir.exists());

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(0);
    }

    /**
     * 配置跨域设置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
