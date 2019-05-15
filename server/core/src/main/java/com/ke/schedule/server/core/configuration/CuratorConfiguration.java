package com.ke.schedule.server.core.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zk配置类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 上午11:00
 */
@Configuration
public class CuratorConfiguration {

    @Value("${kob-schedule.zk-connect-string}")
    private String connect;

    @Bean
    public CuratorFramework curator() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connect)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000);
        CuratorFramework client = builder.build();
        client.start();
        return client;
    }

}
