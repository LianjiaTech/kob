package com.ke.kob.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * SpringBoot 启动类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/28 下午3:10
 */

@SpringBootApplication(scanBasePackages = {"com.ke.kob"})
@MapperScan("com.ke.kob.admin.core.mapper")
@ServletComponentScan
public class BootAll implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BootAll.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================starting all==================");
        System.out.println("==================starting all==================");
        System.out.println("==================starting all==================");
    }
}
