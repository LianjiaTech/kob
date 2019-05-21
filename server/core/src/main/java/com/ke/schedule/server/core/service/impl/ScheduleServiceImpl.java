package com.ke.schedule.server.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.*;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.UuidUtils;
import com.ke.schedule.server.core.common.CronExpression;
import com.ke.schedule.server.core.common.NodeHashLoadBalance;
import com.ke.schedule.server.core.mapper.JobCronMapper;
import com.ke.schedule.server.core.mapper.ProjectUserMapper;
import com.ke.schedule.server.core.mapper.TaskRecordMapper;
import com.ke.schedule.server.core.mapper.TaskWaitingMapper;
import com.ke.schedule.server.core.model.db.JobCron;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.model.db.TaskWaiting;
import com.ke.schedule.server.core.model.oz.BatchType;
import com.ke.schedule.server.core.model.oz.RetryType;
import com.ke.schedule.server.core.service.AlarmService;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午2:51
 */
@Service(value = "scheduleService")
public @Slf4j
class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private JobCronMapper jobCronMapper;
    @Resource
    private TaskWaitingMapper taskWaitingMapper;
    @Resource
    private TaskRecordMapper taskRecordMapper;
    @Resource
    private ProjectUserMapper projectUserMapper;
    @Resource
    private CuratorFramework curator;
    @Resource
    private AlarmService alarmService;
    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    /**
     * 生成通用等待推送任务
     *
     * @param jobCron              cron类型作业
     * @param serverIdentification server节点标识
     * @return 等待推送任务
     */
    private static TaskWaiting createCommonCronTaskWaiting(JobCron jobCron, String serverIdentification) {
        String taskUuid = UuidUtils.builder(UuidUtils.AbbrType.TW);
        TaskWaiting taskWaiting = new TaskWaiting();
        taskWaiting.setProjectCode(jobCron.getProjectCode());
        taskWaiting.setProjectName(jobCron.getProjectName());
        taskWaiting.setJobUuid(jobCron.getJobUuid());
        taskWaiting.setJobType(JobType.CRON.name());
        taskWaiting.setJobCn(jobCron.getJobCn());
        taskWaiting.setTaskKey(jobCron.getTaskKey());
        taskWaiting.setTaskRemark(jobCron.getTaskRemark());
        taskWaiting.setTaskType(TaskType.NONE.name());
        taskWaiting.setLoadBalance(jobCron.getLoadBalance());
        taskWaiting.setTaskUuid(taskUuid);
        taskWaiting.setRelationTaskUuid(taskUuid);
        taskWaiting.setRetryType(jobCron.getRetryType());
        taskWaiting.setRely(jobCron.getRely());
        taskWaiting.setUserParams(jobCron.getUserParams());
        InnerParams innerParams = new InnerParams();
        innerParams.setCronTaskGenerateNode(serverIdentification);
        taskWaiting.setInnerParams(JSONObject.toJSONString(innerParams));
        taskWaiting.setCronExpression(jobCron.getCronExpression());
        taskWaiting.setTimeoutThreshold(jobCron.getTimeoutThreshold());
        taskWaiting.setRetryCount(jobCron.getRetryCount());
        taskWaiting.setFailover(jobCron.getFailover());
        taskWaiting.setBatchType(jobCron.getBatchType());
        return taskWaiting;
    }

    @Override
    public List<TaskWaiting> findTriggerTaskInLimit(long triggerTime, int limit, String mp) {
        return taskWaitingMapper.findTriggerTaskInLimit(triggerTime, limit, mp);
    }

    @Override
    public List<JobCron> findRunningCronJob(String mp) {
        return jobCronMapper.findCronJobBySuspend(false, mp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCronWaitingTaskForTime(String serverIdentification, JobCron jobCron, boolean appendPreviousTask, Integer intervalMin, Date now) {
        String cronExpression = jobCron.getCronExpression();
        CronExpression cron;
        Long lastGenerateTriggerTime = jobCron.getLastGenerateTriggerTime();
        boolean timeAfterSetNow = lastGenerateTriggerTime == null || (!appendPreviousTask && now.getTime() > lastGenerateTriggerTime);
        Date timeAfter = timeAfterSetNow ? now : new Date(lastGenerateTriggerTime);
        try {
            cron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            log.error("cronExpression parse error cronExpression:" + cronExpression, e);
            return;
        }
        Date end = KobUtils.addMin(now, intervalMin);
        List<TaskWaiting> cronTaskWaitingList = new ArrayList<>();
        while (true) {
            Date nextTriggerTime = cron.getTimeAfter(timeAfter);
            if (nextTriggerTime == null || !nextTriggerTime.before(end)) {
                break;
            } else {
                TaskWaiting tw = createCommonCronTaskWaiting(jobCron, serverIdentification);
                tw.setTriggerTime(nextTriggerTime.getTime());
                cronTaskWaitingList.add(tw);
            }
            timeAfter = nextTriggerTime;
        }
        if (!CollectionUtils.isEmpty(cronTaskWaitingList)) {
            int updateCount = jobCronMapper.updateRunningJobCronLastGenerateTriggerTime(jobCron.getJobUuid(),
                    jobCron.getCronExpression(), lastGenerateTriggerTime, timeAfter.getTime(), jobCron.getVersion(), mp);
            if (updateCount != 1) {
                log.error("job_cron data has change uuid:" + jobCron.getJobUuid());
                throw new RuntimeException("job_cron data has change uuid:" + jobCron.getJobUuid());
            }
            int insertCount = taskWaitingMapper.insertBatch(cronTaskWaitingList, mp);
            System.out.println("====" + insertCount + "======");
        }
    }

    @Override
    public void pushTask0(TaskWaiting tw, String cluster) {
        TaskContext context = new TaskContext();
        context.getData().setProjectCode(tw.getProjectCode());
        context.getData().setJobUuid(tw.getJobUuid());
        context.getData().setJobCn(tw.getJobCn());
        context.getPath().setTaskUuid(tw.getTaskUuid());
        context.getPath().setTaskKey(tw.getTaskKey());
        context.getPath().setTriggerTime(tw.getTriggerTime());
        context.getPath().setDesignatedNode(tw.getInnerParamsBean().getDesignatedNode());
        context.getPath().setRecommendNode(tw.getInnerParamsBean().getRecommendNode());
        context.getPath().setTryToExclusionNode(tw.getInnerParamsBean().getTryToExclusionNode());
        context.getData().setUserParam(JSONObject.parseObject(tw.getUserParams()));
        String projectTaskPath = ZkPathConstant.clientTaskPath(cluster, context.getData().getProjectCode());
        int state = TaskRecordStateConstant.PUSH_SUCCESS;
        Map<String, Object> param = new HashMap<>(10);
        try {
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(projectTaskPath + ZkPathConstant.BACKSLASH + JSONObject.toJSONString(context));
        } catch (Exception e) {
            log.error("pushTask_error 推送zk事件异常", e);
            state = TaskRecordStateConstant.PUSH_FAIL;
            param.put("complete", true);
        }
        param.put("state", state);
        taskRecordMapper.updateByTaskUuid(param, tw.getTaskUuid(), cluster);
    }

    @Override
    public Boolean lockPushTask(TaskWaiting tw, String cluster, String serverIdentification) {
        return null;
    }

    @Override
    public void fireOverstockTask(List<TaskContext.Path> overstockTask) {
        overstockTask.forEach(t -> {
            try {
                curator.delete().forPath(t.getPath());
                taskRecordMapper.updateStateByTaskUuid(TaskRecordStateConstant.STACKED_RECYCLING, t.getTaskUuid(), zp);
            } catch (Exception e) {
                log.error("er", e);
            }
        });
    }

    @Override
    public int selectCountExpireTaskRecord(long now, String cluster) {
        return taskRecordMapper.selectCountExpireTaskRecord(now, cluster);
    }

    @Override
    public List<TaskRecord> selectListExpireTaskRecord(long now, int start, int limit, String cluster) {
        return taskRecordMapper.selectListExpireTaskRecord(System.currentTimeMillis(), start, limit, cluster);
    }

    @Override
    public void handleExpireTask(TaskRecord taskExpire, String cluster) {
        //todo  后期需要判断zk 任务是否在运行
        taskRecordMapper.updateStateByTaskUuid(TaskRecordStateConstant.EXECUTE_EXPIRE, taskExpire.getTaskUuid(), cluster);
    }

    @Override
    public void handleTaskLog(LogContext context, TaskRecord taskRecord) {
        Map<String, Object> param = new HashMap<>(20);
        if (TaskRecordStateConstant.RECEIVE_SUCCESS == context.getState()) {
            param.put("clientIdentification", context.getClientIdentification());
            param.put("consumptionTime", new Date(context.getLogTime()));
            param.put("state", context.getState());
        }
        if (TaskRecordStateConstant.RUNNER_START == context.getState()) {
            alarmService.run(taskRecord);
            param.put("executeStartTime", new Date(context.getLogTime()));
            param.put("state", context.getState());
        }
        if (TaskRecordStateConstant.EXECUTE_SUCCESS == context.getState()
                || TaskRecordStateConstant.EXECUTE_FAIL == context.getState()
                || TaskRecordStateConstant.EXECUTE_EXCEPTION == context.getState()) {
            alarmService.end(taskRecord);
            param.put("complete", true);
            param.put("executeEndTime", new Date(context.getLogTime()));
            param.put("state", context.getState());
        }
        Boolean needAppendRetry = false;
        String appendRetryTaskUuid = null;
        if (TaskRecordStateConstant.EXECUTE_FAIL == context.getState()) {
            if (taskRecord.getAncestor() && RetryType.FAIL.name().equals(taskRecord.getRetryType())) {
                appendRetryTaskUuid = UuidUtils.builder(UuidUtils.AbbrType.AR);
                InnerParams innerParams = taskRecord.getInnerParamsBean();
                innerParams.setAppendRetryTaskUuid(appendRetryTaskUuid);
                param.put("innerParams", JSONObject.toJSONString(innerParams));
                needAppendRetry = true;
            }
        }
        if (!KobUtils.isEmpty(context.getMsg())) {
            param.put("msg", context.getMsg());
        }
        if (!param.isEmpty()) {
            taskRecordMapper.updateByTaskUuid(param, context.getTaskUuid(), mp);
        }
        if (needAppendRetry) {
            handleRetryFailTask(context, taskRecord, appendRetryTaskUuid);
        }
    }

    /**
     * 处理失败重试任务 这里代码一把唆了 后期需要方法共用
     *
     * @param logContext          日志内容
     * @param taskRecord          任务记录
     * @param appendRetryTaskUuid 追加重试任务的task_uuid
     */
    private void handleRetryFailTask(LogContext logContext, TaskRecord taskRecord, String appendRetryTaskUuid) {
        TaskRecord retryTask = createRetryTaskRecord(logContext, taskRecord, appendRetryTaskUuid);
        taskRecordMapper.insertOne(retryTask, mp);
        TaskContext context = new TaskContext();
        context.getData().setProjectCode(retryTask.getProjectCode());
        context.getData().setJobUuid(retryTask.getJobUuid());
        context.getData().setJobCn(retryTask.getJobCn());
        context.getPath().setTaskUuid(retryTask.getTaskUuid());
        context.getPath().setTaskKey(retryTask.getTaskKey());
        context.getPath().setTriggerTime(retryTask.getTriggerTime());
        context.getPath().setDesignatedNode(retryTask.getInnerParamsBean().getDesignatedNode());
        context.getPath().setRecommendNode(retryTask.getInnerParamsBean().getRecommendNode());
        context.getPath().setTryToExclusionNode(retryTask.getInnerParamsBean().getTryToExclusionNode());
        context.getData().setUserParam(JSONObject.parseObject(retryTask.getUserParams()));
        String projectTaskPath = ZkPathConstant.clientTaskPath(mp, context.getData().getProjectCode());
        int state = TaskRecordStateConstant.PUSH_SUCCESS;
        Map<String, Object> param = new HashMap<>(10);
        try {
            curator.create().withMode(CreateMode.PERSISTENT).forPath(projectTaskPath + ZkPathConstant.BACKSLASH + JSONObject.toJSONString(context));
        } catch (Exception e) {
            log.error("pushTask_error 推送zk事件异常", e);
            state = TaskRecordStateConstant.PUSH_FAIL;
            param.put("complete", true);
        }
        param.put("state", state);
        taskRecordMapper.updateByTaskUuid(param, appendRetryTaskUuid, mp);
    }

    /**
     * 生成 追加重试任务记录
     *
     * @param context             日志内容
     * @param failTaskRecord      失败任务记录
     * @param appendRetryTaskUuid 最佳任务记录的task_uuid
     * @return 追加重试任务记录
     */
    private TaskRecord createRetryTaskRecord(LogContext context, TaskRecord failTaskRecord, String appendRetryTaskUuid) {
        TaskRecord tr = new TaskRecord();
        tr.setProjectCode(failTaskRecord.getProjectCode());
        tr.setProjectName(failTaskRecord.getProjectName());
        tr.setJobUuid(failTaskRecord.getJobUuid());
        tr.setJobType(failTaskRecord.getJobType());
        tr.setJobCn(failTaskRecord.getJobCn());
        tr.setTaskKey(failTaskRecord.getTaskKey());
        tr.setTaskRemark(failTaskRecord.getTaskRemark());
        tr.setTaskType(TaskType.RETRY_FAIL.name());
        tr.setTaskUuid(appendRetryTaskUuid);
        tr.setRelationTaskUuid(failTaskRecord.getTaskUuid());
        tr.setLoadBalance(LoadBalanceType.RANDOM.name());
        tr.setRetryType(RetryType.NONE.name());
        tr.setBatchType(BatchType.NONE.name());
        tr.setRely(false);
        tr.setAncestor(false);
        tr.setUserParams(failTaskRecord.getUserParams());
        InnerParams innerParams = new InnerParams();
        innerParams.setTryToExclusionNode(context.getClientIdentification());
        //todo  我觉得不需要 pushNode
        tr.setTimeoutThreshold(failTaskRecord.getTimeoutThreshold());
        tr.setState(TaskRecordStateConstant.WAITING_PUSH);
        tr.setComplete(false);
        tr.setInnerParams(JSONObject.toJSONString(innerParams));
        tr.setRetryCount(0);
        tr.setTriggerTime(System.currentTimeMillis());
        return tr;
    }

    @Override
    public int selectCronJobCountByProjectCode(String projectCode) {
        return jobCronMapper.selectCountByProjectCode(projectCode, mp);
    }

    @Override
    public List<JobCron> selectJobCronPageByProject(String projectCode, Integer start, Integer limit) {
        return jobCronMapper.selectPageJobCronByProject(projectCode, start, limit, mp);
    }

    @Override
    public int selectTaskWaitingCountByProjectCode(String projectCode) {
        return taskWaitingMapper.selectCountByProjectCode(projectCode, mp);
    }

    @Override
    public List<TaskWaiting> selectTaskWaitingPageByProject(String projectCode, Integer start, Integer limit) {
        return taskWaitingMapper.selectPageByProjectCode(projectCode, start, limit, mp);
    }

    @Override
    public void saveJobRealTime(TaskWaiting taskWaiting) {
        taskWaitingMapper.insertOne(taskWaiting, mp);
    }

    @Override
    public int startJobCron(String jobUuid, Boolean suspend, String projectCode) {
        return jobCronMapper.updateSuspend(!suspend, jobUuid, projectCode, suspend, mp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendJobCron(String jobUuid, Boolean suspend, String projectCode) {
        jobCronMapper.updateSuspend(!suspend, jobUuid, projectCode, suspend, mp);
        taskWaitingMapper.deleteByJobUuidAndProjectCode(jobUuid, projectCode, mp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delJobCron(String jobUuid, String projectCode) {
        jobCronMapper.deleteByJobUuidAndProjectCode(jobUuid, projectCode, mp);
        taskWaitingMapper.deleteByJobUuidAndProjectCode(jobUuid, projectCode, mp);
    }

    @Override
    public int triggerTaskWaiting(String taskUuid, String projectCode) {
        return taskWaitingMapper.triggerTaskWaiting(System.currentTimeMillis(), taskUuid, projectCode, mp);
    }

    @Override
    public int delTaskWaiting(String taskUuid, String projectCode) {
        return taskWaitingMapper.deleteByTaskUuidAndProjectCode(taskUuid, projectCode, mp);
    }

    @Override
    public int saveJobCron(JobCron jobCron) {
        return jobCronMapper.insertOne(jobCron, mp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editJobCron(JobCron editJobCron) {
        jobCronMapper.updateOne(editJobCron.getTaskRemark(),
                editJobCron.getCronExpression(),
                editJobCron.getUserParams(),
                editJobCron.getJobUuid(),
                editJobCron.getProjectCode(),
                mp);
        taskWaitingMapper.deleteByJobUuidAndProjectCode(editJobCron.getJobUuid(), editJobCron.getProjectCode(), mp);
    }

    @Override
    public Set<String> selectServiceProjectCodeSet() {
        List<ProjectUser> projectList = projectUserMapper.selectProjectIsOwner(mp);
        Set<String> serviceProjectCodeSet = new HashSet<>();
        if (!KobUtils.isEmpty(projectList)) {
            for (ProjectUser projectUser : projectList) {
                if ("service".equals(projectUser.getProjectMode())) {
                    serviceProjectCodeSet.add(projectUser.getProjectCode());
                }
            }
        }
        return serviceProjectCodeSet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushTask(TaskWaiting tw, String identification) {
        int deleteCount = taskWaitingMapper.deleteOne(tw.getTaskUuid(), mp);
        if (deleteCount != 1) {
            log.error("server_admin_code_error_100:删除等待任务数量不为1");
            return false;
        }

        TaskRecord taskRecord = createCommonTaskRecord(tw, identification);
        if (tw.getRely()) {
            TaskRecord lastTask = taskRecordMapper.selectLastUndoTaskByJobUuid(tw.getJobUuid(), mp);
            if (lastTask == null && TaskRecordStateConstant.isComplete(lastTask.getState())) {
                taskRecord.setState(TaskRecordStateConstant.RELY_UNDO);
                taskRecord.setComplete(true);
                taskRecordMapper.insertOne(taskRecord, mp);
                return false;
            }
        }

        int insertCount = taskRecordMapper.insertOne(taskRecord, mp);
        if (insertCount != 1) {
            log.error("server_admin_code_error_101:插入任务记录数量不为1");
            throw new RuntimeException("server_code_error_101:插入任务记录数量不为1");
        }

        TaskContext context = new TaskContext();
        context.getData().setProjectCode(tw.getProjectCode());
        context.getData().setJobUuid(tw.getJobUuid());
        context.getData().setJobCn(tw.getJobCn());
        context.getPath().setTaskUuid(tw.getTaskUuid());
        context.getPath().setTaskKey(tw.getTaskKey());
        context.getPath().setTriggerTime(tw.getTriggerTime());
        context.getPath().setDesignatedNode(tw.getInnerParamsBean().getDesignatedNode());
        context.getPath().setRecommendNode(tw.getInnerParamsBean().getRecommendNode());
        context.getPath().setTryToExclusionNode(tw.getInnerParamsBean().getTryToExclusionNode());
        context.getData().setUserParam(JSONObject.parseObject(tw.getUserParams()));
        String projectTaskPath = ZkPathConstant.clientTaskPath(zp, context.getData().getProjectCode());
        int state = TaskRecordStateConstant.PUSH_SUCCESS;
        Map<String, Object> param = new HashMap<>(10);
        try {
            alarmService.send(taskRecord);
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(projectTaskPath + ZkPathConstant.BACKSLASH + context.getZkPath(), JSONObject.toJSONString(context.getData()).getBytes());
        } catch (Exception e) {
            log.error("pushTask_error 推送zk事件异常", e);
            state = TaskRecordStateConstant.PUSH_FAIL;
            param.put("complete", true);
        }
        param.put("state", state);
        taskRecordMapper.updateByTaskUuid(param, tw.getTaskUuid(), mp);
        return true;
    }

    private TaskRecord createCommonTaskRecord(TaskWaiting tw, String serverIdentification) {
        TaskRecord tr = new TaskRecord();
        tr.setProjectCode(tw.getProjectCode());
        tr.setProjectName(tw.getProjectName());
        tr.setJobUuid(tw.getJobUuid());
        tr.setJobType(tw.getJobType());
        tr.setJobCn(tw.getJobCn());
        tr.setTaskKey(tw.getTaskKey());
        tr.setTaskRemark(tw.getTaskRemark());
        tr.setTaskType(TaskType.NONE.name());
        tr.setTaskUuid(tw.getTaskUuid());
        tr.setRelationTaskUuid(tw.getTaskUuid());
        tr.setLoadBalance(tw.getLoadBalance());
        tr.setRetryType(tw.getRetryType());
        tr.setBatchType(tw.getBatchType());
        tr.setRely(tw.getRely());
        tr.setAncestor(true);
        tr.setUserParams(tw.getUserParams());
        tr.setClientIdentification("");
        InnerParams innerParams = KobUtils.isEmpty(tw.getInnerParams()) ? new InnerParams() : JSONObject.parseObject(tw.getInnerParams(), InnerParams.class);
        innerParams.setTaskPushNode(serverIdentification);
        if (LoadBalanceType.NODE_HASH.name().equals(tw.getLoadBalance())) {
            List<String> clientNodePathList = null;
            try {
                clientNodePathList = curator.getChildren().forPath(ZkPathConstant.clientNodePath(zp, tw.getProjectCode()));
            } catch (Exception e) {
                //todo
                log.error("e", e);
            }
            List<String> nodeList = new ArrayList<>();
            if (!KobUtils.isEmpty(clientNodePathList)) {
                for (String child : clientNodePathList) {
//                  todo what mean  ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
                    nodeList.add(child);
                }
            }
            innerParams.setRecommendNode(NodeHashLoadBalance.doSelect(nodeList, tw.getJobUuid()));
        }

        tr.setCronExpression(tw.getCronExpression());
        tr.setTimeoutThreshold(tw.getTimeoutThreshold());
        tr.setState(TaskRecordStateConstant.WAITING_PUSH);
        tr.setInnerParams(JSONObject.toJSONString(innerParams));
        tr.setRetryCount(tw.getRetryCount());
        tr.setTriggerTime(tw.getTriggerTime());
        return tr;
    }

}
