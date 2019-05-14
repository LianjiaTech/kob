package com.ke.kob.server.core.service.impl;

import com.ke.kob.server.core.mapper.ProjectUserMapper;
import com.ke.kob.server.core.mapper.UserMapper;
import com.ke.kob.server.core.model.db.ProjectUser;
import com.ke.kob.server.core.model.db.User;
import com.ke.kob.server.core.service.ManagerService;
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
    @Value("${kob.cluster}")
    private String cluster;

    @Override
    public int selectProjectUserCountByProjectCode(String projectCode) {
        return projectUserMapper.selectCountByProjectCode(projectCode, cluster);
    }

    @Override
    public List<ProjectUser> selectProjectUserPageByProjectCode(String userCode, String projectCode, Integer start, Integer limit) {
        return projectUserMapper.selectPageByProjectCode(projectCode, start, limit, cluster);
    }

    @Override
    public User selectUserByUserCode(String userCode) {
        return userMapper.selectOneByCode(userCode, cluster);
    }

    @Override
    public int insertProjectUser(ProjectUser projectUser) {
        return projectUserMapper.insertOne(projectUser, cluster);
    }

    @Override
    public int deleteProjectUser(String projectCode, String id) {
        return projectUserMapper.delete(projectCode, id, cluster);
    }
}
