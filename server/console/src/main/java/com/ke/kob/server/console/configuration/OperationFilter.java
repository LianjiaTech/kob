package com.ke.kob.server.console.configuration;

import com.alibaba.fastjson.JSONObject;
import com.ke.kob.server.core.common.Attribute;
import com.ke.kob.server.core.model.db.LogOpt;
import com.ke.kob.server.core.model.db.User;
import com.ke.kob.server.core.service.LoggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/13 下午1:32
 */
@WebFilter(filterName = "operationFilter", urlPatterns = "/*")
public @Slf4j class OperationFilter implements Filter {

    private LoggerService loggerService;
    private AntPathMatcher matcher = new AntPathMatcher();
    /**
     * 注意url 匹配方式
     */
    private String[] excludedURL = {"/static/**/*.*", "/**/login.json", "/**/index.htm", "/**/favicon.ico", "/collect/**"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        loggerService = (LoggerService) ctx.getBean("loggerService");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isExclude(((HttpServletRequest) request).getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute(Attribute.SESSION_USER);
        if (user == null) {
            ((HttpServletResponse) response).sendRedirect("/index.htm");
            return;
        }
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long end = System.currentTimeMillis();
        LogOpt logOpt = new LogOpt();
        logOpt.setUserCode(user.getCode());
        logOpt.setUserName(user.getName());
        logOpt.setOptUrl(((HttpServletRequest) request).getServletPath());
        logOpt.setRequest(JSONObject.toJSONString(request.getParameterMap()));
        logOpt.setCostTime(end - start);
        loggerService.saveLogOpt(logOpt);
    }

    @Override
    public void destroy() {

    }

    private boolean isExclude(String path) {
        for (String url : excludedURL) {
            if (matcher.match(url, path)) {
                return true;
            }
        }
        return false;
    }
}
