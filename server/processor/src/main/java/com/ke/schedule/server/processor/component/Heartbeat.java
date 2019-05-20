package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j
class Heartbeat {

    @Resource
    private CuratorFramework curator;
    @Resource
    private ServerContext context;

    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    private static final ScheduledExecutorService SERVER_HEARTBEAT = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("server-heartbeat", true));

    void initialize() {
        SERVER_HEARTBEAT.scheduleAtFixedRate(this::heartbeat, 10, 60, TimeUnit.SECONDS);
    }

    private void heartbeat() {
        String path = ZkPathConstant.serverNodePath(zp) + ZkPathConstant.BACKSLASH + context.getNode().getIdentification();
        try {
            if (curator.checkExists().forPath(path) == null) {
                curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, JSONObject.toJSONString(context.getNode()).getBytes());
            }
        } catch (Exception e) {
            log.error("心跳 error", e);
        }
    }
}
