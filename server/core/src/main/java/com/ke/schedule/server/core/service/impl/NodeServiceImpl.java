package com.ke.schedule.server.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.server.core.model.oz.ClientInfo;
import com.ke.schedule.server.core.model.oz.NodeServer;
import com.ke.schedule.server.core.service.NodeService;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.ClientPath;
import com.ke.schedule.basic.support.KobUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("nodeService")
public class NodeServiceImpl implements NodeService {

    @Resource
    private CuratorFramework curator;

    @Value("${kob-schedule.zk-prefix}")
    private String zp;

    @Override
    public List<NodeServer> getNodeServerList() throws Exception {
        String serverNodePath = ZkPathConstant.serverNodePath(zp);
        if (curator.checkExists().forPath(serverNodePath)==null) {
            return new ArrayList<>();
        }
        List<String> nodeServerStrList = curator.getChildren().forPath(serverNodePath);
        List<NodeServer> nodeServerList = new ArrayList<>();
        if (!KobUtils.isEmpty(nodeServerStrList)) {
            for (String nodeServerStr : nodeServerStrList) {
                byte[] b = curator.getData().forPath(ZkPathConstant.serverNodePath(zp)+ZkPathConstant.BACKSLASH+nodeServerStr);
                NodeServer nodeServer = JSONObject.parseObject(new String(b), NodeServer.class);
                nodeServerList.add(nodeServer);
            }
        }
        return nodeServerList;
    }

    @Override
    public Map<String, ClientInfo> getClientNodes(String projectCode) throws Exception {
        String clientNodePath = ZkPathConstant.clientNodePath(zp, projectCode);
        if (curator.checkExists().forPath(clientNodePath)==null) {
            return new HashMap<>(0);
        }
        List<String> nodeClientStrList = curator.getChildren().forPath(clientNodePath);
        Map<String, ClientInfo> projectClientNode = new HashMap<>(10);
        if (!KobUtils.isEmpty(nodeClientStrList)) {
            for (String child : nodeClientStrList) {
                child = URLDecoder.decode(child, "UTF-8");
                ClientPath clientPath = JSONObject.parseObject(child, ClientPath.class);
                String path = ZkPathConstant.clientNodePath(zp, projectCode) + ZkPathConstant.BACKSLASH + child;
                String dataStr = new String(curator.getData().forPath(path));
                if (!KobUtils.isEmpty(dataStr)) {
                    ClientData clientData = JSONObject.parseObject(dataStr, ClientData.class);
                    projectClientNode.put(clientPath.getIdentification(), new ClientInfo(path, clientPath, clientData));
                }
            }
        }
        return projectClientNode;
    }

    @Override
    public Map<String, ClientPath> getClientPaths(String projectCode) throws Exception {
        String clientNodePath = ZkPathConstant.clientNodePath(zp, projectCode);
        if (curator.checkExists().forPath(clientNodePath)!=null) {
            return null;
        }
        List<String> nodePathStrList = curator.getChildren().forPath(clientNodePath);
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
