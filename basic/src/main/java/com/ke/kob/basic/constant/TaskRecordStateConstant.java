package com.ke.kob.basic.constant;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:28
 */

public class TaskRecordStateConstant {

    /**
     * 等待推送
     */
    public static final int WAITING_PUSH = 10;
    /**
     * 依赖上周期任务上周期没完成
     */
    public static final int RELY_UNDO = 11;
    /**
     * 推送成功
     */
    public static final int PUSH_SUCCESS = 30;
    /**
     * 推送失败
     */
    public static final int PUSH_FAIL = 31;
    /**
     * 作业积压过多，服务端回收任务
     */
    public static final int STACKED_RECYCLING = 32;

    /**
     * 客户端接收任务
     */
    public static final int RECEIVE_SUCCESS = 50;
    /**
     * 客户端开始执行
     */
    public static final int RUNNER_START = 51;
    /**
     * 客户端任务超时回收（比如触发时间与客户端当前时间超过某一个阈值，就不执行并回收此任务）
     */
    public static final int EXPIRE_RECYCLING = 52;
    /**
     * 执行成功
     */
    public static final int EXECUTE_SUCCESS = 70;
    /**
     * 执行失败
     */
    public static final int EXECUTE_FAIL = 71;
    /**
     * 执行异常
     */
    public static final int EXECUTE_EXCEPTION = 72;
    /**
     * 执行过期
     */
    public static final int EXECUTE_EXPIRE = 73;
}
