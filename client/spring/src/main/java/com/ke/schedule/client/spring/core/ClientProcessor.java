package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.startup.ClientProperties;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午3:57
 */

public @NoArgsConstructor
@Slf4j
class ClientProcessor {
    private ClientContext clientContext;

    public ClientProcessor(ClientProperties prop, Map<String, Object> beans) {
        clientContext = new ClientContext.Builder()
                .zk(prop.getZkConnectString(), prop.getZkConnectionTimeout(), prop.getZkSessionTimeout(), prop.getZkAuthInfo())
                .runner(beans)
                .pool(prop.getThreads())
                .build();
    }

    public void init() {
        clientContext.getZkClient().subscribeChildChanges(clientContext.getClientTaskPath(), (parentPath, currentChilds) -> TaskDispatcher.INSTANCE.dispatcher(clientContext, parentPath, currentChilds));
        if (!clientContext.checkProperties()) {
            return;
        }
        log.info(ClientLogConstant.info101(clientContext.getClient().getProjectCode(),
                clientContext.getClient().getIp(),
                clientContext.getProp().getZkConnectString(),
                clientContext.getAdminUrl(),
                clientContext.getClient().getTasks()));
    }

    public void destroy() {
        clientContext.getZkClient().close();
    }
}
