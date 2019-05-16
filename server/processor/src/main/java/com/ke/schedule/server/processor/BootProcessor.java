package com.ke.schedule.server.processor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot 启动类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 下午4:40
 */

@SpringBootApplication(scanBasePackages = {"com.ke"})
@MapperScan("com.ke.schedule.server.core.mapper")
public class BootProcessor implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BootProcessor.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================starting processor==================");
        System.out.println("==================starting processor==================");
        System.out.println("==================starting processor==================");
    }
}