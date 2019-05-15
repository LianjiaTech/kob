package com.ke.schedule.server.core.service;


import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.User;

import java.util.List;

/**
 * 涉及权限相关的所有业务功能接口
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 上午11:15
 */
public interface IndexService {

    /**
     * 根据 name & pwd 查询用户
     *
     * @param name 入参 code 对于数据库表 kob_admin code
     * @param pwd  入参 pwd 对于数据库表 kob_admin pwd
     * @return 存在用户返回 true
     */
    User selectUserByCodeAndPwd(String name, String pwd);

    /**
     * 初始化项目信息
     *
     * @param userCode      用户账号
     * @param userName      用户姓名
     * @param configuration 用户配置
     * @param projectCode   项目标识
     * @param projectName   项目名称
     */
    void initProject(String userName, String userCode, String configuration, String projectCode, String projectName);

    /**
     * 项目是否存在
     *
     * @param projectCode 项目标识
     * @return 项目是否存在
     */
    boolean existProject(String projectCode);

    /**
     * 获取当前用户所有项目
     *
     * @param code 用户标识
     * @return 项目列表
     */
    List<ProjectUser> selectProjectUserByUserCode(String code);

    /**
     * 获取所有项目
     *
     * @return 项目列表
     */
    List<ProjectUser> selectProject();
}
