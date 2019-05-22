package com.ke.schedule.server.core.service;

import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.server.core.model.db.JobCron;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.model.db.TaskWaiting;
import com.ke.schedule.basic.model.LogContext;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午2:50
 */

public interface ScheduleService {

    List<TaskWaiting> findTriggerTaskInLimit(long triggerTime, int limit, String mp);

    List<JobCron> findRunningCronJob(String mp);

    void createCronWaitingTaskForTime(String serverIdentification, JobCron jobCron, boolean appendPreviousTask, Integer intervalMin, Date now);

    void pushTask0(TaskWaiting tw, String cluster);

    Boolean lockPushTask(TaskWaiting tw, String cluster, String serverIdentification);

    void fireOverstockTask(List<TaskContext.Path> overstockTask);

    int selectCountExpireTaskRecord(long now, String cluster);

    List<TaskRecord> selectListExpireTaskRecord(int start, int limit, String cluster);

    void handleExpireTask(TaskRecord taskExpire, String cluster);

    void handleTaskLog(LogContext context, TaskRecord taskRecord);

    int selectCronJobCountByProjectCode(String projectCode);

    List<JobCron> selectJobCronPageByProject(String projectCode, Integer start, Integer limit);

    int selectTaskWaitingCountByProjectCode(String projectCode);

    List<TaskWaiting> selectTaskWaitingPageByProject(String projectCode, Integer start, Integer limit);

    void saveJobRealTime(TaskWaiting taskWaiting);

    int startJobCron(String jobUuid, Boolean suspend, String projectCode);

    void suspendJobCron(String jobUuid, Boolean suspend, String projectCode);

    void delJobCron(String jobUuid, String projectCode);

    int triggerTaskWaiting(String taskUuid, String projectCode);

    int delTaskWaiting(String taskUuid, String projectCode);

    int saveJobCron(JobCron jobCron);

    void editJobCron(JobCron editJobCron);

    Set<String> selectServiceProjectCodeSet();

    boolean pushTask(TaskWaiting tw, String identification);
}
