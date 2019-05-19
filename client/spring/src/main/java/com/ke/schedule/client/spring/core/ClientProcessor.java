package com.ke.schedule.client.spring.core;

import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.startup.ClientProperties;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

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
    //    private static final AtomicBoolean POWER = new AtomicBoolean(false);
//    private static final AtomicBoolean CHECK_STEP = new AtomicBoolean(false);
//    private static final AtomicBoolean WATCHER_STEP = new AtomicBoolean(false);
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

            private void heartbeat0() {
                ZkClient zkClient = clientContext.getZkClient();
                String path = clientContext.getClientNodePath()+ ZkPathConstant.BACKSLASH + clientContext.getData().getIdentification();
                if (zkClient.exists(path)) {
                    zkClient.writeData(path, clientContext.getData());
                } else {
                    zkClient.create(path, clientContext.getData(), CreateMode.EPHEMERAL);
                }
                log.info(ClientLogConstant.info102(60L, clientContext.getData().getWorkers(), 1));
            }
        }, 15, 60, TimeUnit.SECONDS);
    }

//    private void heartbeat() {
//        CLIENT_HEARTBEAT.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (ClientProcessor.POWER.get()) {
//                        heartbeat0();
//                    }
//                } catch (Exception e) {
//                    log.error(ClientLogConstant.error503(), e);
//                }
//            }
//
//            private void heartbeat0() {
//                ZkClient zkClient = clientContext.getZkClient();
//                if (zkClient == null) {
//                    if (!clientContext.buildZkClient()) {
//                        return;
//                    }
//                    zkClient = clientContext.getZkClient();
//                }
//                ClientData client = clientContext.getData();
//                String clientNodePath = clientContext.getClientNodePath();
//                if (!CHECK_STEP.get()) {
//                    String clientTaskPath = clientContext.getClientTaskPath();
//                    if (!zkClient.exists(clientTaskPath) || !zkClient.exists(clientNodePath)) {
//                        log.error(ClientLogConstant.error504(client.getProjectCode()));
//                        return;
//                    }
//                    CHECK_STEP.set(true);
//                }
//                String pathClientInfoLocal = clientContext.getPathClientInfoLocal();
//                int workers = clientContext.getPool().getActiveCount();
//                client.setThreads(workers);
//                if (zkClient.exists(pathClientInfoLocal)) {
//                    zkClient.writeData(pathClientInfoLocal, client);
//                } else {
//                    zkClient.createEphemeral(pathClientInfoLocal, client);
//                }
//                if (CHECK_STEP.get() && !WATCHER_STEP.get()) {
//                    zkClient.subscribeChildChanges(clientContext.getClientTaskPath(), new IZkChildListener() {
//                        @Override
//                        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
//                            TaskDispatcher.INSTANCE.dispatcher(clientContext, parentPath, currentChilds);
//                        }
//                    });
//                    WATCHER_STEP.set(true);
//                }
//                log.info(ClientLogConstant.info102(clientContext.getProp().getHeartbeatPeriod(), workers, clientContext.getProp().getThreads()));
//            }
//        }, clientContext.getProp().getInitialDelay(), clientContext.getProp().getHeartbeatPeriod(), TimeUnit.SECONDS);
//    }

}
