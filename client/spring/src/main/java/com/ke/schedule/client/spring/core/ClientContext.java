package com.ke.schedule.client.spring.core;

import com.alibaba.fastjson.JSON;
import com.ke.schedule.basic.model.*;
import com.ke.schedule.client.spring.constant.ClientConstant;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.startup.ClientProperties;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.IpUtils;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.basic.support.UuidUtils;
import com.ke.schedule.client.spring.annotation.Task;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午9:42
 */

public @Slf4j @NoArgsConstructor class ClientContext {
    private @Getter
    ClientProperties prop;
    private Map<String, Object> beans;
    private @Getter @Setter Long expireRecyclingTime = 20L * 60L * 1000L;
    private @Getter
    ClientData client;
    private @Getter ZkClient zkClient;
    private @Getter String clientTaskPath;
    private @Getter String clientNodePath;
    private @Getter String pathClientInfoLocal;
    private @Getter Map<String, Pair<String, Function<TaskBaseContext, TaskResult>>> runner;
    private @Getter ThreadPoolExecutor pool;
    private @Getter
    TaskDispatcher2 dispatcher = new TaskDispatcher2(this);
    private @Getter String adminUrl;
    private @Getter @Setter String processorPath;
    private String taskLogPath;
    private String serviceLogPath;
    private Integer clientWorks;
    private Long initialDelay;


    public ClientContext(ClientProperties prop, Map<String, Object> beans) {
        this.prop = prop;
        this.beans = beans;
        this.client = new ClientData();
        this.runner = new ConcurrentHashMap<>();
    }

    static class Builder {
        private ClientContext context;

        public Builder() {
            context = new ClientContext();
            context.runner = new ConcurrentHashMap<>();
        }

        public ClientContext build() {
            return context;
        }

        Builder zk(String zkServers, Integer sessionTimeout, Integer connectionTimeout, List<ZkAuthInfo> auths) {
            try {
                context.zkClient = new ZkClient(zkServers, sessionTimeout, connectionTimeout, new ZkSerializer() {
                    @Override
                    public byte[] serialize(Object data) throws ZkMarshallingError {
                        if (data instanceof String) {
                            return ((String) data).getBytes();
                        }
                        return JSON.toJSONString(data).getBytes();
                    }

                    @Override
                    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                        return new String(bytes);
                    }
                });
                if (!CollectionUtils.isEmpty(auths)) {
                    auths.forEach(e -> context.zkClient.addAuthInfo(e.getScheme(), e.getAuth().getBytes()));
                }
            } catch (ZkTimeoutException e) {
                log.error(ClientLogConstant.error507(zkServers, sessionTimeout), e);
            } catch (Exception e) {
                log.error(ClientLogConstant.error508(), e);
            }
            return this;
        }

        Builder runner(Map<String, Object> beans) {
            if (!CollectionUtils.isEmpty(beans)) {
                beans.forEach((k, v) -> {
                    Method[] methods = v.getClass().getMethods();
                    if (methods != null && methods.length > 0) {
                        for (final Method method : methods) {
                            Task task = AnnotationUtils.findAnnotation(method, Task.class);
                            if (task != null) {
                                context.runner.put(task.key(), new Pair<>(task.cn(), TaskRunnerBuilder.build(v, method)));
                            }
                        }
                    }
                });
            }
            return this;
        }

        Builder pool(Integer threads) {
            context.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
            return this;
        }
    }

    public boolean checkProperties() {
        if (prop == null) {
            log.error(ClientLogConstant.error500("spring配置文件中未找到kob.client属性"));
            return false;
        }
        if (KobUtils.isEmpty(prop.getZkPrefix())) {
            log.error(ClientLogConstant.error500("spring配置文件中未找到kob.client.cluster属性"));
            return false;
        }
        if (KobUtils.isEmpty(prop.getProjectCode())) {
            log.error(ClientLogConstant.error500("spring配置文件中未找到kob.client.project_code属性"));
            return false;
        }
        if (KobUtils.isEmpty(prop.getZkConnectString())) {
            log.error(ClientLogConstant.error500("spring配置文件中未找到kob.client.zk_servers属性"));
            return false;
        }
        if (KobUtils.isEmpty(prop.getAdminUrl())) {
            log.error(ClientLogConstant.error500("spring配置文件中未找到kob.client.admin_url"));
            return false;
        }
        if (KobUtils.isEmpty(beans)) {
            log.error(ClientLogConstant.error500("spring IOC容器未找到有@Kob注解标记的Bean,请检查使用spring scan注解是否有误"));
            return false;
        }
        return true;
    }
//
//    public void build() {
//        if (prop.getZkSessionTimeout() == null) {
//            prop.setZkSessionTimeout(ClientConstant.DEFAULT_ZK_SESSION_TIMEOUT);
//        }
//        if (prop.getZkConnectionTimeout() == null) {
//            prop.setZkConnectionTimeout(ClientConstant.DEFAULT_ZK_CONNECTION_TIMEOUT);
//        }
//        if (KobUtils.isEmpty(prop.getSystemLogPath())) {
//            prop.setSystemLogPath(ClientConstant.DEFAULT_TASK_LOG_PATH);
//        }
//        if (KobUtils.isEmpty(prop.getServiceLogPath())) {
//            prop.setServiceLogPath(ClientConstant.DEFAULT_SERVICE_LOG_PATH);
//        }
//        if (prop.getThreads() == null) {
//            prop.setThreads(ClientConstant.DEFAULT_CLIENT_WORKS);
//        }
//        if (prop.getInitialDelay() == null) {
//            prop.setInitialDelay(ClientConstant.DEFAULT_INITIAL_DELAY);
//        }
//        if (prop.getHeartbeatPeriod() == null) {
//            prop.setHeartbeatPeriod(ClientConstant.DEFAULT_HEARTBEAT_PERIOD);
//        }
//        if (prop.getLogWarnEnable() == null) {
//            prop.setLogWarnEnable(ClientConstant.DEFAULT_LOG_WARN_ENABLE);
//        }
//        if (prop.getExpireRecyclingSec() == null) {
//            prop.setExpireRecyclingSec(ClientConstant.DEFAULT_EXPIRE_RECYCLING_SEC);
//        }
//        if (prop.getLoadFactor() == null) {
//            prop.setLoadFactor(ClientConstant.DEFAULT_LOAD_FACTOR);
//        }
//        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
//        okHttpClient = okHttpClientBuilder.build();
//        clientTaskPath = ZkPathConstant.clientTaskPath(prop.getZkPrefix(), prop.getProjectCode());
//        clientNodePath = ZkPathConstant.clientNodePath(prop.getZkPrefix(), prop.getProjectCode());
//        buildKobRunner(beans);
//        buildClientInfo(prop);
//        ClientPath clientPathLocal = new ClientPath(client.getIp(),client.getIdentification(), client.getProjectCode(), client.getTasks());
//        pathClientInfoLocal = clientNodePath + ZkPathConstant.BACKSLASH + JSON.toJSONString(clientPathLocal);
//        pool = new ThreadPoolExecutor(prop.getThreads(), prop.getThreads(),0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("kob-schedule-performing"));
//    }

    private void buildClientInfo(ClientProperties prop) {
        client.setVersion(ClientConstant.VERSION);
        String ip = IpUtils.getLocalAddress();
        client.setIp(ip);
        String uuid = UuidUtils.builder(UuidUtils.AbbrType.CI);
        client.setIdentification(ip + ZkPathConstant.HYPHEN + uuid);
        client.setProjectCode(prop.getProjectCode());
        Long now = System.currentTimeMillis();
        client.setCreated(now);
        client.setModified(now);
        this.adminUrl = prop.getAdminUrl();
        this.taskLogPath = ClientConstant.DEFAULT_TASK_LOG_PATH;
        this.serviceLogPath = ClientConstant.DEFAULT_SERVICE_LOG_PATH;
        this.clientWorks = prop.getThreads();
        client.setLogWarnEnable(prop.getLogWarnEnable());
        client.setExpireRecyclingTime(prop.getExpireRecyclingSec());
        client.setLoadFactor(prop.getLoadFactor());
        client.setThreads(prop.getThreads());
        this.initialDelay = prop.getInitialDelay();
        client.setHeartbeatPeriod(prop.getHeartbeatPeriod());
    }
}
