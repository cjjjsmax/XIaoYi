package com.example.android;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.example.android.mapper")
public class Application {
        public static void main(String[] args) {
            // 启动 Spring Boot 应用
            SpringApplication.run(Application.class, args);
        }
}
