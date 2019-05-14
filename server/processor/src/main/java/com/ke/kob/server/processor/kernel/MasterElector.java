package com.ke.kob.server.processor.kernel;

import com.alibaba.fastjson.JSONObject;
import com.ke.kob.server.core.model.oz.NodeServer;
import com.ke.kob.basic.support.IpUtils;
import com.ke.kob.basic.support.KobUtils;
import com.ke.kob.basic.support.UuidUtils;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午5:51
 */

public class MasterElector {

    private static String ip = IpUtils.getLocalAddress();
    private static String uuid = UuidUtils.builder(UuidUtils.AbbrType.SN);
    private ReentrantLock lock = new ReentrantLock();
    private NodeServer master;
    private @Getter NodeServer local;

    public MasterElector(String cluster, long now) {
        local = new NodeServer(cluster, ip, uuid, now);
    }

    static NodeServer getNodeMaster(List<String> currentChilds) {
        NodeServer remoteMaster = null;
        for (String child : currentChilds) {
            NodeServer node = JSONObject.parseObject(child, NodeServer.class);
            if (remoteMaster == null || node.getCreated() < remoteMaster.getCreated()) {
                remoteMaster = node;
            }
        }
        return remoteMaster;
    }

    public void elector(List<String> currentChilds) {
        if (KobUtils.isEmpty(currentChilds)) {
            return;
        }
        lock.lock();
        try {
            NodeServer remoteMaster = getNodeMaster(currentChilds);
            if (this.master == null || !this.master.getIdentification().equals(remoteMaster.getIdentification())) {
                this.master = remoteMaster;
                if (this.local.getIdentification().equals(remoteMaster.getIdentification())) {
                    System.out.println("master is me");
                } else {
                    System.out.println("master is not");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    NodeServer getMaster() {
        return this.master;
    }

    boolean isMaster() {
        return this.local.getIdentification().equals(this.master.getIdentification());
    }
}
