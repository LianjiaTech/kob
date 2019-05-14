package com.ke.kob.server.core.service;

import com.ke.kob.basic.model.LogContext;

/**
 * 日志service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/17 下午12:11
 */

public interface CollectService {

    /**
     * 处理日志信息
     *
     * @param context 上报日志
     */
    void handleLogger(LogContext context);
}
