package com.phonemall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.phonemall.mapper")
public class PhoneMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneMallApplication.class, args);
    }
}
