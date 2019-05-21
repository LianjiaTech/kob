package com.ke.schedule.server.core.service.impl;

import com.ke.schedule.server.core.mapper.ProjectUserMapper;
import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuguang
 */
@Service
public @Slf4j class QWAlarmServiceImpl implements AlarmService {

    @Resource
    private ProjectUserMapper projectUserMapper;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;

    @Override
    public void send(TaskRecord record) {
        try {
            List<ProjectUser> projectUsers = projectUserMapper.selectByProjectCode(record.getProjectCode(), mp);
            List<ProjectUser> users = new ArrayList<>();
            projectUsers.forEach(e -> {
                e.getUserConfiguration().getSend().equals("1");
                users.add(e);
            });
            if(!CollectionUtils.isEmpty(users)){
                log.info("send:"+users.size()+",context:"+record.getProjectName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(TaskRecord record) {
        try {
            List<ProjectUser> projectUsers = projectUserMapper.selectByProjectCode(record.getProjectCode(), mp);
            List<ProjectUser> users = new ArrayList<>();
            projectUsers.forEach(e -> {
                e.getUserConfiguration().getRun().equals("1");
                users.add(e);
            });
            if(!CollectionUtils.isEmpty(users)){
                log.info("run:"+users.size()+",context:"+record.getProjectName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void end(TaskRecord record) {
        try {
            List<ProjectUser> projectUsers = projectUserMapper.selectByProjectCode(record.getProjectCode(), mp);
            List<ProjectUser> users = new ArrayList<>();
            projectUsers.forEach(e -> {
                e.getUserConfiguration().getEnd().equals("1");
                users.add(e);
            });
            if(!CollectionUtils.isEmpty(users)){
                log.info("end:"+users.size()+",context:"+record.getProjectName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
