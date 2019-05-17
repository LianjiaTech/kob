package com.ke.schedule.client.spring.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.TaskResult;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.logger.KobOkLogger;
import com.ke.schedule.client.spring.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/1 下午4:39
 */
public @Slf4j class TaskDispatcher2 {

    private ClientContext clientContext;

    public TaskDispatcher2(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    /**
     * 分发任务总入口
     *
     * @param parentPath    zk节点父节点 path
     * @param currentChilds zk节点任务节点 path list
     */
    public void dispatcher(String parentPath, List<String> currentChilds) {
        if (KobUtils.isEmpty(currentChilds)) {
            return;
        }
        for (String currentChild : currentChilds) {
            try  {
                dispatcher0(parentPath, currentChild);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * dispatcher0 方法偏向校验
     * 根据designatedNode 执行指定节点校验
     * 根据tryToExclusionNode 执行排除节点校验
     * 根据recommendNode 执行推荐节点校验
     * 根据taskKey 判断此客户端是否支持此任务
     * 根据triggerTime 判断是否已经过期很久需要不执行回收
     *
     * @param parentPath   zk节点父节点 path
     * @param currentChild zk节点任务节点 path
     */
    private void dispatcher0(String parentPath, String currentChild) throws UnsupportedEncodingException {
        String decodedString = URLDecoder.decode(currentChild, "UTF-8");
        final TaskContext.Path path = JSONObject.parseObject(decodedString, TaskContext.Path.class);
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

    /**
     * dispatcher1 方法偏向任务执行
     *
     * @param context              任务context
     * @param clientIdentification 节点标识
     * @param taskKey              任务方法
     * @param zkClient             zk客户端
     * @param fullPath             节点全部路径
     * @param okLogger             日志上报实现
     */
    private void dispatcher1(final TaskContext context, final String clientIdentification, final String taskKey, ZkClient zkClient, String fullPath,  final KobOkLogger okLogger) {
        String data = "";
        try {
            data = zkClient.readData(fullPath);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (!zkClient.exists(fullPath) || !zkClient.delete(fullPath)) {
            if (clientContext.getClient().getLogWarnEnable()) {
                log.warn(ClientLogConstant.warn405(JSON.toJSONString(context), clientIdentification));
            }
            return;
        }
        log.info(ClientLogConstant.info100(JSON.toJSONString(context), clientIdentification));
        okLogger.systemLog(TaskRecordStateConstant.RECEIVE_SUCCESS);
        clientContext.getPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    okLogger.systemLog(TaskRecordStateConstant.RUNNER_START);
                    TaskResult result = clientContext.getRunners(taskKey).run(context);
                    log.info(ClientLogConstant.info103(context.getData().getJobCn(), path.getTaskKey(), result.getState()));
                    okLogger.systemLog(result);
                } catch (Exception e) {
                    log.error(ClientLogConstant.error502(JSON.toJSONString(context), clientIdentification), e);
                    okLogger.systemLog(TaskRecordStateConstant.EXECUTE_EXCEPTION, e);
                }
            }
        });
    }

    private boolean checkPoolSize() {
        int active = clientContext.getPool().getActiveCount();
        int max = clientContext.getPool().getMaximumPoolSize();
        double value = new BigDecimal((float) active / max).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double loadFactor = clientContext.getClient().getLoadFactor();
        if (value > loadFactor) {
            if (clientContext.getClient().getLogWarnEnable()) {
                log.warn(ClientLogConstant.warn404(loadFactor, clientContext.getClient().getIdentification(), active, max));
            }
            return true;
        }
        return false;
    }
}