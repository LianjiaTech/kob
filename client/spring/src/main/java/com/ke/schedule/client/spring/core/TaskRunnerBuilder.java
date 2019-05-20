package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.model.TaskBaseContext;
import com.ke.schedule.basic.model.TaskResult;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:18
 */

public class TaskRunnerBuilder {

    public static Function<TaskBaseContext, TaskResult> build(final Object targetObject, final Method method) {
        return context -> {
            Object[] parameters = new Object[]{context};
            try {
                return (TaskResult) method.invoke(targetObject, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}
