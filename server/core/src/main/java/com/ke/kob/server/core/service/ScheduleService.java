package com.ke.kob.server.core.service;

import com.ke.kob.server.core.model.db.JobCron;
import com.ke.kob.server.core.model.db.TaskRecord;
import com.ke.kob.server.core.model.db.TaskWaiting;
import com.ke.kob.basic.model.LogContext;
import com.ke.kob.basic.model.TaskBaseContext;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午2:50
 */

public interface ScheduleService {
    /**
     * 返回一定条数
     *
     * @param triggerTime 触发时间
     * @param limit       限制条数
     * @param cluster     集群
     * @return 等待推送的任务列表
     */
    List<TaskWaiting> findTriggerTaskInLimit(long triggerTime, int limit, String cluster);

    /**
     * 全量查找未暂停的cron类型的作业
     *
     * @param cluster 项目名称
     * @return JobCronList 未暂停的cron类型的作业
     */
    List<JobCron> findRunningCronJob(String cluster);

    /**
     * 解析规定时间间隔内的corn作业
     * <p>
     * 解析cron表达式 解析触发时间是在规定时间内任务
     * 通过事务方法保证一致性
     * task表中jobUuid 和 trigger是联合唯一索引
     *
     * @param serverIdentification serverNode
     * @param jobCron              cron作业
     * @param appendPreviousTask   是否追加过去任务
     * @param intervalMin          规定时间间隔 单位 min
     * @param cluster              集群名称
     * @param now                  当前时间
     */
    void createCronWaitingTaskForTime(String serverIdentification, JobCron jobCron, boolean appendPreviousTask, Integer intervalMin, String cluster, Date now);

    /**
     * 推送任务 添加zk任务事件，更新record状态，很难做到极端情况数据一致
     *
     * @param zkClient 101ZkClient
     * @param tw       等待推送任务
     * @param cluster  集群
     */
    void pushTask(ZkClient zkClient, TaskWaiting tw, String cluster);

    /**
     * 锁定作业
     *
     * @param tw                   等待推送任务
     * @param cluster              集群
     * @param serverIdentification 节点标识
     * @return 是否次作业上周期任务未完成
     */
    Boolean lockPushTask(TaskWaiting tw, String cluster, String serverIdentification);

    /**
     * 回收积压任务
     *
     * @param zkClient      101ZkClient
     * @param overstockTask 待回收积压任务
     * @param cluster       集群名称
     */
    void fireOverstockTask(ZkClient zkClient, List<TaskBaseContext> overstockTask, String cluster) throws UnsupportedEncodingException;

    /**
     * 查询过期任务数量
     *
     * @param now     当前时间
     * @param cluster 集群
     * @return 返回查询总量
     */
    int selectCountExpireTaskRecord(long now, String cluster);

    /**
     * 分页查询 查询正在运行中的超时任务
     *
     * @param now     当前时间
     * @param start   起始位置
     * @param limit   偏移量
     * @param cluster 集群
     * @return 返回查询列表
     */
    List<TaskRecord> selectListExpireTaskRecord(long now, int start, int limit, String cluster);

    /**
     * 处理过期任务
     * 运行task状况，如果注册发现找不到就且超时则任务触发超时时间
     *
     * @param zkClient   zk客户端
     * @param taskExpire 过期任务
     * @param cluster    集群
     */
    void handleExpireTask(ZkClient zkClient, TaskRecord taskExpire, String cluster);

    /**
     * 处理任务日志
     *
     * @param context    上报日志
     * @param taskRecord 日志对应的任务记录
     */
    void handleTaskLog(LogContext context, TaskRecord taskRecord);

    /**
     * 查询当前项目下所有的cron类型作业
     *
     * @param projectCode 项目标识
     * @return 当前项目下的cron类型任务列表
     */
    int selectCronJobCountByProjectCode(String projectCode);

    /**
     * 分页查询jobCron信息
     *
     * @param projectCode 项目标识
     * @param start       起始位置
     * @param limit       偏移量
     * @return 当前项目下的cron类型任务列表
     */
    List<JobCron> selectJobCronPageByProject(String projectCode, Integer start, Integer limit);

    /**
     * 查询当前项目下待推送任务
     *
     * @param projectCode 项目标识
     * @return 查询总量
     */
    int selectTaskWaitingCountByProjectCode(String projectCode);

    /**
     * 查询当前项目下待推送任务
     *
     * @param projectCode 项目标识
     * @param start       起始位置
     * @param limit       偏移量
     * @return 查询列表
     */
    List<TaskWaiting> selectTaskWaitingPageByProject(String projectCode, Integer start, Integer limit);

    /**
     * 保存实时作业
     *
     * @param taskWaiting 等待推送任务
     */
    void saveJobRealTime(TaskWaiting taskWaiting);

    /**
     * 将暂停中的cron作业置为启动
     *
     * @param jobUuid     作业标识
     * @param suspend     开关
     * @param projectCode 项目标识
     * @return 影响行数
     */
    int startJobCron(String jobUuid, Boolean suspend, String projectCode);

    /**
     * 将运行中的cron作业置为暂停
     *
     * @param jobUuid     作业标识
     * @param suspend     开关
     * @param projectCode 项目标识
     */
    void suspendJobCron(String jobUuid, Boolean suspend, String projectCode);

    /**
     * 删除cron作业
     *
     * @param jobUuid     作业标识
     * @param projectCode 项目标识
     */
    void delJobCron(String jobUuid, String projectCode);

    /**
     * 触发等待执行的任务
     *
     * @param taskUuid    任务标识
     * @param projectCode 项目标识
     * @return 影响行数
     */
    int triggerTaskWaiting(String taskUuid, String projectCode);

    /**
     * 删除等待推送任务
     *
     * @param taskUuid    作业唯一标识
     * @param projectCode 项目标识
     * @return 影响行数
     */
    int delTaskWaiting(String taskUuid, String projectCode);

    /**
     * 保存cron作业
     *
     * @param jobCron cron作业
     * @return 影响行数
     */
    int saveJobCron(JobCron jobCron);

    /**
     * 编辑cron作业
     *
     * @param editJobCron JobCron实体类
     */
    void editJobCron(JobCron editJobCron);

    /**
     * 查询所有项目标识
     *
     * @return 查询所有 project_mode = service 项目标识
     */
    Set<String> selectServiceProjectCodeSet();
}
