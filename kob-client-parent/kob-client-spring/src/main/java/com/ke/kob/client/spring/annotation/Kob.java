package com.ke.kob.client.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 类注解:用于标记此类为作业调度类，里面可能存在用于接受调度的方法
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:09
 */
@Component
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Kob {
}
