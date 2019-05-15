package com.ke.schedule.server.console.controller;

import com.ke.schedule.server.core.common.Attribute;
import com.ke.schedule.server.core.common.FtlPath;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.oz.ClientInfo;
import com.ke.schedule.server.core.model.oz.NodeServer;
import com.ke.schedule.server.core.model.oz.ResponseData;
import com.ke.schedule.server.core.service.NodeService;
import com.ke.schedule.basic.support.KobUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 涉及节点信息相关的API入口
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/6 下午9:17
 */
@RequestMapping("/node")
@Controller
public class NodeController {

    @Resource(name = "nodeService")
    private NodeService nodeService;

    /**
     * view入口
     *
     * @param model ui.Model
     * @return 跳转项目接入页面 FtlPath.CLIENT_NODE
     */
    @RequestMapping(value = "/server_node.htm")
    public String serverNode(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, "./node/server_node.ftl");
        return FtlPath.INDEX_PATH;
    }

    /**
     * view入口
     *
     * @param model ui.Model
     * @return 跳转项目接入页面 FtlPath.CLIENT_NODE
     */
    @RequestMapping(value = "/client_node.htm")
    public String clientNode(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.CLIENT_NODE);
        return FtlPath.INDEX_PATH;
    }


    /**
     * client view查询节点信息，分页查询
     *
     * @return Json格式的节点信息，格式为ResponseData
     */
    @RequestMapping(value = "/client_node_list.json")
    @ResponseBody
    public ResponseData clientNodeList() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        Map<String, ClientInfo> projectClientInfo = nodeService.getClientNodes(projectUser.getProjectCode());
        if (KobUtils.isEmpty(projectClientInfo)) {
            return ResponseData.success(0);
        } else {
            return ResponseData.success(projectClientInfo.values());
        }
    }

    /**
     * server view查询节点信息，分页查询
     *
     * @return Json格式的节点信息，格式为ResponseData
     */
    @RequestMapping(value = "/server_node_list.json")
    @ResponseBody
    public ResponseData serverNodeList() throws Exception {
        List<NodeServer> nodeServerList = nodeService.getNodeServerList();
        return ResponseData.success(nodeServerList);
    }
}
