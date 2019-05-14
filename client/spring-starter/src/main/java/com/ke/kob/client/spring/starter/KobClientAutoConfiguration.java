package com.ke.kob.client.spring.starter;

import com.ke.kob.client.spring.annotation.Kob;
import com.ke.kob.client.spring.core.ClientProcessor;
import com.ke.kob.client.spring.startup.AbstractAutoConfiguration;
import com.ke.kob.client.spring.startup.ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * KOB作业调度 springboot自动装配类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午2:45
 */
@Configuration
public class KobClientAutoConfiguration extends AbstractAutoConfiguration {

    @Bean(name = "kobClientProperties")
    @ConfigurationProperties(prefix = "kob.client")
    public ClientProperties prop() {
        return new ClientProperties();
    }

    @Order()
    @Bean(name = "kobScheduleProcessor", initMethod = "init", destroyMethod = "destroy")
    public ClientProcessor scheduleProcessor() {
        ClientProperties prop = (ClientProperties) applicationContext.getBean("kobClientProperties");
        prop.build();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Kob.class);
        return new ClientProcessor(prop, beans);
    }
}
