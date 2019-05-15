package com.ke.schedule.server.console.controller;

import com.ke.schedule.server.core.common.Attribute;
import com.ke.schedule.server.core.common.FtlPath;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.User;
import com.ke.schedule.server.core.model.oz.ResponseData;
import com.ke.schedule.server.core.service.IndexService;
import com.ke.schedule.server.core.service.LoggerService;
import com.ke.schedule.basic.support.KobUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 涉及登陆、首页相关api请求入口
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/25 下午8:24
 */
@Controller
public class IndexController {

    @Resource(name = "indexService")
    private IndexService indexService;
    @Resource(name = "loggerService")
    private LoggerService loggerService;

    /**
     * 登陆后默认path，根据session中的user属性判断当前用户是否有登录态，若接公司的统一登录请针对性的修改。
     *
     * @param model model
     * @return 校验通过返回 FtlPath.path
     */
    @RequestMapping(value = {"/", "/index.htm"})
    public String index(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Attribute.SESSION_USER);
        if (user == null) {
            return FtlPath.LOGIN_PATH;
        }
        return welcome(model);
    }

    /**
     * 登陆页面post方法，官方版本会校验 code、pwd，若接公司的统一登录请针对性的修改
     */
    @RequestMapping(value = {"/login.json"})
    @ResponseBody
    public ResponseData login() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String code = request.getParameter("code");
        String pwd = request.getParameter("pwd");
        if (StringUtils.isEmpty(code)) {
            return ResponseData.error("用户标识不能为空");
        }
        if (StringUtils.isEmpty(pwd)) {
            return ResponseData.error("密码标识不能为空");
        }
        User user = indexService.selectUserByCodeAndPwd(code, pwd);
        if (user == null) {
            return ResponseData.error("用户库未匹配到用户");
        }
        request.getSession().setAttribute(Attribute.SESSION_USER, user);
        List<ProjectUser> projectUserList = indexService.selectProjectUserByUserCode(user.getCode());
        request.getSession().setAttribute(Attribute.PROJECT_LIST, projectUserList);
        if (!KobUtils.isEmpty(projectUserList)) {
            request.getSession().setAttribute(Attribute.PROJECT_SELECTED, projectUserList.get(0));
        }
        return ResponseData.success();
    }

    @RequestMapping(value = {"/logout.htm"})
    public String logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().removeAttribute(Attribute.SESSION_USER);
        return FtlPath.LOGIN_PATH;
    }

    @RequestMapping(value = {"/change_project.htm"})
    public String changeProject(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Attribute.SESSION_USER);
        List<ProjectUser> projectUserList = indexService.selectProjectUserByUserCode(user.getCode());
        request.getSession().setAttribute(Attribute.PROJECT_LIST, projectUserList);
        String projectCode = request.getParameter("project_code");
        if (!KobUtils.isEmpty(projectUserList)) {
            for (ProjectUser projectUser : projectUserList) {
                if (projectUser.getProjectCode().equals(projectCode)) {
                    request.getSession().setAttribute(Attribute.PROJECT_SELECTED, projectUser);
                    return welcome(model);
                }
            }
        }
        return welcome(model);
    }

    /**
     * 默认欢迎页私有方法
     *
     * @param model
     * @return
     */
    private String welcome(Model model) {
        List<ProjectUser> projectUsers = indexService.selectProject();
        Map<String, Object> param = new HashMap<>(10);
        Date initDateByDay = KobUtils.initDateByDay();
        param.put("triggerTimeStart", KobUtils.addHour(initDateByDay, -24).getTime());
        param.put("triggerTimeEnd", initDateByDay.getTime());
        Integer tomorrowRecordCount = loggerService.selectTaskRecordCountByParam(param);
        model.addAttribute("tomorrow_record_count", tomorrowRecordCount);
        model.addAttribute("project", projectUsers);
        model.addAttribute(Attribute.INDEX_SCREEN, FtlPath.INDEX_WELCOME_PATH);
        return FtlPath.INDEX_PATH;
    }
}
