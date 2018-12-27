package com.ke.kob.client.spring.core;

import com.ke.kob.basic.model.ClientData;
import com.ke.kob.basic.support.NamedThreadFactory;
import com.ke.kob.client.spring.constant.ClientLogConstant;
import com.ke.kob.client.spring.startup.ClientProperties;
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

public @NoArgsConstructor @Slf4j class ClientProcessor {
    private static final ScheduledExecutorService CLIENT_HEARTBEAT = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("kob-client-heartbeat", true));
    private static final AtomicBoolean POWER = new AtomicBoolean(false);
    private static final AtomicBoolean CHECK_STEP = new AtomicBoolean(false);
    private static final AtomicBoolean WATCHER_STEP = new AtomicBoolean(false);
    private ClientContext clientContext;

    public ClientProcessor(ClientProperties prop, Map<String, Object> beans) {
        clientContext = new ClientContext(prop, beans);
    }

    public void init() {
        if (!clientContext.checkProperties()) {
            return;
        }
        clientContext.build();
        if (!ClientProcessor.POWER.compareAndSet(false, true)) {
            log.error(ClientLogConstant.error506());
            return;
        }
        heartbeat();
        log.info(ClientLogConstant.info101(clientContext.getClient().getProjectCode(),
                clientContext.getClient().getIp(),
                clientContext.getProp().getZkServers(),
                clientContext.getAdminUrl(),
                clientContext.getClient().getTasks()));
    }

    public void destroy() {
        POWER.set(false);
        clientContext.getZkClient().close();
    }

    private void heartbeat() {
        CLIENT_HEARTBEAT.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ClientProcessor.POWER.get()) {
                        heartbeat0();
                    }
                } catch (Exception e) {
                    log.error(ClientLogConstant.error503(), e);
                }
            }

            private void heartbeat0() {
                ZkClient zkClient = clientContext.getZkClient();
                if (zkClient == null) {
                    if (!clientContext.buildZkClient()) {
                        return;
                    }
                    zkClient = clientContext.getZkClient();
                }
                ClientData client = clientContext.getClient();
                String clientNodePath = clientContext.getClientNodePath();
                if (!CHECK_STEP.get()) {
                    String clientTaskPath = clientContext.getClientTaskPath();
                    if (!zkClient.exists(clientTaskPath) || !zkClient.exists(clientNodePath)) {
                        log.error(ClientLogConstant.error504(client.getProjectCode()));
                        return;
                    }
                    CHECK_STEP.set(true);
                }
                String pathClientInfoLocal = clientContext.getPathClientInfoLocal();
                int workers = clientContext.getPool().getActiveCount();
                client.setThreads(workers);
                if (zkClient.exists(pathClientInfoLocal)) {
                    zkClient.writeData(pathClientInfoLocal, client);
                } else {
                    zkClient.createEphemeral(pathClientInfoLocal, client);
                }
                if (CHECK_STEP.get() && !WATCHER_STEP.get()) {
                    zkClient.subscribeChildChanges(clientContext.getClientTaskPath(), new IZkChildListener() {
                        @Override
                        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                            clientContext.getDispatcher().dispatcher(parentPath, currentChilds);
                        }
                    });
                    WATCHER_STEP.set(true);
                }
                log.info(ClientLogConstant.info102(clientContext.getProp().getHeartbeatPeriod(), workers, clientContext.getProp().getThreads()));
            }
        }, clientContext.getProp().getInitialDelay(), clientContext.getProp().getHeartbeatPeriod(), TimeUnit.SECONDS);
    }

}
