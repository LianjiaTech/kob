package com.ke.kob.server.core.service;


import com.ke.kob.server.core.model.oz.ClientInfo;
import com.ke.kob.server.core.model.oz.NodeServer;
import com.ke.kob.basic.model.ClientPath;

import java.util.List;
import java.util.Map;

/**
 * 节点信息Service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/23 下午4:55
 */

public interface NodeService {

    /**
     * 获取服务端节点
     *
     * @return List<NodeServer>
     */
    List<NodeServer> getNodeServerList();

    /**
     * 获取客户端节点详情
     *
     * @param projectCode 项目标识
     * @return 返回客户端节点map
     */
    Map<String, ClientInfo> getClientNodes(String projectCode);

    /**
     * 获得客户端节点概况
     *
     * @param projectCode 项目标识
     * @return 返回客户端节点路径map
     */
    Map<String, ClientPath> getClientPaths(String projectCode);
}
