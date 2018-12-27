package com.ke.kob.admin.processor.configuration;

import com.ke.kob.admin.core.model.oz.ProcessorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 下午5:00
 */
@Configuration
public class ProcessorConfiguration {
    @Bean(name = "kobProcessorProperties", initMethod = "initialize")
    @ConfigurationProperties(prefix = "kob.processor")
    public ProcessorProperties prop() {
        return new ProcessorProperties();
    }
}