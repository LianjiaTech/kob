package com.ke.kob.server.core.service.impl;

import com.ke.kob.server.core.mapper.LogCollectMapper;
import com.ke.kob.server.core.mapper.LogOptMapper;
import com.ke.kob.server.core.mapper.TaskRecordMapper;
import com.ke.kob.server.core.model.db.LogCollect;
import com.ke.kob.server.core.model.db.LogOpt;
import com.ke.kob.server.core.model.db.TaskRecord;
import com.ke.kob.server.core.service.LoggerService;
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
    @Value("${kob.cluster}")
    private String cluster;


    @Override
    public int selectTaskRecordCountByParam(Map<String, Object> param) {
        return recordMapper.selectCountByParam(param, cluster);
    }

    @Override
    public List<TaskRecord> selectTaskRecordPageByParam(Map<String, Object> param, Integer start, Integer limit) {
        return recordMapper.selectPageByParam(param, start, limit, cluster);
    }

    @Override
    public int selectLogCollectCountByProjectCodeAndTaskUuid(String projectCode, String taskUuid) {
        return logCollectMapper.selectCountByProjectCode(projectCode, taskUuid, cluster);
    }

    @Override
    public List<LogCollect> selectLogCollectPageByProjectAndTaskUuid(String projectCode, String taskUuid, Integer start, Integer limit) {
        return logCollectMapper.selectPageByProjectCodeAndTaskUuid(projectCode, taskUuid, start, limit, cluster);
    }

    @Override
    public int saveLogOpt(LogOpt logOpt) {
        return logOptMapper.insertOne(logOpt, cluster);
    }

    @Override
    public int selectLogOptCountByCostTime(Integer costTime) {
        return logOptMapper.selectCountByCostTime(costTime, cluster);
    }

    @Override
    public List<LogOpt> selectLogOptPageByProject(Integer costTime, Integer start, Integer limit) {
        return logOptMapper.selectPageByCostTime(costTime, start, limit, cluster);
    }
}
