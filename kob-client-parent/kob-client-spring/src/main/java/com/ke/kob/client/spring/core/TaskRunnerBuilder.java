package com.ke.kob.client.spring.core;

import com.ke.kob.basic.constant.TaskRecordStateConstant;
import com.ke.kob.basic.model.TaskBaseContext;
import com.ke.kob.basic.model.TaskResult;

import java.lang.reflect.Method;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:18
 */

public class TaskRunnerBuilder {

    public static TaskRunner build(final Object targetObject, final Method targetMethod, final Class<?>[] pTypes) {
        return new TaskRunner() {
            @Override
            public TaskResult run(TaskBaseContext context) throws Exception {
                if (pTypes == null || pTypes.length == 0) {
                    return invoke(targetObject, targetMethod);
                }

                Object[] pTypeValues = new Object[pTypes.length];
                for (int i = 0; i < pTypes.length; i++) {
                    if (pTypes[i] == TaskBaseContext.class) {
                        pTypeValues[i] = context;
                    } else {
                        pTypeValues[i] = null;
                    }
                }
                return invoke(targetObject, targetMethod, pTypeValues);
            }
        };
    }

    private static TaskResult invoke(Object targetObject, Method targetMethod) throws Exception {
        Class<?> returnType = targetMethod.getReturnType();
        if (returnType != TaskResult.class) {
            targetMethod.invoke(targetObject);
            return new TaskResult(TaskRecordStateConstant.EXECUTE_SUCCESS);
        } else {
            return (TaskResult) targetMethod.invoke(targetObject);
        }
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
