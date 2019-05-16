package com.ke.schedule.server.core.service.impl;

import com.ke.schedule.server.core.mapper.LogCollectMapper;
import com.ke.schedule.server.core.mapper.TaskRecordMapper;
import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.server.core.service.LoggerService;
import com.ke.schedule.server.core.mapper.LogOptMapper;
import com.ke.schedule.server.core.model.db.LogCollect;
import com.ke.schedule.server.core.model.db.LogOpt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 日志service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/24 下午9:15
 */
@Service("loggerService")
public class LoggerServiceImpl implements LoggerService {

    @Resource
    private TaskRecordMapper recordMapper;
    @Resource
    private LogCollectMapper logCollectMapper;
    @Resource
    private LogOptMapper logOptMapper;
    @Value("${kob-schedule.mysql-prefix}")
    private String mp;


    @Override
    public int selectTaskRecordCountByParam(Map<String, Object> param) {
        return recordMapper.selectCountByParam(param, mp);
    }

    @Override
    public List<TaskRecord> selectTaskRecordPageByParam(Map<String, Object> param, Integer start, Integer limit) {
        return recordMapper.selectPageByParam(param, start, limit, mp);
    }

    @Override
    public int selectLogCollectCountByProjectCodeAndTaskUuid(String projectCode, String taskUuid) {
        return logCollectMapper.selectCountByProjectCode(projectCode, taskUuid, mp);
    }

    @Override
    public List<LogCollect> selectLogCollectPageByProjectAndTaskUuid(String projectCode, String taskUuid, Integer start, Integer limit) {
        return logCollectMapper.selectPageByProjectCodeAndTaskUuid(projectCode, taskUuid, start, limit, mp);
    }

    @Override
    public int saveLogOpt(LogOpt logOpt) {
        return logOptMapper.insertOne(logOpt, mp);
    }

    @Override
    public int selectLogOptCountByCostTime(Integer costTime) {
        return logOptMapper.selectCountByCostTime(costTime, mp);
    }

    @Override
    public List<LogOpt> selectLogOptPageByProject(Integer costTime, Integer start, Integer limit) {
        return logOptMapper.selectPageByCostTime(costTime, start, limit, mp);
    }
}
