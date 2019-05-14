package com.ke.kob.client.spring.core;

import com.ke.kob.basic.model.TaskBaseContext;
import com.ke.kob.basic.model.TaskResult;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:18
 */

public interface TaskRunner {

    /**
     * @param context
     * @return
     * @throws Exception
     */
    TaskResult run(TaskBaseContext context) throws Exception;
}
