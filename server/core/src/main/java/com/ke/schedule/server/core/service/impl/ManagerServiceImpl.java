package com.ke.schedule.server.core.service.impl;

import com.ke.schedule.server.core.mapper.ProjectUserMapper;
import com.ke.schedule.server.core.mapper.UserMapper;
import com.ke.schedule.server.core.service.ManagerService;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目管理service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/5 上午10:26
 */
@Service("managerService")
public class ManagerServiceImpl implements ManagerService {

    @Resource
    private ProjectUserMapper projectUserMapper;
    @Resource
    private UserMapper userMapper;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    @Override
    public int selectProjectUserCountByProjectCode(String projectCode) {
        return projectUserMapper.selectCountByProjectCode(projectCode, mp);
    }

    @Override
    public List<ProjectUser> selectProjectUserPageByProjectCode(String userCode, String projectCode, Integer start, Integer limit) {
        return projectUserMapper.selectPageByProjectCode(projectCode, start, limit, mp);
    }

    @Override
    public User selectUserByUserCode(String userCode) {
        return userMapper.selectOneByCode(userCode, mp);
    }

    @Override
    public int insertProjectUser(ProjectUser projectUser) {
        return projectUserMapper.insertOne(projectUser, mp);
    }

    @Override
    public int deleteProjectUser(String projectCode, String id) {
        return projectUserMapper.delete(projectCode, id, mp);
    }
}
