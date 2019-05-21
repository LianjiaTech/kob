package com.ke.schedule.server.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.server.core.mapper.LogCollectMapper;
import com.ke.schedule.server.core.mapper.TaskRecordMapper;
import com.ke.schedule.server.core.model.db.LogCollect;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.service.CollectService;
import com.ke.schedule.server.core.service.ScheduleService;
import com.ke.schedule.basic.model.LogContext;
import com.ke.schedule.basic.model.LogMode;
import com.ke.schedule.basic.support.KobUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/17 下午12:11
 */
@Service
public @Slf4j class CollectServiceImpl implements CollectService {

    @Resource
    private TaskRecordMapper taskRecordMapper;
    @Resource
    private LogCollectMapper logCollectMapper;
    @Resource(name = "scheduleService")
    private ScheduleService scheduleService;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    @Override
    public void handleLogger(LogContext context) {
        String taskUuid = context.getTaskUuid();
        TaskRecord taskRecord = taskRecordMapper.findByTaskUuid(taskUuid, mp);
        System.out.println("tr =" + JSONObject.toJSONString(taskRecord));
        if (taskRecord == null) {
            log.error("哪来的日志 " + JSONObject.toJSONString(context));
            return;
        }
        LogCollect logCollect = new LogCollect();
        logCollect.setState(context.getState());
        logCollect.setLogUuid(context.getLogUuid());
        logCollect.setProjectCode(context.getProjectCode());
        logCollect.setTaskUuid(context.getTaskUuid());
        logCollect.setIp(context.getIp());
        logCollect.setClientIdentification(context.getClientIdentification());
        logCollect.setLogTime(new Date(context.getLogTime()));

        logCollectMapper.insertOne(logCollect, mp);
        if (context.getState()!=null && TaskRecordStateConstant.SERVICE_LOG!=context.getState()) {
            scheduleService.handleTaskLog(context, taskRecord);
        }
    }
}
