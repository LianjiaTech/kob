package com.ke.kob.admin.core.service.impl;

import com.ke.kob.admin.core.mapper.ProjectUserMapper;
import com.ke.kob.admin.core.mapper.UserMapper;
import com.ke.kob.admin.core.model.db.ProjectUser;
import com.ke.kob.admin.core.model.db.User;
import com.ke.kob.admin.core.service.IndexService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设计权限相关的所有业务功能接口实现类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 上午11:15
 */
@Service("indexService")
public class IndexServiceImpl implements IndexService {

    @Value("${kob.cluster}")
    private String cluster;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ProjectUserMapper projectUserMapper;

    @Override
    public User selectUserByCodeAndPwd(String code, String pwd) {
        return userMapper.selectByCodeAndPwd(code, pwd, cluster);
    }

    @Override
    public void initProject(String userCode, String userName, String configuration, String projectCode, String projectName) {
        ProjectUser projectUser = new ProjectUser();
        projectUser.setUserCode(userCode);
        projectUser.setUserName(userName);
        projectUser.setProjectCode(projectCode);
        projectUser.setProjectName(projectName);
        projectUser.setConfiguration(configuration);
        projectUser.setProjectMode("service");
        projectUser.setOwner(true);
        projectUserMapper.insertOne(projectUser, cluster);
    }


    @Override
    public boolean existProject(String projectCode) {
        return projectUserMapper.selectCountByProjectCode(projectCode, cluster) != 0;
    }

    @Override
    public List<ProjectUser> selectProjectUserByUserCode(String code) {
        return projectUserMapper.selectByUserCode(code, cluster);
    }

    @Override
    public List<ProjectUser> selectProject() {
        return projectUserMapper.selectProjectIsOwner(cluster);
    }
}
