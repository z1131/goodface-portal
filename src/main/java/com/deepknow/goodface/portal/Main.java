package com.deepknow.goodface.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

@SpringBootApplication
@EnableDubbo
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}