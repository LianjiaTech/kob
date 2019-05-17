package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.constant.TaskRecordStateConstant;
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
            final Class<?>[] types = method.getParameterTypes();
            Object[] parameters = new Object[method.getTypeParameters().length];
            for (int i = 0; i < method.getTypeParameters().length; i++) {
                if (types[i] == TaskBaseContext.class) {
                    parameters[i] = context;
                }
            }
            try {
                return invoke(targetObject, method, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    private static TaskResult invoke(Object targetObject, Method targetMethod, Object[] pTypeValues) throws Exception {
        Class<?> returnType = targetMethod.getReturnType();
        if (returnType != TaskResult.class) {
            targetMethod.invoke(targetObject, pTypeValues);
            return new TaskResult(TaskRecordStateConstant.EXECUTE_SUCCESS);
        }
        return (TaskResult) targetMethod.invoke(targetObject, pTypeValues);
    }
}
