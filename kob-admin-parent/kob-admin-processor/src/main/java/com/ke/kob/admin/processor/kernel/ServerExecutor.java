package com.ke.kob.admin.processor.kernel;

import com.ke.kob.admin.core.model.oz.ProcessorProperties;
import com.ke.kob.admin.core.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 服务端作业调度处理器
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午6:30
 */
@Component("serverExecutor")
@DependsOn("serverContext")
public@Slf4j class ServerExecutor implements InitializingBean {

    @Resource(name = "zkClient")
    private ZkClient zkClient;
    @Resource(name = "kobProcessorProperties")
    private ProcessorProperties processorProperties;
    @Resource(name = "scheduleService")
    private ScheduleService scheduleService;
    @Resource(name = "serverContext")
    private ServerContext serverContext;
    private ServerProcessor serverProcessor;

    @PostConstruct
    public void postConstruct() {
        serverProcessor = new ServerProcessor();
    }

    /**
     * 初始化配置信息
     * 初始化服务端
     * schedule线程: 心跳线程 客户端信息校准，服务端续约等等（可以把等等去了么？）
     * schedule线程：cron类型作业生成未来指定时间间隔内的待执行任务
     * schedule线程: 推送等待执行的任务
     * schedule线程: 处理过期任务的线程
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        serverProcessor.initializeAttributes(processorProperties, zkClient, scheduleService, serverContext);
        serverProcessor.initializeEnvironment();
        serverProcessor.heartbeat();
        serverProcessor.initializeCornTaskExecutor();
        serverProcessor.initializeWaitingTaskExecutor();
        serverProcessor.initializeExpireTaskExecutor();
    }
}
