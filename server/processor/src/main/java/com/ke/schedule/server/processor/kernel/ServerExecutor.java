//package com.ke.schedule.server.processor.kernel;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ke.schedule.basic.constant.ZkPathConstant;
//import com.ke.schedule.basic.support.KobUtils;
//import com.ke.schedule.basic.support.NamedThreadFactory;
//import com.ke.schedule.server.core.common.AdminLogConstant;
//import com.ke.schedule.server.core.model.db.JobCron;
//import com.ke.schedule.server.core.model.oz.ProcessorProperties;
//import com.ke.schedule.server.core.service.ScheduleService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.zookeeper.CreateMode;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * 服务端作业调度处理器
// *
// * @Author: zhaoyuguang
// * @Date: 2018/7/30 下午6:30
// */
//@Component("serverExecutor")
//@DependsOn("serverContext")
//public @Slf4j class ServerExecutor implements InitializingBean {
//
//    @Value("${kob-schedule.zk-prefix}")
//    private String zp;
//
//
////    @Resource(name = "zkClient")
////    private ZkClient zkClient;
////    @Resource(name = "kobProcessorProperties")
//    private ProcessorProperties processorProperties;
////    @Resource(name = "scheduleService")
//    private ScheduleService scheduleService;
//    @Resource(name = "serverContext")
//    private ServerContext serverContext;
//    private ServerProcessor serverProcessor;
//
//
//    @PostConstruct
//    public void postConstruct() {
//        serverProcessor = new ServerProcessor();
//    }
//
//    /** todo
//     * 初始化配置信息
//     * 初始化服务端
//     * schedule线程: 心跳线程 客户端信息校准，服务端续约等等（可以把等等去了么？）
//     * schedule线程：cron类型作业生成未来指定时间间隔内的待执行任务
//     * schedule线程: 推送等待执行的任务
//     * schedule线程: 处理过期任务的线程
//     *
//     * @throws Exception 异常
//     */
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        serverProcessor.initializeAttributes(processorProperties, null, scheduleService, serverContext);
//        serverProcessor.initializeEnvironment();
//        serverProcessor.heartbeat();
////        this.initializeWaitingTaskExecutor();
//        serverProcessor.initializeExpireTaskExecutor();
//    }
//
//
//
//
////
////    void initializeWaitingTaskExecutor() {
////        WAITING_TASK_EXECUTOR.scheduleAtFixedRate(() -> pushWaitingTask(), processorProperties.getWaitingTaskExecutorInitialDelayMs(), processorProperties.getWaitingTaskExecutorPeriodMs() * 20, TimeUnit.MILLISECONDS);
////    }
////
////    private void pushWaitingTask() {
////        long now = System.currentTimeMillis();
////        List<TaskWaiting> taskWaitingList = scheduleService.findTriggerTaskInLimit(now, 10, serverContext.getZp());
////        if (!KobUtils.isEmpty(taskWaitingList)) {
////            for (final TaskWaiting taskWaiting : taskWaitingList) {
////                try {
////                    Boolean lastTaskComplete = scheduleService.lockPushTask(taskWaiting, serverContext.getZp(), serverContext.getLocalIdentification());
////                    if (taskWaiting.getRely() && lastTaskComplete != null && !lastTaskComplete) {
////                        continue;
////                    }
////                } catch (Exception e) {
////                    log.error(AdminLogConstant.error9102(JSONObject.toJSONString(taskWaiting)), e);
////                    continue;
////                }
////                try {
//////                    scheduleService.pushTask(zkClient, taskWaiting, serverContext.getZp());
////                    recoveryOverstockTask(taskWaiting.getProjectCode());
////                } catch (Exception e) {
////                    log.error(AdminLogConstant.error9103(JSONObject.toJSONString(taskWaiting)), e);
////                }
////            }
////        }
////    }
////
////    private void recoveryOverstockTask(String projectCode) throws UnsupportedEncodingException {
////        int random100 = new Random().nextInt(AdminConstant.ONE_HUNDRED);
////        if (random100 < processorProperties.getTaskOverstockRecoveryWeight()) {
////            List<String> taskPathList = curator.getChildren(ZkPathConstant.clientTaskPath(serverContext.getZp(), projectCode));
////            if (!KobUtils.isEmpty(taskPathList) && taskPathList.size() > processorProperties.getTaskOverstockRecoveryThreshold()) {
////                List<TaskContext> tasks = new ArrayList<>();
////                for (String s : taskPathList) {
////                    TaskContext task = JSONObject.parseObject(s, TaskContext.class);
////                    //todo
////                    // task.setPath(ZkPathConstant.clientTaskPath(serverContext.getZp(), projectCode) + ZkPathConstant.BACKSLASH + s);
////                    tasks.add(task);
////                }
////                Collections.sort(tasks);
////                List<TaskContext> overstockTask = tasks.subList(0, tasks.size() - processorProperties.getTaskOverstockRecoveryRetainCount());
////                scheduleService.fireOverstockTask(zkClient, overstockTask, serverContext.getZp());
////            }
////        }
////    }
//}
