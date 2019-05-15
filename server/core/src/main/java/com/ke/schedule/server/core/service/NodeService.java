package com.ke.schedule.server.core.service;


import com.ke.schedule.server.core.model.oz.ClientInfo;
import com.ke.schedule.server.core.model.oz.NodeServer;
import com.ke.schedule.basic.model.ClientPath;

import java.util.List;
import java.util.Map;

public interface NodeService {

    List<NodeServer> getNodeServerList() throws Exception;

    Map<String, ClientInfo> getClientNodes(String projectCode) throws Exception;

    Map<String, ClientPath> getClientPaths(String projectCode) throws Exception;
}
