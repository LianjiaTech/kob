package com.ke.schedule.client.spring.startup;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午1:26
 */

public abstract class AbstractAutoConfiguration implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}