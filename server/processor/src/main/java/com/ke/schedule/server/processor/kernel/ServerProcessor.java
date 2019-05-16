//package com.ke.schedule.server.processor.kernel;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ke.schedule.basic.support.KobUtils;
//import com.ke.schedule.basic.support.NamedThreadFactory;
//import com.ke.schedule.server.core.common.AdminLogConstant;
//import com.ke.schedule.server.core.model.db.JobCron;
//import com.ke.schedule.server.core.model.db.TaskWaiting;
//import com.ke.schedule.server.core.model.oz.ProcessorProperties;
//import com.ke.schedule.server.core.service.ScheduleService;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.zookeeper.CreateMode;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * 服务端作业调度执行器
// *
// * @Author: zhaoyuguang
// * @Date: 2018/8/10 下午12:21
// */
//
//public @NoArgsConstructor @Slf4j class ServerProcessor {
//
//    private static final ScheduledExecutorService CRON_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("cron-task", true));
//    private static final ScheduledExecutorService WAITING_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("waiting-task", true));
//    private static final ScheduledExecutorService EXPIRE_TASK_EXECUTOR = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("expire-task", true));
//    private static final ScheduledExecutorService SERVER_HEARTBEAT = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("kob-server-heartbeat", true));
//
//    private static final Integer CURSOR_100 = 100;
//    private CuratorFramework curator;
//    private ProcessorProperties processorProperties;
//    private ServerContext serverContext;
//    private ScheduleService scheduleService;
//
//    public void initializeAttributes(ProcessorProperties processorProperties, CuratorFramework curator, ScheduleService scheduleService, ServerContext serverContext) {
//        this.processorProperties = processorProperties;
//        this.serverContext = serverContext;
//        this.curator = curator;
//        this.scheduleService = scheduleService;
//    }
//
//    public void initializeEnvironment() {
//        String masterNodePath = serverContext.getMasterPath();
//        try {
//            if (curator.checkExists().forPath(masterNodePath)!=null) {
//                try {
//                    curator.create().withMode(CreateMode.PERSISTENT).forPath(masterNodePath);
//                } catch (Exception e) {
//                    //todo
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            //todo
//            e.printStackTrace();
//        }
////        zkClient.subscribeChildChanges(masterNodePath, new IZkChildListener() {
////            @Override
////            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
////                serverContext.getMasterElector().elector(currentChilds);
////            }
////        });
////        zkClient.createEphemeral(serverContext.getLocalNodePath());
//    }
//
//    /**
//     * schedule线程：cron类型作业生成未来指定时间间隔内的待执行任务
//     */
//    void initializeCornTaskExecutor() {
//        CRON_TASK_EXECUTOR.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (serverContext.isMaster()) {
//                    try {
//                        jobCronGenerateWaitingTask();
//                    } catch (Exception e) {
//                        log.error(AdminLogConstant.error9100(), e);
//                    }
//                }
//            }
//        }, processorProperties.getCronTaskExecutorInitialDelaySec(), processorProperties.getCronTaskExecutorPeriodSec(), TimeUnit.SECONDS);
//    }
//
//    /**
//     * cron类型作业生成未来指定时间间隔内的待执行任务
//     * 遍历未暂停的cron类型作业，
//     * 通过事务生成未来一定时间内的作业，并更新cron任务的最后生成触发时间
//     */
//    private void jobCronGenerateWaitingTask() {
//        List<JobCron> jobCronList = scheduleService.findRunningCronJob(serverContext.getZp());
//        if (!KobUtils.isEmpty(jobCronList)) {
//            Date now = new Date();
//            for (JobCron jobCron : jobCronList) {
//                try {
//                    scheduleService.createCronWaitingTaskForTime(serverContext.getLocalIdentification(), jobCron, processorProperties.getAppendPreviousTask(), processorProperties.getIntervalMin(), serverContext.getZp(), now);
//                } catch (Exception e) {
//                    log.error(AdminLogConstant.error9101(JSONObject.toJSONString(jobCron)), e);
//                }
//            }
//        }
//    }
//
//    /**
//     * schedule线程: 推送等待执行的任务
//     */
//
//
//    /**
//     * 推送等待执行的任务
//     * 锁定待推送任务 lockPushTask
//     * 推送任务 pushTask
//     */
//    private void pushWaitingTask() {
//        long now = System.currentTimeMillis();
//        List<TaskWaiting> taskWaitingList = scheduleService.findTriggerTaskInLimit(now, processorProperties.getWaitingTaskScroll(), serverContext.getZp());
//        if (!KobUtils.isEmpty(taskWaitingList)) {
//            for (final TaskWaiting taskWaiting : taskWaitingList) {
//                try {
//                    Boolean lastTaskComplete = scheduleService.lockPushTask(taskWaiting, serverContext.getZp(), serverContext.getLocalIdentification());
//                    if (taskWaiting.getRely() && lastTaskComplete != null && !lastTaskComplete) {
//                        continue;
//                    }
//                } catch (Exception e) {
//                    log.error(AdminLogConstant.error9102(JSONObject.toJSONString(taskWaiting)), e);
//                    continue;
//                }
//                try {
////                    scheduleService.pushTask(zkClient, taskWaiting, serverContext.getZp());
//                    recoveryOverstockTask(taskWaiting.getProjectCode());
//                } catch (Exception e) {
//                    log.error(AdminLogConstant.error9103(JSONObject.toJSONString(taskWaiting)), e);
//                }
//            }
//        }
//    }
//
//    /**
//     * todo
//     * 待回收积压任务
//     * 根据一定权重进入回收方法，判断是否超过积压阈值，并回收已过期任务只可剩余任务数量
//     *
//     * @param projectCode 项目名称
//     */
//    private void recoveryOverstockTask(String projectCode) throws UnsupportedEncodingException {
////        int random100 = new Random().nextInt(AdminConstant.ONE_HUNDRED);
////        if (random100 < processorProperties.getTaskOverstockRecoveryWeight()) {
////            List<String> taskPathList = curator.getChildren(ZkPathConstant.clientTaskPath(serverContext.getZp(), projectCode));
////            if (!KobUtils.isEmpty(taskPathList) && taskPathList.size() > processorProperties.getTaskOverstockRecoveryThreshold()) {
////                List<TaskBaseContext> tasks = new ArrayList<>();
////                for (String s : taskPathList) {
////                    TaskBaseContext task = JSONObject.parseObject(s, TaskBaseContext.class);
////                    //todo
////                    // task.setPath(ZkPathConstant.clientTaskPath(serverContext.getZp(), projectCode) + ZkPathConstant.BACKSLASH + s);
////                    tasks.add(task);
////                }
////                Collections.sort(tasks);
////                List<TaskBaseContext> overstockTask = tasks.subList(0, tasks.size() - processorProperties.getTaskOverstockRecoveryRetainCount());
////                scheduleService.fireOverstockTask(zkClient, overstockTask, serverContext.getZp());
////            }
////        }
//    }
//
//    /**
//     * 心跳线程 客户端信息校准，服务端续约
//     */
//    void heartbeat() {
//        SERVER_HEARTBEAT.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
////todo                try {
////                    if (!zkClient.exists(serverContext.getMasterPath())) {
////                        log.warn("父节点不存在");
////                        return;
////                    }
////                    if (!zkClient.exists(serverContext.getLocalNodePath())) {
////                        zkClient.createEphemeral(serverContext.getLocalNodePath());
////                        log.warn("我的节点不存在");
////                        return;
////                    }
////                    List<String> currentChilds = zkClient.getChildren(ZkPathConstant.serverNodePath(serverContext.getZp()));
////                    NodeServer currentNodeServerMaster = MasterElector.getNodeMaster(currentChilds);
////                    if (!currentNodeServerMaster.getIdentification().equals(serverContext.getMasterElector().getMaster().getIdentification())) {
////                        zkClient.writeData(serverContext.getLocalNodePath(), new MasterElectorNotice(serverContext.getLocalIdentification()));
////                        log.warn("选举可能存在问题 从新选举 好像不能触发watch");
////                    }
////                    log.warn("心跳");
////                } catch (Exception e) {
////                    log.error("心跳 error", e);
////                }
////                try {
////                    Set<String> currentProjectCodeSet = scheduleService.selectServiceProjectCodeSet();
////                    Set<String> localProjectCodeSet = serverContext.getProjectCodeSet();
////                    if (KobUtils.isEmpty(currentProjectCodeSet)) {
////                        for (final String currentProjectCode : currentProjectCodeSet) {
////                            if (localProjectCodeSet.add(currentProjectCode)) {
////                                zkClient.subscribeChildChanges(ZkPathConstant.clientNodePath(serverContext.getZp(), currentProjectCode), new IZkChildListener() {
////                                    @Override
////                                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
////                                        refreshClientNode(currentChilds, currentProjectCode);
////                                    }
////                                });
////                            }
////                        }
////                    }
////                } catch (Exception e) {
////                    log.error("心跳 error", e);
////                }
//            }
//        }, processorProperties.getHeartbeatInitialDelaySec(), processorProperties.getHeartbeatPeriodSec(), TimeUnit.SECONDS);
//    }
//
//    private void refreshClientNode(List<String> currentChilds, String project) {
////todo        Map<String, ClientInfo> projectClientNode = new ConcurrentHashMap<>();
////        if (!KobUtils.isEmpty(currentChilds)) {
////            for (String child : currentChilds) {
////                ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
////                String path = ZkPathConstant.clientNodePath(serverContext.getZp(), project) + ZkPathConstant.BACKSLASH + child;
////                String dataStr = zkClient.readData(path, true);
////                if (!KobUtils.isEmpty(dataStr)) {
////                    ClientData clientData = JSONObject.parseObject(dataStr, ClientData.class);
////                    projectClientNode.put(clientPath.getIdentification(), new ClientInfo(path, clientPath, clientData));
////                }
////            }
////        }
////        serverContext.getClientNodeMap().put(project, projectClientNode);
//    }
//
//    public void initializeExpireTaskExecutor() {
//        EXPIRE_TASK_EXECUTOR.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
////todo                try {
////                    if (serverContext.isMaster()) {
////                        long now = System.currentTimeMillis();
////                        int expireCount = scheduleService.selectCountExpireTaskRecord(now, serverContext.getZp());
////                        if (expireCount > 0) {
////                            int start = expireCount / CURSOR_100 * CURSOR_100;
////                            int limit = expireCount - start;
////                            do {
////                                List<TaskRecord> taskExpireList = scheduleService.selectListExpireTaskRecord(now, start, limit, serverContext.getZp());
////                                if (KobUtils.isEmpty(taskExpireList)) {
////                                    start = start - CURSOR_100;
////                                    limit = CURSOR_100;
////                                    continue;
////                                }
////                                for (TaskRecord taskExpire : taskExpireList) {
////                                    scheduleService.handleExpireTask(zkClient, taskExpire, serverContext.getZp());
////                                }
////                                start = start - CURSOR_100;
////                                limit = CURSOR_100;
////                            } while (start >= 0);
////                        }
////                    }
////                } catch (Exception e) {
////                    log.error("server_admin_code_error_102:过期数据计算异常", e);
////                }
//            }
//        }, processorProperties.getHeartbeatInitialDelaySec(), processorProperties.getHeartbeatPeriodSec(), TimeUnit.SECONDS);
//    }
//}
