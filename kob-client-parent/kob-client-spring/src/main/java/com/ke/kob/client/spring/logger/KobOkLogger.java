package com.ke.kob.client.spring.logger;

import com.alibaba.fastjson.JSONObject;
import com.ke.kob.basic.model.LogContext;
import com.ke.kob.basic.model.LogLevel;
import com.ke.kob.basic.model.LogMode;
import com.ke.kob.basic.model.TaskResult;
import com.ke.kob.basic.support.KobUtils;
import com.ke.kob.basic.support.UuidUtils;
import com.ke.kob.client.spring.constant.ClientLogConstant;
import com.ke.kob.client.spring.core.ClientContext;
import com.ke.kob.client.spring.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/2 下午3:43
 */

public @Slf4j class KobOkLogger implements KobLogger {

    private String adminUrl;
    private String systemLogPath;
    private String serviceLogPath;
    private OkHttpClient okHttpClient;
    private String projectCode;
    private String taskUuid;
    private String clientIdentification;
    private String cluster;

    public KobOkLogger(ClientContext clientContext, TaskContext taskContext) {
        this.adminUrl = clientContext.getProp().getAdminUrl();
        this.systemLogPath = clientContext.getProp().getSystemLogPath();
        this.serviceLogPath = clientContext.getProp().getServiceLogPath();
        this.okHttpClient = clientContext.getOkHttpClient();
        this.projectCode = taskContext.getProjectCode();
        this.taskUuid = taskContext.getTaskUuid();
        this.clientIdentification = clientContext.getClient().getIdentification();
        this.cluster = clientContext.getProp().getCluster();
    }

    @Override
    public void debug(String msg) {
        serviceLog(msg, LogLevel.DEBUG);
    }

    @Override
    public void info(String msg) {
        serviceLog(msg, LogLevel.INFO);
    }

    @Override
    public void warn(String msg) {
        serviceLog(msg, LogLevel.WARN);
    }

    @Override
    public void error(String msg) {
        serviceLog(msg, LogLevel.ERROR);
    }

    private void serviceLog(String msg, LogLevel lv) {
        log(null, LogMode.SERVICE, msg, null, lv);
    }

    public void systemLog(Integer taskRecordState) {
        log(taskRecordState, LogMode.SYSTEM, null, null, null);
    }

    public void systemLog(TaskResult result) {
        log(result.getState(), LogMode.SYSTEM, result.getMsg(), null, null);
    }

    public void systemLog(Integer taskRecordState, Exception e) {
        log(taskRecordState, LogMode.SYSTEM, null, e, null);
    }

    private void log(Integer taskRecordState, LogMode lm, String msg, Exception e, LogLevel lv) {
        if (KobUtils.isEmpty(adminUrl)) {
            log.error(ClientLogConstant.error509(taskRecordState, lv, "adminUrl为空"), e);
            return;
        }
        if (okHttpClient == null) {
            log.error(ClientLogConstant.error509(taskRecordState, lv, "okHttpClient为空"), e);
            return;
        }
        try {
            log0(taskRecordState, lm, msg, e, lv);
        } catch (Exception ex) {
            log.error(ClientLogConstant.error509(taskRecordState, lv, "方法log异常"), ex);
        }
    }

    private void log0(Integer taskRecordState, LogMode lm, String msg, Exception e, LogLevel lv) throws Exception {
        LogContext log = new LogContext();
        log.setProjectCode(projectCode);
        log.setCluster(cluster);
        log.setTaskUuid(taskUuid);
        log.setClientIdentification(clientIdentification);
        log.setLogUuid(UuidUtils.builder(UuidUtils.AbbrType.LU));
        log.setLogTime(System.currentTimeMillis());
        log.setTaskRecordState(taskRecordState);
        log.setLogMode(lm.name());
        log.setMsg(msg);
        String url = lm == LogMode.SYSTEM ? adminUrl + systemLogPath : adminUrl + serviceLogPath;
        if (lv != null) {
            log.setLogLevel(lv.name());
        }
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.setMsg(sw.getBuffer().toString());
            sw.close();
            pw.close();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(buildRequestBody(log))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response != null) {
            response.close();
        }
    }

    private RequestBody buildRequestBody(LogContext log) {
        MediaType media = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(media, JSONObject.toJSONString(log));
    }
}

