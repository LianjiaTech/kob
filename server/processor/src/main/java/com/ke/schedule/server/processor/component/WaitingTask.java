package com.ke.schedule.server.processor.component;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.*;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.basic.support.NamedThreadFactory;
import com.ke.schedule.server.core.common.AdminConstant;
import com.ke.schedule.server.core.common.AdminLogConstant;
import com.ke.schedule.server.core.common.NodeHashLoadBalance;
import com.ke.schedule.server.core.mapper.TaskRecordMapper;
import com.ke.schedule.server.core.mapper.TaskWaitingMapper;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.model.db.TaskWaiting;
import com.ke.schedule.server.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang
 */
@Component
public @Slf4j
class WaitingTask {

    private static final ScheduledExecutorService WAITING_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("waiting-task", true));

    @Resource(name = "scheduleService")
    private ScheduleService scheduleService;
    @Resource
    private CuratorFramework curator;
    @Resource
    private TaskWaitingMapper taskWaitingMapper;
    @Resource
    private TaskRecordMapper taskRecordMapper;

    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;


    void initialize() {
        WAITING_TASK_EXECUTOR.scheduleAtFixedRate(() -> pushWaitingTask(), 2000, 1000, TimeUnit.MILLISECONDS);
    }

    private void pushWaitingTask() {
        boolean create = false;
        String path = ZkPathConstant.serverWaitPath(zp);
        try {
            System.out.println("WAITING_TASK_EXECUTOR");
            if (curator.checkExists().forPath(path) != null) {
                byte[] b = curator.getData().forPath(path);
                String s = new String(b);
                LockData data = JSONObject.parseObject(s, LockData.class);
                if(data.getExpire()>System.currentTimeMillis()){
                    curator.delete().forPath(path);
                }else {
                    return;
                }
            }
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, JSONObject.toJSONString(new LockData("ip:xxx", System.currentTimeMillis() + 1000 * 60 * 5)).getBytes());//todo
            pushWaitingTask0();
            create = true;
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
    }

    private void pushWaitingTask0() {
        long now = System.currentTimeMillis();
        List<TaskWaiting> taskWaitingList = scheduleService.findTriggerTaskInLimit(now, 10, mp);
        if (!CollectionUtils.isEmpty(taskWaitingList)) {
            taskWaitingList.forEach(this::pushWaitingTask1);
        }
    }

    private void pushWaitingTask1(TaskWaiting tw) {
        //todo delete insert
        //todo overtime put or update
        Boolean lastTaskComplete = null;
        String relyUndoTaskUuid = null;

        int deleteCount = taskWaitingMapper.deleteOne(tw.getTaskUuid(), mp);
        if (deleteCount != 1) {
            log.error("server_admin_code_error_100:删除等待任务数量不为1");
            return;
        }
        TaskRecord taskRecord = createCommonTaskRecord(tw, "serverId", lastTaskComplete, relyUndoTaskUuid);
        if (tw.getRely()) {
            TaskRecord lastTask = taskRecordMapper.selectLastUndoTaskByJobUuid(tw.getJobUuid(), mp);
            if (lastTask == null && lastTask.getComplete()) {
                taskRecord.setComplete(true);
                taskRecordMapper.insertOne(taskRecord, mp);
                return;
            }
        }
        //todo 如果于现在时间差别很大 直接丢掉

        int insertCount = taskRecordMapper.insertOne(taskRecord, mp);
        if (insertCount != 1) {
            log.error("server_admin_code_error_101:插入任务记录数量不为1");
            throw new RuntimeException("server_code_error_101:插入任务记录数量不为1");
        }
        //todo 检查zk
        recoveryOverstockTask(taskRecord.getProjectCode());

        TaskBaseContext context = new TaskBaseContext();
        context.getData().setProjectCode(tw.getProjectCode());
        context.getData().setJobUuid(tw.getJobUuid());
        context.getData().setJobCn(tw.getJobCn());
        context.getPath().setTaskUuid(tw.getTaskUuid());
        context.getPath().setTaskKey(tw.getTaskKey());
        context.getPath().setTriggerTime(tw.getTriggerTime());
        context.getPath().setDesignatedNode(tw.getInnerParamsBean().getDesignatedNode());
        context.getPath().setRecommendNode(tw.getInnerParamsBean().getRecommendNode());
        context.getPath().setTryToExclusionNode(tw.getInnerParamsBean().getTryToExclusionNode());
        context.getData().setUserParam(JSONObject.parseObject(tw.getUserParams()));
        String projectTaskPath = ZkPathConstant.clientTaskPath(zp, context.getData().getProjectCode());
        int state = TaskRecordStateConstant.PUSH_SUCCESS;
        Map<String, Object> param = new HashMap<>(10);
        try {
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(projectTaskPath + ZkPathConstant.BACKSLASH + context.getZkPath());
        } catch (Exception e) {
            log.error("pushTask_error 推送zk事件异常", e);
            state = TaskRecordStateConstant.PUSH_FAIL;
            param.put("complete", true);
        }
        param.put("state", state);
        taskRecordMapper.updateByTaskUuid(param, tw.getTaskUuid(), mp);

    }


    /**
     * 生成通用任务记录
     *
     * @param tw                   等待推送任务
     * @param serverIdentification server节点唯一标识
     * @param lastTaskComplete     上一次作业是否完成
     * @param relyUndoTaskUuid     是否依赖上周期的uuid
     * @return 任务记录
     */
    private TaskRecord createCommonTaskRecord(TaskWaiting tw, String serverIdentification, Boolean lastTaskComplete,
                                              String relyUndoTaskUuid) {
        TaskRecord tr = new TaskRecord();
        tr.setProjectCode(tw.getProjectCode());
        tr.setProjectName(tw.getProjectName());
        tr.setJobUuid(tw.getJobUuid());
        tr.setJobType(tw.getJobType());
        tr.setJobCn(tw.getJobCn());
        tr.setTaskKey(tw.getTaskKey());
        tr.setTaskRemark(tw.getTaskRemark());
        tr.setTaskType(TaskType.NONE.name());
        tr.setTaskUuid(tw.getTaskUuid());
        tr.setRelationTaskUuid(tw.getTaskUuid());
        tr.setLoadBalance(tw.getLoadBalance());
        tr.setRetryType(tw.getRetryType());
        tr.setBatchType(tw.getBatchType());
        tr.setRely(tw.getRely());
        tr.setAncestor(true);
        tr.setUserParams(tw.getUserParams());
        tr.setClientIdentification("");
        InnerParams innerParams = KobUtils.isEmpty(tw.getInnerParams()) ? new InnerParams() : JSONObject.parseObject(tw.getInnerParams(), InnerParams.class);
        innerParams.setTaskPushNode(serverIdentification);
        if (LoadBalanceType.NODE_HASH.name().equals(tw.getLoadBalance())) {
            List<String> clientNodePathList = null;
            try {
                clientNodePathList = curator.getChildren().forPath(ZkPathConstant.clientNodePath(zp, tw.getProjectCode()));
            } catch (Exception e) {
                //todo
                log.error("e", e);
            }
            List<String> nodeList = new ArrayList<>();
            if (!KobUtils.isEmpty(clientNodePathList)) {
                for (String child : clientNodePathList) {
//                  todo what mean  ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
                    nodeList.add(child);
                }
            }
            innerParams.setRecommendNode(NodeHashLoadBalance.doSelect(nodeList, tw.getJobUuid()));
            innerParams.setRelyUndoTaskUuid(relyUndoTaskUuid);
        }

        tr.setCronExpression(tw.getCronExpression());
        tr.setTimeoutThreshold(tw.getTimeoutThreshold());
        if (tw.getRely()) {
            tr.setState(lastTaskComplete ? TaskRecordStateConstant.WAITING_PUSH : TaskRecordStateConstant.RELY_UNDO);
            tr.setComplete(!lastTaskComplete);
            innerParams.setRelyUndoTaskUuid(relyUndoTaskUuid);
        } else {
            tr.setState(TaskRecordStateConstant.WAITING_PUSH);
            tr.setComplete(false);
        }
        tr.setInnerParams(JSONObject.toJSONString(innerParams));
        tr.setRetryCount(tw.getRetryCount());
        tr.setTriggerTime(tw.getTriggerTime());
        return tr;
    }

    private void recoveryOverstockTask(String projectCode) {
        try {
            int random100 = new Random().nextInt(AdminConstant.ONE_HUNDRED);
            if (random100 < 20) {
                List<String> taskPathList = curator.getChildren().forPath(ZkPathConstant.clientTaskPath(zp, projectCode));
                if (!CollectionUtils.isEmpty(taskPathList) && taskPathList.size() > 40) {
                    log.error("send qx");
                    taskPathList.forEach(this::recoveryOverstockTask0);
                    List<TaskBaseContext> tasks = new ArrayList<>();
                    for (String s : taskPathList) {
                        TaskBaseContext task = JSONObject.parseObject(s, TaskBaseContext.class);
                        //todo
                        // task.setPath(ZkPathConstant.clientTaskPath(serverContext.getZp(), projectCode) + ZkPathConstant.BACKSLASH + s);
                        tasks.add(task);
                    }
                    //todo Collections.sort(tasks);
                    List<TaskBaseContext> overstockTask = tasks.subList(0, tasks.size() - 30);
                    scheduleService.fireOverstockTask(overstockTask);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recoveryOverstockTask0(String s) {
    }
}
