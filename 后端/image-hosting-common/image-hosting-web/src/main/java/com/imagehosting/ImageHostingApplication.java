package com.imagehosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 图床系统后端启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.imagehosting.dao")
public class ImageHostingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageHostingApplication.class, args);
    }
} 