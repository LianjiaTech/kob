package com.ke.kob.server.core.model.oz;

/**
 * 重试类型
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/21 下午12:03
 */

public enum RetryType {
    /**
     * 不重试
     */
    NONE,
    /**
     * 失败重试
     */
    FAIL,
}
