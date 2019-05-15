package com.ke.schedule.demo.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * spring boot 示例
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午5:32
 */
@SpringBootApplication(scanBasePackages = "com.ke")
public @Slf4j class BootDemoMonitor implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BootDemoMonitor.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("============================================");
        System.out.println("================= starting =================");
        System.out.println("============================================");
    }

    @RestController
    public class ApiController {
        @GetMapping("/ping")
        public String ping() {
            return "pong";
        }
    }
}