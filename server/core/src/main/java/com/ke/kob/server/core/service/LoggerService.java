package com.ke.kob.server.core.service;


import com.ke.kob.server.core.model.db.LogCollect;
import com.ke.kob.server.core.model.db.LogOpt;
import com.ke.kob.server.core.model.db.TaskRecord;

import java.util.List;
import java.util.Map;

/**
 * 日志service
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/24 下午9:15
 */
public interface LoggerService {

    /**
     * 查询任务记录数量
     *
     * @param param 查询参数
     * @return 查询总量
     */
    int selectTaskRecordCountByParam(Map<String, Object> param);

    /**
     * 查询任务记录
     *
     * @param param 查询参数
     * @param start 起始位置
     * @param limit 偏移量
     * @return 任务记录
     */
    List<TaskRecord> selectTaskRecordPageByParam(Map<String, Object> param, Integer start, Integer limit);

    /**
     * 查询日志收集数量
     *
     * @param projectCode 项目标识
     * @param taskUuid    任务唯一标识
     * @return 查询总量
     */
    int selectLogCollectCountByProjectCodeAndTaskUuid(String projectCode, String taskUuid);

    /**
     * 查询日志收集
     *
     * @param projectCode 项目标识
     * @param taskUuid    任务唯一标识
     * @param start       起始位置
     * @param limit       偏移量
     * @return 日志收集记录
     */
    List<LogCollect> selectLogCollectPageByProjectAndTaskUuid(String projectCode, String taskUuid, Integer start, Integer limit);

    /**
     * 保存操作日志
     *
     * @param logOpt 操作日志
     * @return 影响行数
     */
    int saveLogOpt(LogOpt logOpt);

    /**
     * 根据最小耗时查询操作日志数量
     *
     * @param costTime 花费时间
     * @return 查询数量
     */
    int selectLogOptCountByCostTime(Integer costTime);

    /**
     * 根据最小耗时查询操作日志
     *
     * @param costTime 花费时间
     * @param start    起始位置
     * @param limit    偏移量
     * @return 操作日志列表
     */
    List<LogOpt> selectLogOptPageByProject(Integer costTime, Integer start, Integer limit);
}
