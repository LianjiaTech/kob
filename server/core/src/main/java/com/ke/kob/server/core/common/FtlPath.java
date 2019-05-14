package com.ke.kob.server.core.common;

/**
 * free maker 模板地址
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 上午11:38
 */
public interface FtlPath {
    /**
     * 首页
     */
    String INDEX_PATH = "/index.ftl";
    /**
     * 登录页
     */
    String LOGIN_PATH = "/login.ftl";
    /**
     * 项目管理 - 项目接入
     */
    String INDEX_WELCOME_PATH = "./welcome.ftl";
    /**
     * 项目管理 - 项目接入
     */
    String PROJECT_ACCESS_PATH = "./manager/project_access.ftl";
    /**
     * 项目管理 - 人员管理
     */
    String PROJECT_USER_PATH = "./manager/project_user.ftl";
    /**
     * 节点信息-客户端节点
     */
    String CLIENT_NODE = "./node/client_node.ftl";
    /**
     * 作业管理-任务添加
     */
    String JOB_INIT_PATH = "./schedule/job_init.ftl";
    /**
     * 作业管理-cron作业
     */
    String JOB_CRON_PATH = "./schedule/job_cron.ftl";
    /**
     * 作业管理-等待推送
     */
    String TASK_WAITING_PATH = "./schedule/task_waiting.ftl";
    /**
     * 日志-任务记录
     */
    String TASK_RECORD_PATH = "./logger/task_record.ftl";
    /**
     * 日志-上报信息
     */
    String LOG_COLLECT_PATH = "./logger/log_collect.ftl";
    /**
     * 日志-操作日志
     */
    String LOG_OPT_PATH = "./logger/log_opt.ftl";
}
