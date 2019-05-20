package com.ke.schedule.client.spring.logger;

import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.model.TaskBaseContext;
import com.ke.schedule.basic.support.OkHttpUtils;
import com.ke.schedule.client.spring.core.ClientContext;
import okhttp3.RequestBody;

/**
 * @author zhaoyuguang
 */

public enum OkHttpLogger {
    //INSTANCE
    INSTANCE;

    public void expireRecycling(ClientContext context, TaskBaseContext.Path path) {
        RequestBody body = new LoggerBuilder()
                .client(context.getData())
                .state(TaskRecordStateConstant.EXPIRE_RECYCLING)
                .path(path).build();
        OkHttpUtils.post(context.getAdminUrl(), body);
    }

    public void systemLog(ClientContext context, TaskBaseContext.Path path, Integer state) {
        RequestBody body = new LoggerBuilder()
                .client(context.getData())
                .state(state)
                .path(path).build();
        OkHttpUtils.post(context.getAdminUrl(), body);
    }
}
