package com.ke.kob.server.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ke.kob.server.core.model.oz.ClientInfo;
import com.ke.kob.server.core.model.oz.NodeServer;
import com.ke.kob.server.core.service.NodeService;
import com.ke.kob.basic.constant.ZkPathConstant;
import com.ke.kob.basic.model.ClientData;
import com.ke.kob.basic.model.ClientPath;
import com.ke.kob.basic.support.KobUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点信息Service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/23 下午4:55
 */
@Service("nodeService")
public class NodeServiceImpl implements NodeService {

    @Resource(name = "zkClient")
    private ZkClient zkClient;
    @Value("${kob.cluster}")
    private String cluster;

    @Override
    public List<NodeServer> getNodeServerList() {
        String serverNodePath = ZkPathConstant.serverNodePath(cluster);
        if (!zkClient.exists(serverNodePath)) {
            return new ArrayList<>();
        }
        List<String> nodeServerStrList = zkClient.getChildren(serverNodePath);
        List<NodeServer> nodeServerList = new ArrayList<>();
        if (!KobUtils.isEmpty(nodeServerStrList)) {
            for (String nodeServerStr : nodeServerStrList) {
                NodeServer nodeServer = JSONObject.parseObject(nodeServerStr, NodeServer.class);
                nodeServerList.add(nodeServer);
            }
        }
        return nodeServerList;
    }

    @Override
    public Map<String, ClientInfo> getClientNodes(String projectCode) {
        String clientNodePath = ZkPathConstant.clientNodePath(cluster, projectCode);
        if (!zkClient.exists(clientNodePath)) {
            return new HashMap<>(0);
        }
        List<String> nodeClientStrList = zkClient.getChildren(clientNodePath);
        Map<String, ClientInfo> projectClientNode = new HashMap<>(10);
        if (!KobUtils.isEmpty(nodeClientStrList)) {
            for (String child : nodeClientStrList) {
                ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
                String path = ZkPathConstant.clientNodePath(cluster, projectCode) + ZkPathConstant.BACKSLASH + child;
                String dataStr = zkClient.readData(path, true);
                if (!KobUtils.isEmpty(dataStr)) {
                    ClientData clientData = JSONObject.parseObject(dataStr, ClientData.class);
                    projectClientNode.put(clientPath.getIdentification(), new ClientInfo(path, clientPath, clientData));
                }
            }
        }
        return projectClientNode;
    }

    @Override
    public Map<String, ClientPath> getClientPaths(String projectCode) {
        String clientNodePath = ZkPathConstant.clientNodePath(cluster, projectCode);
        if (!zkClient.exists(clientNodePath)) {
            return null;
        }
        List<String> nodePathStrList = zkClient.getChildren(clientNodePath);
        Map<String, ClientPath> projectClientPath = new HashMap<>(10);
        if (!KobUtils.isEmpty(nodePathStrList)) {
            for (String child : nodePathStrList) {
                ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
                projectClientPath.put(clientPath.getIdentification(), clientPath);
            }
        }
        return projectClientPath;
    }
}
