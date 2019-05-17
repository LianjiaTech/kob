package com.ke.schedule.client.spring.constant;

import com.ke.schedule.basic.model.LogLevel;
import com.ke.schedule.basic.support.KobUtils;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 客户端日志信息
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午7:39
 */

public class ClientLogConstant {
    public static String error500(String error) {
        String log = "\n" +
                "+ ——————————————————————————————————————————————————————————————————————————————————————\n" +
                "| KOB 作业平台 \n" +
                "| 客户端版本:" + ClientConstant.VERSION + " \n" +
                "| [日志 code:500] 启动失败, 原因:{0} \n" +
                "+ ——————————————————————————————————————————————————————————————————————————————————————\n";
        return MessageFormat.format(log, error);
    }

    public static String error501(String taskContext) {
        String log = "[日志 code:]501 此任务标识为空,任何节点都有权利销毁此任务,任务信息:{1}";
        return MessageFormat.format(log, taskContext);
    }

    public static String error502(String taskContext, String clientIdentification) {
        String log = "[日志 code:]502 日志系统执行异常, 任务信息:{0}, 当前节点:{1}";
        return MessageFormat.format(log, taskContext, clientIdentification);
    }

    public static String error503() {
        return "[日志 code:]503 kob心跳异常";
    }

    public static String error504(String projectCode) {
        return "[日志 code:]504 作业调度项目所需zk path不存在,应该是还没在服务端创建该项目, 项目标识:" + projectCode;
    }

    public static String error505(String clz) {
        String log = "[日志 code:]505 作业调度类:{0},没有找到有@Task注解的作业方法";
        return MessageFormat.format(log, clz);
    }

    public static String error506() {
        return "[日志 code:]506 KOB处理器可能已经存在";
    }

    public static String error507(String zkServers, Integer timeout) {
        String log = "[日志 code:]507 未能在{0}ms内,完成zk客户端的创建,可能是zk:{1}服务端未存在或是网络问题," +
                "或将kob.client.zk_connection_timeout参数值设大些,当前值{0}";
        return MessageFormat.format(log, timeout, zkServers);
    }

    public static String error508() {
        return "[日志 code:]508 完成zk客户端的创建异常";
    }

    public static String error509(Integer taskRecordState, LogLevel lv, String errMsg) {
        String log = "[日志 code:]509 上报日志异常:{0},状态:{1}" + (lv == null ? "" : ",日志级别:{2}");
        return MessageFormat.format(log, errMsg, taskRecordState, lv);
    }

    public static String error510() {
        return "[日志 code:]510 休眠异常";
    }

    public static String warn400(String clientIdentification, String taskContext) {
        String log = "[日志 code:]400 指定节点非本节点,不消费本作业,指定消费节点:{0},任务信息:{1}";
        return MessageFormat.format(log, clientIdentification, taskContext);
    }

    public static String warn401(String clientIdentification, String taskContext, int sec) {
        String log = "[日志 code:]401 此任务尝试排除作业节点信息中包含本节点,不消费本此任务,尝试排除节点:{0},任务信息:{1},休眠{2}秒在去处理此任务事件";
        return MessageFormat.format(log, clientIdentification, taskContext, sec);
    }

    public static String warn402(String clientIdentification, String taskContext) {
        String log = "[日志 code:]402 本客户端不支持执行此任务,节点:{0},任务信息:{1}";
        return MessageFormat.format(log, clientIdentification, taskContext);
    }

    public static String warn403(String clientIdentification, String taskContext, String date, int expireTime) {
        String log = "[日志 code:]403 客户端检测到过期任务并回收, 节点:{0},任务信息:{1},当前时间:{2}, 过期阈值:{3}秒";
        return MessageFormat.format(log, clientIdentification, taskContext, date, expireTime);
    }

    public static String warn404(double loadFactor, String clientIdentification, int active, int max) {
        String log = "[日志 code:]404 作业系统线程池超过阈值{0}, 节点:{1}, 当前工作线程:{2}, 线程池大小:{3}";
        return MessageFormat.format(log, loadFactor, clientIdentification, active, max);
    }

    public static String warn405(String taskContext, String clientIdentification) {
        String log = "[日志 code:]405 任务可能被其他节点消费, 任务信息:{0}, 当前节点:{1}";
        return MessageFormat.format(log, taskContext, clientIdentification);
    }

    public static String warn406(String clz) {
        return "[日志 code:]406 检测到了异常类:" + clz;
    }

    public static String warn407(String clientIdentification, String recommendNode, int sec) {
        String log = "[日志 code:]407 此作业推荐节点:{0}, 并非此节点:{1}, 休眠{1}秒在去处理此任务事件";
        return MessageFormat.format(log, recommendNode, clientIdentification, sec);
    }

    public static String info100(String taskContext, String clientIdentification) {
        String log = "[日志 code:]100 拿到作业,作业信息, 任务信息:{0}, 当前节点:{1}";
        return MessageFormat.format(log, taskContext, clientIdentification);
    }

    public static String info101(String projectCode, String ip, String zkServers, String adminUrl, Map<String, String> tasks) {
        String slogan = "\n" +
                "+ ——————————————————————————————————————————————————————————————————————————————\n" +
                "| 欢迎使用作业平台 KOB \n" +
                "| 请宣传并点赞:https://github.com/lianjiatech/kob \n" +
                "+ ——————————————————————————————————————————————————————————————————————————————\n" +
                "| 客户端版本:" + ClientConstant.VERSION + "\n" +
                "| 项目标识:{0} \n" +
                "| 注册中心地址:{2} \n" +
                "| 客户端IP:{1} \n" +
                "| 服务端地址:{3} \n" +
                "| 挂载任务量:{4} \n" +
                "{5}" +
                "+ ——————————————————————————————————————————————————————————————————————————————\n";
        int taskCount = 0;
        StringBuffer sb = new StringBuffer();
        if (!KobUtils.isEmpty(tasks)) {
            taskCount = tasks.size();
            sb.append("| 挂载任务详情: \n");
            for (String key : tasks.keySet()) {
                sb.append("|      任务标识:" + key + " - 任务备注:" + tasks.get(key) + " \n");
            }
        }
        return MessageFormat.format(slogan, projectCode, ip, zkServers, adminUrl, taskCount, sb.toString());
    }

    public static String info102(Long period, Integer workers, Integer threads) {
        String log = "[日志 code:]102 KOB心跳健康检查Q{0}S,当前工作线程数:{1},线程池最大工作线程数:{2}";
        return MessageFormat.format(log, period, workers, threads);
    }

    public static String info103(String jobCn, String taskKey, Integer state) {
        String log = "[日志 code:]103 作业名称:{0},任务key:{1},执行完成,状态码:{2}";
        return MessageFormat.format(log, jobCn, taskKey, state);
    }
}
