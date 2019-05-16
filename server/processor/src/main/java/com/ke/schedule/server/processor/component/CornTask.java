package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.LockData;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.server.core.common.AdminLogConstant;
import com.ke.schedule.server.core.model.db.JobCron;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j class CornTask {

    private static final ScheduledExecutorService CRON_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("cron-task", true));

    @Resource
    private CuratorFramework curator;
    @Resource(name = "scheduleService")
    private ScheduleService scheduleService;

    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    void initialize() {
        CRON_TASK_EXECUTOR.scheduleAtFixedRate(this::initializeCornTask, 10, 20, TimeUnit.SECONDS);
    }

    private void initializeCornTask(){
        boolean create = false;
        try {
            System.out.println("CRON_TASK_EXECUTOR");
            if(curator.checkExists().forPath(ZkPathConstant.serverCronPath(zp)) !=null){
                byte[] b = curator.getData().forPath(ZkPathConstant.serverCronPath(zp));
                b = b;
            }
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZkPathConstant.serverCronPath(zp), JSONObject.toJSONString(new LockData("ip:xxx", System.currentTimeMillis()+1000*60*5)).getBytes());//todo
            create = true;
            jobCronGenerateWaitingTask();
        } catch (Exception e) {
            log.error(AdminLogConstant.error9100(), e);
        }
        if(create){
            try {
                curator.delete().forPath(ZkPathConstant.serverCronPath(zp));
            } catch (Exception e) {
                log.error(AdminLogConstant.error9100(), e);
            }
        }
    }

    private void jobCronGenerateWaitingTask() {
        List<JobCron> jobCronList = scheduleService.findRunningCronJob(mp);
        if (!KobUtils.isEmpty(jobCronList)) {
            Date now = new Date();
            jobCronList.forEach(c -> {
                try {
                    scheduleService.createCronWaitingTaskForTime("serverid", c, false, 10, now);//todo
                } catch (Exception e) {
                    log.error(AdminLogConstant.error9101(JSONObject.toJSONString(c)), e);
                }
            });
        }
    }

}
