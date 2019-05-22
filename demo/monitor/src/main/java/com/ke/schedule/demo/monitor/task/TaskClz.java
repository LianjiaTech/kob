package com.ke.schedule.demo.monitor.task;

import com.alibaba.fastjson.JSON;
import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.basic.model.TaskResult;
import com.ke.schedule.client.spring.annotation.KobSchedule;
import com.ke.schedule.client.spring.annotation.Task;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午6:14
 */
@KobSchedule
public @Slf4j class TaskClz {

    @Task(key = "helloWorld", remark = "哈喽")
    public TaskResult helloWorld(TaskContext context) {
        System.out.println("日志 =============");
        System.out.println("日志 =============");
        System.out.println("日志 =============");
        System.out.println(JSON.toJSONString(context));
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TaskResult.success("helloWorld");
    }
}
