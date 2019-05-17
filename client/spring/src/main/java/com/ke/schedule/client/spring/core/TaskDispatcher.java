package com.ke.schedule.client.spring.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.logger.KobOkLogger;
import com.ke.schedule.client.spring.logger.OkHttpLogger;
import com.ke.schedule.client.spring.model.TaskContext;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */

public enum TaskDispatcher {
    //1
    INSTANCE;

    public void dispatcher(ClientContext context, String parent, List<String> childs) {
        if (KobUtils.isEmpty(childs)) {
            return;
        }
        childs.forEach(child -> {
            try  {
                dispatcher0(context, parent, child);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void dispatcher0(ClientContext context, String parentPath, String child) {
        String dec;
        try {
            dec = URLDecoder.decode(child, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        TaskContext.Path path = JSONObject.parseObject(dec, TaskContext.Path.class);
        String full = parentPath + ZkPathConstant.BACKSLASH + child;
        if (System.currentTimeMillis() - path.getTriggerTime() > context.getExpireRecyclingTime()) {
            if(context.getZkClient().delete(full)){
                OkHttpLogger.INSTANCE.expireRecycling(context.getClient(), path);
                okLogger.systemLog(TaskRecordStateConstant.EXPIRE_RECYCLING);
            }
            return;
        }



        if (triggerTime != null) {
            Date now = new Date();
            if (new Date(path.getTriggerTime() - expireTime).after(now)) {
                if (zkClient.exists(full)) {
                    if (zkClient.delete(full)) {
                        if (client.getLogWarnEnable()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = sdf.format(now);
                            log.warn(ClientLogConstant.warn403(clientIdentification, JSON.toJSONString(context), date, expireTime));
                        }
                        okLogger.systemLog(TaskRecordStateConstant.EXPIRE_RECYCLING);
                    }
                }
                return;
            }
        }




        String designatedNode = path.getDesignatedNode();
        String recommendNode = path.getRecommendNode();
        String tryToExclusionNode = path.getTryToExclusionNode();
        final ClientData client = clientContext.getClient();
        final String clientIdentification = client.getIdentification();
        boolean tryToExclusionNodeHasMe = !KobUtils.isEmpty(tryToExclusionNode) && tryToExclusionNode.contains(clientIdentification);
        boolean recommendNodeNotMe = !KobUtils.isEmpty(recommendNode) && !recommendNode.equals(clientIdentification);
        if (tryToExclusionNodeHasMe || recommendNodeNotMe) {
            if (client.getLogWarnEnable()) {
                if (tryToExclusionNodeHasMe) {
                    log.warn(ClientLogConstant.warn401(clientIdentification, JSON.toJSONString(path), 5));
                } else {
                    log.warn(ClientLogConstant.warn407(clientIdentification, recommendNode, 5));
                }
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                log.error(ClientLogConstant.error510(), e);
            }
        }
        final String taskKey = path.getTaskKey();
        ZkClient zkClient = clientContext.getZkClient();
        String fullPath = parentPath + ZkPathConstant.BACKSLASH + currentChild;
        if (clientContext.getRunners(taskKey) == null) {
            if (client.getLogWarnEnable()) {
                log.warn(ClientLogConstant.warn402(clientIdentification, JSON.toJSONString(path)));
            }
            return;
        }
        int expireTime = client.getExpireRecyclingTime();
        final KobOkLogger okLogger = new KobOkLogger(clientContext, path);
        Long triggerTime = path.getTriggerTime();
        if (triggerTime != null) {
            Date now = new Date();
            if (new Date(triggerTime - expireTime).after(now)) {
                if (zkClient.exists(fullPath)) {
                    if (zkClient.delete(fullPath)) {
                        if (client.getLogWarnEnable()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = sdf.format(now);
                            log.warn(ClientLogConstant.warn403(clientIdentification, JSON.toJSONString(context), date, expireTime));
                        }
                        okLogger.systemLog(TaskRecordStateConstant.EXPIRE_RECYCLING);
                    }
                }
                return;
            }
        }
        if (checkPoolSize()) {
            return;
        }
        dispatcher1(context, clientIdentification, taskKey, zkClient, fullPath,  okLogger);
    }
}
