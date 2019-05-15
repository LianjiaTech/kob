package com.ke.schedule.basic.model;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/22 上午11:52
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public @NoArgsConstructor @Getter @Setter class RunningTaskInfo {
    private String taskUuid;
    private String relationTaskUuid;
    /**
     * 仅仅用于展示 不准
     */
    private String executeStartTime;
}
