package com.ke.schedule.server.processor.component;

import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j class ExpireTask {

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
        WAITING_TASK_EXECUTOR.scheduleAtFixedRate(() -> fireExpireTask(), 2000, 1000, TimeUnit.MILLISECONDS);
    }

    private void fireExpireTask() {
        LockConsumer.INSTANCE.lock(fireExpireTask0(), curator, context.getNode().getIdentification(), ZkPathConstant.serverExpirePath(zp)).accept(null);
    }

    private Consumer<Object> fireExpireTask0() {
        return o -> {
            try {
                long now = System.currentTimeMillis();
                int expireCount = scheduleService.selectCountExpireTaskRecord(now, mp);
                if (expireCount > 0) {
                    int start = expireCount / 100 * 100;
                    int limit = expireCount - start;
                    do {
                        List<TaskRecord> taskRunnerList = scheduleService.selectListExpireTaskRecord(start, limit, mp);
                        List<TaskRecord> taskExpireList = new ArrayList<>();
                        if (CollectionUtils.isEmpty(taskRunnerList)) {
                            taskRunnerList.forEach(e -> {
                                if ((e.getTriggerTime() + e.getTimeoutThreshold() * 1000L) > System.currentTimeMillis()) {
                                    taskExpireList.add(e);
                                }
                            });
                        }
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
        };
    }
}
