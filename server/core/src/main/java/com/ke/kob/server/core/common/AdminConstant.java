package com.ke.kob.server.core.common;

/**
 * 服务端常量
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 下午12:00
 */
public class AdminConstant {
    public static final int DEFAULT_ZK_SESSION_TIMEOUT = 10000;
    public static final int DEFAULT_ZK_CONNECTION_TIMEOUT = 16000;

    public static final int DEFAULT_TASK_PUSH_THREADS = 72;
    public static final int DEFAULT_INTERVAL_MIN = 10;
    public static final int DEFAULT_WAITING_TASK_SCROLL = 100;
    public static final long DEFAULT_CRON_TASK_EXECUTOR_INITIAL_DELAY_SEC = 10;
    public static final long DEFAULT_CRON_TASK_EXECUTOR_PERIOD_SEC = 100;
    public static final long DEFAULT_WAITING_TASK_EXECUTOR_INITIAL_DELAY_MS = 6 * 1000;
    public static final long DEFAULT_WAITING_TASK_EXECUTOR_PERIOD_MS = 500;
    public static final int ONE_HUNDRED = 100;
    public static final int DEFAULT_TASK_OVERSTOCK_RECOVERY_WEIGHT = 20;
    public static final int DEFAULT_TASK_OVERSTOCK_RECOVERY_THRESHOLD = 220;
    public static final int DEFAULT_TASK_OVERSTOCK_RECOVERY_RETAIN_COUNT = 60;
    public static final boolean DEFAULT_APPEND_PREVIOUS_TASK = false;
    public static final long DEFAULT_CRON_HEARTBEAT_INITIAL_DELAY_SEC = 6;
    public static final long DEFAULT_CRON_HEARTBEAT_PERIOD_SEC = 60;
}
