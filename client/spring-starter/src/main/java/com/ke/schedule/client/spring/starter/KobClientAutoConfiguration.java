package com.ke.schedule.client.spring.starter;

import com.ke.schedule.client.spring.annotation.KobSchedule;
import com.ke.schedule.client.spring.core.ClientProcessor;
import com.ke.schedule.client.spring.startup.AbstractAutoConfiguration;
import com.ke.schedule.client.spring.startup.ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class KobClientAutoConfiguration extends AbstractAutoConfiguration {

    @Bean(name = "kobClientProperties")
    @ConfigurationProperties(prefix = "kob-schedule-client")
    public ClientProperties prop() {
        return new ClientProperties();
    }

    @Order()
    @Bean(name = "kobScheduleProcessor", initMethod = "init", destroyMethod = "destroy")
    public ClientProcessor scheduleProcessor() {
        ClientProperties prop = (ClientProperties) applicationContext.getBean("kobClientProperties");
        return new ClientProcessor(prop.build(), applicationContext.getBeansWithAnnotation(KobSchedule.class));
    }
}
