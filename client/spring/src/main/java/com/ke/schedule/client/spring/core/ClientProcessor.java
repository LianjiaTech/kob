package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.startup.ClientProperties;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午3:57
 */

public @NoArgsConstructor
@Slf4j
class ClientProcessor {
    private static final ScheduledExecutorService CLIENT_HEARTBEAT = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("kob-client-heartbeat", true));
    private ClientContext clientContext;

    public ClientProcessor(ClientProperties prop, Map<String, Object> beans) {
        clientContext = new ClientContext.Builder()
                .zk(prop.getZkConnectString(), prop.getZkConnectionTimeout(), prop.getZkSessionTimeout(), prop.getZkAuthInfo())
                .runner(beans)
                .pool(prop.getThreads())
                .path(prop.getZkPrefix(), prop.getProjectCode())
                .admin(prop.getAdminUrl())
                .client(prop.getProjectCode(), prop.getThreads())
                .build();
    }

    public void init() {
        try {
            heartbeat();
            clientContext.getZkClient().subscribeChildChanges(
                    clientContext.getClientTaskPath(),
                    (parentPath, currentChilds)
                            -> TaskDispatcher.INSTANCE.dispatcher(clientContext, parentPath, currentChilds));
//            if (!clientContext.checkProperties()) {todo properties check
//                return;
//            }
            log.info(ClientLogConstant.info101(clientContext.getData().getProjectCode(),
                    clientContext.getData().getIp(),
                    clientContext.getZkConnect(),
                    clientContext.getAdminUrl(),
                    clientContext.getData().getTasks()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        clientContext.getZkClient().close();
    }

    private void heartbeat() {
        CLIENT_HEARTBEAT.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    heartbeat0();
                } catch (Exception e) {
                    log.error(ClientLogConstant.error503(), e);
                }
            }

            private void heartbeat0() throws UnsupportedEncodingException {
                ZkClient zkClient = clientContext.getZkClient();
                String path = clientContext.getClientNodePath()+ ZkPathConstant.BACKSLASH + clientContext.getData().getClientPath();
                if (zkClient.exists(path)) {
                    zkClient.writeData(path, clientContext.getData());
                } else {
                    zkClient.create(path, clientContext.getData(), CreateMode.EPHEMERAL);
                }
                log.info(ClientLogConstant.info102(60L, clientContext.getData().getWorkers(), 1));
            }
        }, 15, 60, TimeUnit.SECONDS);
    }
}
