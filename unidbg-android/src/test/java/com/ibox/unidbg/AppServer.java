package com.ibox.unidbg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppServer {

    // 启动springboot服务
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AppServer.class);
        app.run(args);
    }
}
