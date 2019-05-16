package com.ke.schedule.basic.model;

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
