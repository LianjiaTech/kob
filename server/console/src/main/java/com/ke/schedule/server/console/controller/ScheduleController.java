package com.ke.schedule.server.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.model.*;
import com.ke.schedule.server.core.common.Attribute;
import com.ke.schedule.server.core.common.CronExpression;
import com.ke.schedule.server.core.common.FtlPath;
import com.ke.schedule.server.core.model.db.JobCron;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.TaskWaiting;
import com.ke.schedule.server.core.model.db.User;
import com.ke.schedule.server.core.model.oz.BatchType;
import com.ke.schedule.server.core.model.oz.ResponseData;
import com.ke.schedule.server.core.model.oz.RetryType;
import com.ke.schedule.server.core.service.NodeService;
import com.ke.schedule.server.core.service.ScheduleService;
import com.ke.schedule.basic.constant.TaskContextKeyConstant;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/schedule")
@Controller
public @Slf4j class ScheduleController {

    @Resource
    private ScheduleService scheduleService;
    @Resource
    private NodeService nodeService;

    @RequestMapping(value = "/job_init.htm")
    public String jobInitPage(Model model) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        Map<String, String> taskMap = new HashMap<>(100);
        MultiValueMap<String, String> taskNodeMap = new LinkedMultiValueMap<>();
        if (projectUser != null) {
            Map<String, ClientPath> clientInfoMap = nodeService.getClientPaths(projectUser.getProjectCode());
            if (!KobUtils.isEmpty(clientInfoMap)) {
                Collection<ClientPath> clientInfoCollection = clientInfoMap.values();
                for (ClientPath clientPath : clientInfoCollection) {
                    Map<String, String> tasks = clientPath.getTasks();
                    if (!KobUtils.isEmpty(tasks)) {
                        taskMap.putAll(tasks);
                        for (String taskKey : tasks.keySet()) {
                            taskNodeMap.add(taskKey, clientPath.getIdentification());
                        }
                    }
                }
            }
        }
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.JOB_INIT_PATH);
        model.addAttribute("taskMap", taskMap);
        model.addAttribute("taskNodeMap", taskNodeMap);
        return FtlPath.INDEX_PATH;
    }

    @RequestMapping(value = "/job_add.json")
    @ResponseBody
    public ResponseData jobAdd() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Attribute.SESSION_USER);
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String paramJobType = request.getParameter("job_type");
        if (KobUtils.isEmpty(paramJobType)) {
            return ResponseData.error("请选择作业类型");
        }
        String paramTaskKey = request.getParameter("task_key");
        if (KobUtils.isEmpty(paramTaskKey)) {
            return ResponseData.error("请选择已挂载任务");
        }
        String paramJobCn = request.getParameter("job_cn");
        if (KobUtils.isEmpty(paramJobCn)) {
            return ResponseData.error("请输入作业名称");
        }
        String paramTaskRemark = request.getParameter("task_remark");
        if (KobUtils.isEmpty(paramTaskRemark)) {
            return ResponseData.error("请输入任务备注");
        }
        String paramDesignatedNode = request.getParameter("designated_node");
        String paramLoadBalance = request.getParameter("load_balance");
        if (TaskContextKeyConstant.RANDOM_NODE.equals(paramDesignatedNode)) {
            paramDesignatedNode = "";
            paramLoadBalance = "RANDOM";
        }
        String paramRetryType = request.getParameter("retry_type");
        RetryType retryType = RetryType.valueOf(paramRetryType);
        String paramRetryCount = request.getParameter("retry_count");
        Integer retryCount = 0;
        boolean paramFailover = false;
        if (retryType != RetryType.NONE) {
            retryCount = Integer.valueOf(paramRetryCount);
            paramFailover = "1".equals(request.getParameter("failover"));
        }
        Boolean paramRely = "1".equals(request.getParameter("rely"));
        Integer timeoutThreshold = Integer.valueOf(request.getParameter("timeout_threshold"));
        JobType jobType = JobType.valueOf(paramJobType);
        String paramUserParams = request.getParameter("user_params");
        if (!KobUtils.isEmpty(paramUserParams) && !KobUtils.isJson(paramUserParams)) {
            return ResponseData.error("请正确输入JSON格式的自定义参数");
        }
        switch (jobType) {
            case REAL_TIME: {
                return addRealTimeJob(user, projectUser, paramTaskKey, paramJobCn,
                        paramTaskRemark, paramDesignatedNode, timeoutThreshold,
                        paramUserParams);
            }
            case CRON: {
                String paramCronExpression = request.getParameter("cron_expression");
                return addCronJob(paramCronExpression, user, projectUser, paramTaskKey,
                        paramJobCn, paramTaskRemark, paramLoadBalance,
                        retryType, retryCount, paramFailover, paramRely,
                        timeoutThreshold, paramUserParams);
            }
            default: {
                return ResponseData.error("作业类型非法");
            }
        }
    }

    private ResponseData addRealTimeJob(User user, ProjectUser projectUser, String paramTaskKey, String paramJobCn,
                                        String paramTaskRemark, String paramDesignatedNode, Integer timeoutThreshold,
                                        String paramUserParams) {
        TaskWaiting taskWaiting = new TaskWaiting();
        taskWaiting.setProjectCode(projectUser.getProjectCode());
        taskWaiting.setProjectName(projectUser.getProjectName());
        taskWaiting.setJobUuid(UuidUtils.builder(UuidUtils.AbbrType.JC));
        taskWaiting.setJobType(JobType.REAL_TIME.name());
        taskWaiting.setJobCn(paramJobCn);
        taskWaiting.setTaskKey(paramTaskKey);
        taskWaiting.setTaskRemark(paramTaskRemark);
        taskWaiting.setTaskType(TaskType.NONE.name());
        taskWaiting.setBatchType(BatchType.NONE.name());
        String taskUuid = UuidUtils.builder(UuidUtils.AbbrType.TW);
        taskWaiting.setTaskUuid(taskUuid);
        taskWaiting.setRelationTaskUuid(taskUuid);
        taskWaiting.setRetryType(RetryType.NONE.name());
        taskWaiting.setRely(false);
        taskWaiting.setRetryCount(0);
        taskWaiting.setFailover(false);
        if (KobUtils.isEmpty(paramDesignatedNode)) {
            taskWaiting.setLoadBalance(LoadBalanceType.RANDOM.name());
        } else {
            taskWaiting.setLoadBalance(LoadBalanceType.NONE.name());
        }
        taskWaiting.setUserParams(paramUserParams);
        InnerParams innerParams = new InnerParams();
        innerParams.setDesignatedNode(paramDesignatedNode);
        innerParams.setCreateUserName(user.getName());
        taskWaiting.setInnerParams(JSONObject.toJSONString(innerParams));
        taskWaiting.setTimeoutThreshold(timeoutThreshold);
        taskWaiting.setTriggerTime(System.currentTimeMillis());
        taskWaiting.setCronExpression("");
        scheduleService.saveJobRealTime(taskWaiting);
        return ResponseData.success();
    }

    private ResponseData addCronJob(String paramCronExpression, User user, ProjectUser projectUser, String paramTaskKey,
                                    String paramJobCn, String paramTaskRemark, String paramLoadBalance,
                                    RetryType retryType, Integer retryCount, Boolean paramFailover, Boolean paramRely,
                                    Integer timeoutThreshold, String paramUserParams) {
        if (!CronExpression.isValidExpression(paramCronExpression)) {
            return ResponseData.error("CRON表达式输入有误");
        }
        JobCron jobCron = new JobCron();
        jobCron.setProjectCode(projectUser.getProjectCode());
        jobCron.setProjectName(projectUser.getProjectName());
        jobCron.setJobUuid(UuidUtils.builder(UuidUtils.AbbrType.JC));
        jobCron.setJobType(JobType.CRON.name());
        jobCron.setJobCn(paramJobCn);
        jobCron.setTaskKey(paramTaskKey);
        jobCron.setTaskRemark(paramTaskRemark);
        jobCron.setTaskType(TaskType.NONE.name());
        jobCron.setRetryType(retryType.name());
        jobCron.setBatchType(BatchType.NONE.name());
        jobCron.setRely(paramRely);
        jobCron.setRetryCount(retryCount);
        jobCron.setFailover(paramFailover);
        jobCron.setLoadBalance(paramLoadBalance);
        jobCron.setUserParams(paramUserParams);
        InnerParams innerParams = new InnerParams();
        innerParams.setCreateUserName(user.getName());
        jobCron.setInnerParams(JSONObject.toJSONString(innerParams));
        jobCron.setTimeoutThreshold(timeoutThreshold);
        jobCron.setSuspend(false);
        jobCron.setCronExpression(paramCronExpression);
        scheduleService.saveJobCron(jobCron);
        return ResponseData.success();
    }

    @RequestMapping(value = "/job_cron.htm")
    public String cronJobPage(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.JOB_CRON_PATH);
        return FtlPath.INDEX_PATH;
    }

    @RequestMapping(value = "/job_cron_list.json")
    @ResponseBody
    public ResponseData cronJobList() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        int count = scheduleService.selectCronJobCountByProjectCode(projectUser.getProjectCode());
        if (count == 0) {
            return ResponseData.success(0);
        }
        List<JobCron> jobCronList = scheduleService.selectJobCronPageByProject(projectUser.getProjectCode(), start, limit);
        return ResponseData.success(count, jobCronList);
    }

    @RequestMapping(value = "/job_suspend_opt.json")
    @ResponseBody
    public ResponseData jobSuspendOpt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String jobUuid = request.getParameter("job_uuid");
        Boolean suspend = Boolean.valueOf(request.getParameter("suspend"));
        if (suspend) {
            scheduleService.startJobCron(jobUuid, suspend, projectUser.getProjectCode());
        } else {
            scheduleService.suspendJobCron(jobUuid, suspend, projectUser.getProjectCode());
        }
        return ResponseData.success();
    }

    @RequestMapping(value = "/job_del_opt.json")
    @ResponseBody
    public ResponseData jobDelOpt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String jobUuid = request.getParameter("job_uuid");
        scheduleService.delJobCron(jobUuid, projectUser.getProjectCode());
        return ResponseData.success();
    }

    @RequestMapping(value = "/job_edit_opt.json")
    @ResponseBody
    public ResponseData taskDEditOpt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        JobCron editJobCron = new JobCron();
        editJobCron.setJobUuid(request.getParameter("job_uuid"));
        editJobCron.setProjectCode(projectUser.getProjectCode());
        editJobCron.setTaskRemark(request.getParameter("task_remark"));
        String paramCronExpression = request.getParameter("cron_expression");
        if (!CronExpression.isValidExpression(paramCronExpression)) {
            return ResponseData.error("CRON表达式输入有误");
        }
        String paramUserParams = request.getParameter("user_params");
        if (!KobUtils.isEmpty(paramUserParams) && !KobUtils.isJson(paramUserParams)) {
            return ResponseData.error("请正确输入JSON格式的自定义参数");
        }
        editJobCron.setCronExpression(paramCronExpression);
        scheduleService.editJobCron(editJobCron);
        return ResponseData.success();
    }

    @RequestMapping(value = "/task_waiting.htm")
    public String taskWaitingPage(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.TASK_WAITING_PATH);
        return FtlPath.INDEX_PATH;
    }

    @RequestMapping(value = "/task_waiting_list.json")
    @ResponseBody
    public ResponseData taskWaitingList() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        int count = scheduleService.selectTaskWaitingCountByProjectCode(projectUser.getProjectCode());
        if (count == 0) {
            return ResponseData.success(0);
        }
        List<TaskWaiting> taskWaitingList = scheduleService.selectTaskWaitingPageByProject(projectUser.getProjectCode(), start, limit);
        return ResponseData.success(count, taskWaitingList);
    }

    @RequestMapping(value = "/task_trigger_opt.json")
    @ResponseBody
    public ResponseData taskTriggerOpt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String taskUuid = request.getParameter("taskUuid");
        int count = scheduleService.triggerTaskWaiting(taskUuid, projectUser.getProjectCode());
        return ResponseData.success();
    }

    @RequestMapping(value = "/task_del_opt.json")
    @ResponseBody
    public ResponseData taskDelOpt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String taskUuid = request.getParameter("taskUuid");
        int count = scheduleService.delTaskWaiting(taskUuid, projectUser.getProjectCode());
        return ResponseData.success();
    }

}
