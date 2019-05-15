package com.ke.schedule.client.spring.model;

import com.ke.schedule.basic.model.TaskBaseContext;
import com.ke.schedule.client.spring.logger.KobLogger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/9 下午5:31
 */

public @NoArgsConstructor @Getter @Setter class TaskContext extends TaskBaseContext {
    /**
     * 日志上报接口 目前使用http实现 详见：com.ke.kob.client.spring.logger.KobOkLogger
     */
    private @Getter @Setter KobLogger logger;
}
