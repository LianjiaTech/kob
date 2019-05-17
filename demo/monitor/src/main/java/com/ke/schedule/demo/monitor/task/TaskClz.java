package com.ke.schedule.demo.monitor.task;

import com.alibaba.fastjson.JSON;
import com.ke.schedule.basic.model.TaskBaseContext;
import com.ke.schedule.basic.model.TaskResult;
import com.ke.schedule.client.spring.annotation.Kob;
import com.ke.schedule.client.spring.annotation.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午6:14
 */
@Kob
public @Slf4j class TaskClz {

    @Task(key = "helloWorld", remark = "你好旅行者")
    public TaskResult helloWorld(TaskBaseContext context) {
        System.out.println(JSON.toJSONString(context));
        return TaskResult.success();
    }
}
