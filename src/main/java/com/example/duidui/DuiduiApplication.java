package com.example.duidui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.duidui.mapper")
public class DuiduiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuiduiApplication.class, args);
    }

}
