package com.ke.kob.client.spring.logger;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/2 下午3:43
 */

public interface KobLogger {

    void debug(String msg);

    void info(String msg);

    void warn(String msg);

    void error(String msg);
}
