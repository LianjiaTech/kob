package com.ke.schedule.server.processor.component;

import com.ke.schedule.basic.support.IpUtils;
import com.ke.schedule.basic.support.UuidUtils;
import com.ke.schedule.server.core.model.oz.NodeServer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("serverContext")
public class ServerContext{

    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    private @Getter NodeServer node;

    public ServerContext() {
        node = new NodeServer(zp, mp, IpUtils.getLocalAddress(), UuidUtils.builder(UuidUtils.AbbrType.SN), System.currentTimeMillis());
    }
}
