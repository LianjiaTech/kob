package com.ke.schedule.client.spring.logger;

import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.basic.support.OkHttpUtils;
import com.ke.schedule.client.spring.core.ClientContext;
import okhttp3.RequestBody;

/**
 * @author zhaoyuguang
 */

public enum OkHttpLogger {
    //INSTANCE
    INSTANCE;

    public void systemLog(ClientContext context, TaskContext.Path path, Integer state) {
        RequestBody body = new LoggerBuilder()
                .now()
                .uuid()
                .client(context.getData())
                .state(state)
                .taskUuid(path.getTaskUuid())
                .build();
        OkHttpUtils.post(context.getAdminUrl(), body);
    }

    public void systemLog(ClientContext context, TaskContext.Path path, Integer state, String msg) {
        RequestBody body = new LoggerBuilder()
                .now()
                .uuid()
                .client(context.getData())
                .state(state)
                .taskUuid(path.getTaskUuid())
                .setMessage(msg)
                .build();
        OkHttpUtils.post(context.getAdminUrl(), body);
    }

    public void systemLog(ClientContext context, TaskContext.Path path, Integer state, Throwable t) {
        RequestBody body = new LoggerBuilder()
                .now()
                .uuid()
                .client(context.getData())
                .state(state)
                .taskUuid(path.getTaskUuid())
                .setMessage(t)
                .build();
        OkHttpUtils.post(context.getAdminUrl(), body);
    }
}
