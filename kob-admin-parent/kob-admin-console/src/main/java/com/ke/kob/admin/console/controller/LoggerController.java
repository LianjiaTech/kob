package com.ke.kob.admin.console.controller;

import com.ke.kob.admin.core.common.Attribute;
import com.ke.kob.admin.core.common.FtlPath;
import com.ke.kob.admin.core.model.db.LogCollect;
import com.ke.kob.admin.core.model.db.LogOpt;
import com.ke.kob.admin.core.model.db.ProjectUser;
import com.ke.kob.admin.core.model.db.TaskRecord;
import com.ke.kob.admin.core.model.oz.ResponseData;
import com.ke.kob.admin.core.service.LoggerService;
import com.ke.kob.basic.support.KobUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/24 下午9:14
 */
@RequestMapping("/logger")
@Controller
public @Slf4j class LoggerController {

    @Resource(name = "loggerService")
    private LoggerService loggerService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 任务记录view
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/task_record.htm")
    public String taskWaiting(Model model) {
        Date now = new Date();
        model.addAttribute("trigger_time_start", sdf.format(KobUtils.addHour(now, -24)));
        model.addAttribute("trigger_time_end", sdf.format(now));
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String jobUuid = request.getParameter("job_uuid");
        model.addAttribute("job_uuid", KobUtils.isEmpty(jobUuid) ? "" : jobUuid);
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.TASK_RECORD_PATH);
        return FtlPath.INDEX_PATH;
    }

    /**
     * 任务日志List
     *
     * @return
     */
    @RequestMapping(value = "/task_record_list.json")
    @ResponseBody
    public ResponseData taskWaitingList() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        Map<String, Object> param = new HashMap<>(10);
        try {
            String triggerTimeStart = request.getParameter("trigger_time_start");
            if (!KobUtils.isEmpty(triggerTimeStart)) {
                param.put("triggerTimeStart", sdf.parse(triggerTimeStart).getTime());
            }
            String triggerTimeEnd = request.getParameter("trigger_time_end");
            if (!KobUtils.isEmpty(triggerTimeEnd)) {
                param.put("triggerTimeEnd", sdf.parse(triggerTimeEnd).getTime());
            }
        } catch (ParseException e) {
            log.error("sdf_error", e);
            return ResponseData.error("时间输入有误");
        }
        String jobUuid = request.getParameter("job_uuid");
        if (!KobUtils.isEmpty(jobUuid)) {
            param.put("jobUuid", jobUuid);
        }
        param.put("projectCode", projectUser.getProjectCode());
        int count = loggerService.selectTaskRecordCountByParam(param);
        if (count == 0) {
            return ResponseData.success(0);
        }
        List<TaskRecord> taskWaitingList = loggerService.selectTaskRecordPageByParam(param, start, limit);
        return ResponseData.success(count, taskWaitingList);
    }

    /**
     * 任务记录view
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/log_collect.htm")
    public String logCollect(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String taskUuid = request.getParameter("task_uuid");
        model.addAttribute("task_uuid", KobUtils.isEmpty(taskUuid) ? "" : taskUuid);
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.LOG_COLLECT_PATH);
        return FtlPath.INDEX_PATH;
    }

    /**
     * 任务记录List
     *
     * @return
     */
    @RequestMapping(value = "/log_collect_list.json")
    @ResponseBody
    public ResponseData logCollectList() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        String paramTaskUuid = request.getParameter("task_uuid");
        String taskUuid = null;
        if (!KobUtils.isEmpty(paramTaskUuid)) {
            taskUuid = paramTaskUuid;
        }
        int count = loggerService.selectLogCollectCountByProjectCodeAndTaskUuid(projectUser.getProjectCode(), taskUuid);
        if (count == 0) {
            return ResponseData.success(0);
        }
        List<LogCollect> logCollectList = loggerService.selectLogCollectPageByProjectAndTaskUuid(projectUser.getProjectCode(), taskUuid, start, limit);
        return ResponseData.success(count, logCollectList);
    }

    /**
     * 操作记录view
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/log_opt.htm")
    public String optLog(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.LOG_OPT_PATH);
        return FtlPath.INDEX_PATH;
    }

    /**
     * 操作记录List
     *
     * @return
     */
    @RequestMapping(value = "/log_opt_list.json")
    @ResponseBody
    public ResponseData logOptList() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        Integer costTime = KobUtils.isEmpty(request.getParameter("cost_time")) ?
                null : Integer.valueOf(request.getParameter("cost_time"));
        int count = loggerService.selectLogOptCountByCostTime(costTime);
        if (count == 0) {
            return ResponseData.success(0);
        }
        List<LogOpt> logCollectList = loggerService.selectLogOptPageByProject(costTime, start, limit);
        return ResponseData.success(count, logCollectList);
    }
}
