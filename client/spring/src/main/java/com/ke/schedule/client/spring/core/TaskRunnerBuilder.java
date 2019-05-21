package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.basic.model.TaskResult;

import java.lang.reflect.Method;
import java.util.function.Function;

public class TaskRunnerBuilder {

    public static Function<TaskContext, TaskResult> build(final Object targetObject, final Method method) {
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
