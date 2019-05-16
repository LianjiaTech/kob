package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.*;
import com.ke.schedule.basic.support.IpUtils;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.basic.support.UuidUtils;
import com.ke.schedule.server.core.common.AdminConstant;
import com.ke.schedule.server.core.common.AdminLogConstant;
import com.ke.schedule.server.core.common.NodeHashLoadBalance;
import com.ke.schedule.server.core.mapper.TaskRecordMapper;
import com.ke.schedule.server.core.mapper.TaskWaitingMapper;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.model.db.TaskWaiting;
import com.ke.schedule.server.core.model.oz.NodeServer;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j class Heartbeat {

    @Resource
    private CuratorFramework curator;

    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    private static final ScheduledExecutorService SERVER_HEARTBEAT = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("server-heartbeat", true));

    void initialize() {
        SERVER_HEARTBEAT.scheduleAtFixedRate(this::heartbeat, 10, 60, TimeUnit.SECONDS);
    }

    private void heartbeat() {
        try {
            if(curator.checkExists().forPath(ZkPathConstant.serverNodePath(zp))==null){
                NodeServer local = new NodeServer(zp, IpUtils.getLocalAddress(), UuidUtils.builder(UuidUtils.AbbrType.SN), System.currentTimeMillis());
                curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZkPathConstant.serverNodePath(zp)+ZkPathConstant.BACKSLASH+"id:xxx", JSONObject.toJSONString(local).getBytes());
            }
        } catch (Exception e) {
            log.error("心跳 error", e);
        }
    }
}
