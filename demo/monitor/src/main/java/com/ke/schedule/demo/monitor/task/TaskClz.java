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

    @Task(key = "standardSuccessMethod", remark = "标准成功方法")
    public TaskResult standardSuccessMethod(TaskBaseContext context) {
        System.out.println(JSON.toJSONString(context));
        return TaskResult.success();
    }

    @Task(key = "sleep120sMethod", remark = "睡120秒任务")
    public TaskResult sleep120sMethod(TaskBaseContext context) {
        System.out.println(JSON.toJSONString(context));
        try {
            TimeUnit.SECONDS.sleep(120);
        } catch (InterruptedException e) {
            log.error("sleep120sMethod_error", e);
            return TaskResult.fail(e);
        }
        return TaskResult.success();
    }

    @Task(key = "standardFailMethod", remark = "标准失败方法")
    public TaskResult standardFailMethod(TaskBaseContext context) {
        System.out.println(JSON.toJSONString(context));
        try {
            TimeUnit.SECONDS.sleep(12);
        } catch (InterruptedException e) {
            log.error("sleep120sMethod_error", e);
            return TaskResult.fail(e);
        }
        return TaskResult.fail("fail");
    }

    @Task(key = "corruptMethod", remark = "不标准方法")
    public void corruptMethod() {
        System.out.println("corruptMethod");
    }
}
