package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.LockData;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.server.core.common.AdminLogConstant;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j
class ExpireTask {

    @Resource(name = "scheduleService")
    private ScheduleService scheduleService;
    @Resource
    private ServerContext context;
    @Resource
    private CuratorFramework curator;
    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;
    private static final ScheduledExecutorService WAITING_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("waiting-task", true));

    void initialize() {
        WAITING_TASK_EXECUTOR.scheduleAtFixedRate(() -> pushWaitingTask(), 2000, 1000, TimeUnit.MILLISECONDS);
    }

    private void pushWaitingTask() {
        boolean create = false;
        String path = ZkPathConstant.serverExpirePath(zp);
        try {
            System.out.println("WAITING_TASK_EXECUTOR");
            try {
                byte[] b = curator.getData().forPath(path);
                LockData exitLock = JSONObject.parseObject(new String(b), LockData.class);
                if (exitLock.getExpire() < System.currentTimeMillis()) {
                    curator.delete().forPath(path);
                } else {
                    return;
                }
            } catch (KeeperException.NoNodeException e) {

            }
            LockData lock = new LockData(context.getNode().getIdentification(), System.currentTimeMillis() + 1000 * 20);
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, JSONObject.toJSONString(lock).getBytes());
            create = true;
            fireExpireTask();

        } catch (Exception e) {
            log.error(AdminLogConstant.error9100(), e);
        }
        if (create) {
            try {
                curator.delete().forPath(path);
            } catch (Exception e) {
                log.error(AdminLogConstant.error9100(), e);
            }
        }
    }

    private void fireExpireTask() {
        try {
            long now = System.currentTimeMillis();
            int expireCount = scheduleService.selectCountExpireTaskRecord(now, zp);
            if (expireCount > 0) {
                int start = expireCount / 100 * 100;
                int limit = expireCount - start;
                do {
                    List<TaskRecord> taskExpireList = scheduleService.selectListExpireTaskRecord(now, start, limit, mp);
                    if (CollectionUtils.isEmpty(taskExpireList)) {
                        start = start - 100;
                        limit = 100;
                        continue;
                    }
                    for (TaskRecord taskExpire : taskExpireList) {
                        scheduleService.handleExpireTask(taskExpire, mp);
                    }
                    start = start - 100;
                    limit = 100;
                } while (start >= 0);
            }
        } catch (
                Exception e) {
            log.error("server_admin_code_error_102:过期数据计算异常", e);
        }
    }
}
