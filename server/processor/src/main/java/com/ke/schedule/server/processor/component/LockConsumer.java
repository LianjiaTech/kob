package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.model.LockData;
import com.ke.schedule.server.core.common.AdminLogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.function.Consumer;

/**
 * @author zhaoyuguang
 */

public @Slf4j enum LockConsumer {
    //1
    INSTANCE;

    public Consumer<Object> lock(Consumer consumer, CuratorFramework curator, String identification, String path) {
        return o -> {
            boolean create = false;
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
                LockData lock = new LockData(identification, System.currentTimeMillis() + 1000 * 20);
                curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, JSONObject.toJSONString(lock).getBytes());
                create = true;
                consumer.accept(o);

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
        };
    }
}