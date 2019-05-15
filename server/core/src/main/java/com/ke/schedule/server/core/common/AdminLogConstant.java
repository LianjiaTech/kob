package com.ke.schedule.server.core.common;


import java.text.MessageFormat;

/**
 * 服务端日志信息
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/10 下午4:05
 */
public class AdminLogConstant {

    public static String error9100() {
        String log = "[日志 code:]9000 cron类型作业生成未来指定时间间隔内的待执行任务异常";
        return log;
    }

    public static String error9101(String s) {
        String log = "[日志 code:]9101 cron类型作业生成未来指定时间间隔内的待执行任务异常,jobCron作业信息:{0}";
        return MessageFormat.format(log, s);
    }

    public static String error9102(String s) {
        String log = "[日志 code:]9102 锁定待推送任务,taskWaiting任务信息:{0}";
        return MessageFormat.format(log, s);
    }

    public static String error9103(String s) {
        String log = "[日志 code:]9103 推送任务失败,taskWaiting任务信息:{0}";
        return MessageFormat.format(log, s);
    }

    public static String error9104() {
        String log = "[日志 code:]9104 推送等待执行的任务失败";
        return log;
    }
}
