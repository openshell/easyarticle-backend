package com.easyarticle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 后端应用程序入口类
 * 负责启动Spring Boot应用并配置MyBatis Mapper扫描
 */
@SpringBootApplication
@MapperScan("com.easyarticle.repository")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
