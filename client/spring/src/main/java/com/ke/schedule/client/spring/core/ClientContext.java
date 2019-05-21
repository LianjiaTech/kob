package com.ke.schedule.client.spring.core;

import com.alibaba.fastjson.JSON;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.basic.model.TaskResult;
import com.ke.schedule.basic.model.ZkAuthInfo;
import com.ke.schedule.basic.support.IpUtils;
import com.ke.schedule.basic.support.UuidUtils;
import com.ke.schedule.client.spring.annotation.Task;
import com.ke.schedule.client.spring.constant.ClientConstant;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午9:42
 */

public @Slf4j
@NoArgsConstructor
class ClientContext {
    private @Getter @Setter Long expireRecyclingTime = 20L * 60L * 1000L;
    private @Getter ClientData data;
    private @Getter ZkClient zkClient;
    private @Getter String clientTaskPath;
    private @Getter String clientNodePath;
    private @Getter Map<String, Pair<String, Function<TaskContext, TaskResult>>> runner;
    private @Getter ThreadPoolExecutor pool;
    private @Getter @Setter String adminUrl;
    private @Getter @Setter String zkConnect;

    static class Builder {
        private ClientContext context;

        Builder() {
            context = new ClientContext();
            context.runner = new ConcurrentHashMap<>();
        }

        public ClientContext build() {
            return context;
        }

        Builder zk(String zkServers, Integer sessionTimeout, Integer connectionTimeout, List<ZkAuthInfo> auths) {
            context.zkConnect = zkServers;
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
                                context.runner.put(task.key(), new Pair<>(task.remark(), TaskRunnerBuilder.build(v, method)));
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

        Builder path(String zkPrefix, String projectCode) {
            context.clientTaskPath = ZkPathConstant.clientTaskPath(zkPrefix, projectCode);
            context.clientNodePath = ZkPathConstant.clientNodePath(zkPrefix, projectCode);
            return this;
        }

        Builder client(String projectCode, Integer threads) {
            context.data = new ClientData();
            context.data.setVersion(ClientConstant.VERSION);
            context.data.setIp(IpUtils.getLocalAddress());
            context.data.setIdentification(context.data.getIp() + ZkPathConstant.HYPHEN + UuidUtils.builder(UuidUtils.AbbrType.CI));
            context.data.setProjectCode(projectCode);
            context.data.setCreated(System.currentTimeMillis());
            context.data.setThreads(threads);
            context.data.setTasks(new HashMap<>());
            context.runner.forEach((k,v) -> context.data.getTasks().put(k, v.getKey()));
            return this;
        }

        Builder admin(String adminUrl) {
            context.adminUrl = adminUrl;
            return this;
        }
    }
}
