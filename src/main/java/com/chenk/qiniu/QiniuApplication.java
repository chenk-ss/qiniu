package com.chenk.qiniu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QiniuApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiniuApplication.class, args);
    }

}
