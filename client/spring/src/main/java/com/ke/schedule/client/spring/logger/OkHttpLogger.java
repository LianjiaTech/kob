package com.ke.schedule.client.spring.logger;

import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.TaskBaseContext;
import com.ke.schedule.basic.support.OkHttpUtils;

/**
 * @author zhaoyuguang
 */

public enum OkHttpLogger {
    //1
    INSTANCE;

    public void expireRecycling(ClientData client, TaskBaseContext.Path path) {
        new LoggerBuilder().client(client)
        OkHttpUtils.post()
    }
}
