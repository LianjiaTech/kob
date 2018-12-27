package com.ke.kob.admin.core.configuration;

import com.ke.kob.admin.core.model.oz.CoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置对象
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 下午12:02
 */
@Configuration
public class CoreConfiguration {

    @Bean(name = "kobCoreProperties", initMethod = "initialize")
    @ConfigurationProperties(prefix = "kob.core")
    public CoreProperties prop() {
        return new CoreProperties();
    }
}
