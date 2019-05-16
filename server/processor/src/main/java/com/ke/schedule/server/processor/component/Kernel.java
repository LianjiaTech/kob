package com.ke.schedule.server.processor.component;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhaoyuguang
 */
@Component
public class Kernel implements InitializingBean {

    @Resource
    private CornTask cornTask;
    @Resource
    private WaitingTask waitingTask;
    @Resource
    private Heartbeat heartbeat;

    @Override
    public void afterPropertiesSet() {
        cornTask.initialize();
        waitingTask.initialize();
        heartbeat.initialize();
    }
}
