package com.ke.kob.basic.model;

/**
 * 作业类型
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午2:46
 */
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

