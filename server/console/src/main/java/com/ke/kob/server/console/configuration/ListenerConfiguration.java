package com.ke.kob.server.console.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/6/15 下午12:04
 */

@Configuration
public class ListenerConfiguration {

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
