package com.ke.schedule.server.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.server.core.common.Attribute;
import com.ke.schedule.server.core.common.FtlPath;
import com.ke.schedule.server.core.mapper.ProjectUserMapper;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.User;
import com.ke.schedule.server.core.model.oz.ResponseData;
import com.ke.schedule.server.core.model.oz.UserConfiguration;
import com.ke.schedule.server.core.service.IndexService;
import com.ke.schedule.server.core.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 涉及项目管理的api接口
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/27 下午2:21
 */
@RequestMapping("/manager")
@Controller
public @Slf4j
class ManagerController {

    @Resource
    private CuratorFramework curator;
    @Resource
    private IndexService indexService;
    @Resource(name = "managerService")
    private ManagerService managerService;
    @Resource
    private ProjectUserMapper projectUserMapper;
    @Value("${kob-schedule.zk-prefix}")
    private String zp;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    /**
     * 项目接入 view入口
     *
     * @param model ui.Model
     * @return FtlPath.PROJECT_ACCESS_PATH
     */
    @RequestMapping(value = "/project_access.htm")
    public String projectAccess(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.PROJECT_ACCESS_PATH);
        return FtlPath.INDEX_PATH;
    }

    /**
     * 保存项目接入信息
     *
     * @return ResponseData
     */
    @RequestMapping(value = "/save_project_access.json")
    @ResponseBody
    public ResponseData saveProjectAccess() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Attribute.SESSION_USER);
        String projectCode = request.getParameter("project_code");
        String regex = "([A-Z]|[a-z]|_){6,60}";
        if (KobUtils.isEmpty(projectCode) || !projectCode.matches(regex)) {
            return ResponseData.error("项目标识有误");
        }
        String projectName = request.getParameter("project_name");
        if (KobUtils.isEmpty(projectName) || projectName.length() > 60) {
            return ResponseData.error("项目名称有误");
        }
        boolean zkExist = curator.checkExists().forPath(ZkPathConstant.clientNodePath(zp, projectCode)) != null;
        boolean dbExist = indexService.existProject(projectCode);
        if (zkExist || dbExist) {
            return ResponseData.error("项目已存在");
        }
        indexService.initProject(user.getCode(), user.getName(), user.getConfiguration(), projectCode, projectName);
        try {
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZkPathConstant.clientTaskPath(zp, projectCode));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZkPathConstant.clientNodePath(zp, projectCode));
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResponseData.success();
    }

    /**
     * 人员管理 view入口
     *
     * @param model ui.Model
     * @return FtlPath.PROJECT_USER_PATH
     */
    @RequestMapping(value = "/project_user.htm")
    public String projectUser(Model model) {
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.PROJECT_USER_PATH);
        return FtlPath.INDEX_PATH;
    }

    /**
     * 项目人员列表
     *
     * @param start
     * @param limit
     * @return
     */
    @RequestMapping(value = "/project_user_list.json")
    @ResponseBody
    public ResponseData projectUserList(@RequestParam("start") Integer start, @RequestParam("limit") Integer limit) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        int count = managerService.selectProjectUserCountByProjectCode(projectUser.getProjectCode());
        if (count == 0) {
            return ResponseData.success(0);
        }
        User user = (User) request.getSession().getAttribute(Attribute.SESSION_USER);
        List<ProjectUser> projectUserList = managerService.selectProjectUserPageByProjectCode(user.getCode(), projectUser.getProjectCode(), start, limit);
        return ResponseData.success(count, projectUserList);
    }

    /**
     * 邀请人员保存
     *
     * @return
     */
    @RequestMapping(value = "/project_user_invite.json")
    @ResponseBody
    public ResponseData projectUserInvite() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userCode = request.getParameter("user_code");
        if (KobUtils.isEmpty(userCode)) {
            return ResponseData.error("用户标识不能为空");
        }
        User user = managerService.selectUserByUserCode(userCode);
        if (user == null) {
            return ResponseData.error("用户库无此用户标识");
        }
        ProjectUser projectUserInSession = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        ProjectUser projectUser = new ProjectUser();
        projectUser.setUserCode(userCode);
        projectUser.setUserName(user.getName());
        projectUser.setProjectCode(projectUserInSession.getProjectCode());
        projectUser.setProjectName(projectUserInSession.getProjectName());
        projectUser.setProjectMode(projectUserInSession.getProjectMode());
        projectUser.setOwner(false);
        projectUser.setConfiguration(projectUserInSession.getConfiguration());
        managerService.insertProjectUser(projectUser);
        return ResponseData.success();
    }

    /**
     * 邀请人员保存
     *
     * @return
     */
    @RequestMapping(value = "/project_user_delete.json")
    @ResponseBody
    public ResponseData projectUserDelete() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String id = request.getParameter("id");
        if (KobUtils.isEmpty(id)) {
            return ResponseData.error("入参非法");
        }
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        managerService.deleteProjectUser(projectUser.getProjectCode(), id);
        return ResponseData.success();
    }

    @RequestMapping(value = "/person_config.htm")
    public String personConfig(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        model.addAttribute("config", projectUser);
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.PERSON_CONFIG_PATH);
        return FtlPath.INDEX_PATH;
    }

    @RequestMapping(value = "/person_config_save.json")
    @ResponseBody
    public ResponseData personConfigSave() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String send = request.getParameter("send");
        String run = request.getParameter("run");
        String end = request.getParameter("end");
        ProjectUser projectUser = (ProjectUser) request.getSession().getAttribute(Attribute.PROJECT_SELECTED);
        UserConfiguration userConfiguration = projectUser.getUserConfiguration();
        userConfiguration.setSend(send);
        userConfiguration.setRun(run);
        userConfiguration.setEnd(end);
        projectUser.setConfiguration(JSONObject.toJSONString(userConfiguration));
        projectUserMapper.updateConfiguration(projectUser, mp);
        request.getSession().setAttribute(Attribute.PROJECT_SELECTED, projectUser);
        return ResponseData.success();
    }
}
