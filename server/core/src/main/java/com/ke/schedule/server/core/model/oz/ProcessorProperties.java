package com.ke.schedule.server.core.model.oz;

import com.ke.schedule.server.core.common.AdminConstant;
import com.ke.schedule.basic.support.KobUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 下午4:44
 */

public @NoArgsConstructor @Getter @Setter class ProcessorProperties implements Serializable {
    private static final long serialVersionUID = 6822163291844608036L;
    private Integer taskPushThreads;
    private Integer intervalMin;
    private Integer waitingTaskScroll;
    private Long cronTaskExecutorInitialDelaySec;
    private Long cronTaskExecutorPeriodSec;
    private Long waitingTaskExecutorInitialDelayMs;
    private Long waitingTaskExecutorPeriodMs;
    private Integer taskOverstockRecoveryWeight;
    private Integer taskOverstockRecoveryThreshold;
    private Integer taskOverstockRecoveryRetainCount;
    private Boolean appendPreviousTask;
    private Long heartbeatInitialDelaySec;
    private Long heartbeatPeriodSec;

    private void initialize() {
        if(KobUtils.isEmpty(this.taskPushThreads)){
            this.taskPushThreads = AdminConstant.DEFAULT_TASK_PUSH_THREADS;
        }
        if(KobUtils.isEmpty(this.intervalMin)){
            this.intervalMin = AdminConstant.DEFAULT_INTERVAL_MIN;
        }
        if(KobUtils.isEmpty(this.waitingTaskScroll)){
            this.waitingTaskScroll = AdminConstant.DEFAULT_WAITING_TASK_SCROLL;
        }
        if(KobUtils.isEmpty(this.cronTaskExecutorInitialDelaySec)){
            this.cronTaskExecutorInitialDelaySec = AdminConstant.DEFAULT_CRON_TASK_EXECUTOR_INITIAL_DELAY_SEC;
        }
        if(KobUtils.isEmpty(this.cronTaskExecutorPeriodSec)){
            this.cronTaskExecutorPeriodSec = AdminConstant.DEFAULT_CRON_TASK_EXECUTOR_PERIOD_SEC;
        }
        if(KobUtils.isEmpty(this.waitingTaskExecutorInitialDelayMs)){
            this.waitingTaskExecutorInitialDelayMs = AdminConstant.DEFAULT_WAITING_TASK_EXECUTOR_INITIAL_DELAY_MS;
        }
        if(KobUtils.isEmpty(this.waitingTaskExecutorPeriodMs)){
            this.waitingTaskExecutorPeriodMs = AdminConstant.DEFAULT_WAITING_TASK_EXECUTOR_PERIOD_MS;
        }
        if(KobUtils.isEmpty(this.taskOverstockRecoveryWeight)){
            this.taskOverstockRecoveryWeight = AdminConstant.DEFAULT_TASK_OVERSTOCK_RECOVERY_WEIGHT;
        }
        if(KobUtils.isEmpty(this.taskOverstockRecoveryThreshold)){
            this.taskOverstockRecoveryThreshold = AdminConstant.DEFAULT_TASK_OVERSTOCK_RECOVERY_THRESHOLD;
        }
        if(KobUtils.isEmpty(this.taskOverstockRecoveryRetainCount)){
            this.taskOverstockRecoveryRetainCount = AdminConstant.DEFAULT_TASK_OVERSTOCK_RECOVERY_RETAIN_COUNT;
        }
        if(KobUtils.isEmpty(this.appendPreviousTask)){
            this.appendPreviousTask = AdminConstant.DEFAULT_APPEND_PREVIOUS_TASK;
        }
        if(KobUtils.isEmpty(this.heartbeatInitialDelaySec)){
            this.heartbeatInitialDelaySec = AdminConstant.DEFAULT_CRON_HEARTBEAT_INITIAL_DELAY_SEC;
        }
        if(KobUtils.isEmpty(this.heartbeatPeriodSec)){
            this.heartbeatPeriodSec = AdminConstant.DEFAULT_CRON_HEARTBEAT_PERIOD_SEC;
        }
    }
}
