package com.ke.schedule.basic.model;

public enum TaskType {
    /**
     * 正常推送的任务
     */
    NONE,
    /**
     * 失败重试
     */
    RETRY_FAIL,
}

