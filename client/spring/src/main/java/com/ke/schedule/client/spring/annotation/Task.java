package com.ke.schedule.client.spring.annotation;

import java.lang.annotation.*;

/**
 * 方法注解: 用于标识此方法是任务方法，需在@Kob注解内使用
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:10
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Task {
    String key();//任务标识

    String remark();//任务备注
}
