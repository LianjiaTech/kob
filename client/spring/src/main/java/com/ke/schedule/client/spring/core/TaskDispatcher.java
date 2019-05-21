package com.ke.schedule.client.spring.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.basic.model.TaskResult;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.logger.OkHttpLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhaoyuguang
 */

public @Slf4j
enum TaskDispatcher {
    //INSTANCE
    INSTANCE;

    public void dispatcher(ClientContext context, String parent, List<String> childs) {
        if (KobUtils.isEmpty(childs)) {
            return;
        }
        childs.forEach(child -> {
            try {
                System.out.println(child);
                dispatcher0(context, parent, child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void dispatcher0(ClientContext client, String parentPath, String child) {
        TaskContext.Path path = ClientFunction.INSTANCE.convertPath().apply(child);
        if (path == null) return;
        String full = parentPath + ZkPathConstant.BACKSLASH + child;
        if (ClientFunction.INSTANCE.fireExpire().test(new Object[]{path, client, full})) return;
        if (!client.getRunner().containsKey(path.getTaskKey())) return;
        if (path.getDesignatedNode() != null) {
            if (!client.getData().getIdentification().equals(path.getDesignatedNode())) return;
        }
        if (path.getRecommendNode() != null) {
            if (!client.getData().getIdentification().equals(path.getRecommendNode()))
                ClientFunction.INSTANCE.sleep().accept(5);
        }
        if (ClientFunction.INSTANCE.tryToExclusionNodeHasMe().test(new Object[]{path, client})) return;
        if (ClientFunction.INSTANCE.checkPoolSize().test(client)) return;
        dispatcher1(client, path, full);
    }

    private void dispatcher1(ClientContext client, TaskContext.Path path, String full) {
        String data = "";
        try {
            data = client.getZkClient().readData(full);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (client.getZkClient().delete(full)) {
            log.info(ClientLogConstant.info100(JSON.toJSONString(path), client.getData().getIdentification()));
            OkHttpLogger.INSTANCE.systemLog(client, path, TaskRecordStateConstant.RECEIVE_SUCCESS);
            TaskContext context = new TaskContext(path, JSONObject.parseObject(data, TaskContext.Data.class));
            client.getPool().execute(() -> {
                try {
                    OkHttpLogger.INSTANCE.systemLog(client, path, TaskRecordStateConstant.RUNNER_START);
                    TaskResult result = client.getRunner().get(path.getTaskKey()).getValue().apply(context);
                    OkHttpLogger.INSTANCE.systemLog(client, path, result.getState(), result.getMsg());
                } catch (Exception e) {
                    log.error(ClientLogConstant.error502(JSON.toJSONString(context), client.getData().getIdentification()), e);
                    OkHttpLogger.INSTANCE.systemLog(client, path, TaskRecordStateConstant.EXECUTE_EXCEPTION, e);
                }
            });
        }
    }
}
