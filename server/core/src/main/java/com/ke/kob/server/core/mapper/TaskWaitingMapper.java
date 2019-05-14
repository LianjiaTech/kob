package com.ke.kob.server.core.mapper;

import com.ke.kob.server.core.model.db.TaskWaiting;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * mybatis mapper 操作数据库表kob_task_waiting_$cluster 等待执行表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 上午11:52
 */
@Mapper
public interface TaskWaitingMapper {

    String column = " id, project_code, project_name, job_uuid, job_type, job_cn, task_key, task_remark, task_type, " +
            "task_uuid, load_balance, batch_type, retry_type, rely, user_params, inner_params, cron_expression, " +
            "timeout_threshold, retry_count, failover, trigger_time, gmt_created, gmt_modified ";

    String table = " kob_task_waiting_${cluster} ";

    /**
     * 插入等待执行任务表
     *
     * @param tw
     * @param cluster
     * @return
     */
    @Insert("<script>" +
            "   insert into " + table +
            "   (project_code, project_name, job_type, task_type, job_uuid, job_cn," +
            "   task_uuid, relation_task_uuid, task_key, task_remark, cron_expression, " +
            "   load_balance, retry_type, batch_type, rely, retry_count, failover, " +
            "   timeout_threshold, user_params, inner_params, trigger_time) " +
            "   values " +
            "   (#{tw.projectCode}, #{tw.projectName}, #{tw.jobType}, #{tw.taskType}, #{tw.jobUuid}, #{tw.jobCn}, " +
            "   #{tw.taskUuid}, #{tw.relationTaskUuid}, #{tw.taskKey}, #{tw.taskRemark}, #{tw.cronExpression}, " +
            "   #{tw.loadBalance}, #{tw.retryType}, #{tw.batchType}, #{tw.rely}, #{tw.retryCount}, #{tw.failover}, " +
            "   #{tw.timeoutThreshold}, #{tw.userParams}, #{tw.innerParams}, #{tw.triggerTime}) " +
            "</script>")
    int insertOne(@Param("tw") TaskWaiting tw, @Param("cluster") String cluster);

    /**
     * 查询可以出发的任务，限定条数
     *
     * @param cluster     集群名称
     * @param triggerTime 触发时间
     * @param limit       限定条数
     * @return 可以出发任务list
     */
    @Select("select " + column +
            "from " + table +
            "where trigger_time <= #{triggerTime} " +
            "limit ${limit} ")
    List<TaskWaiting> findTriggerTaskInLimit(@Param("triggerTime") long triggerTime,
                                             @Param("limit") int limit,
                                             @Param("cluster") String cluster);

    /**
     * 批量插入等待执行任务表
     *
     * @param taskWaitingList 待推送作业list
     * @param cluster         集群名称
     * @return 插入条数
     */
    @Insert("<script>" +
            "   insert into " + table +
            "   (project_code, project_name, job_type, task_type, job_uuid, job_cn," +
            "   task_uuid, relation_task_uuid, task_key, task_remark, cron_expression, " +
            "   load_balance, retry_type, batch_type, rely, retry_count, failover, " +
            "   timeout_threshold, user_params, inner_params, trigger_time)" +
            "   values" +
            "   <foreach collection='tws' item='tw' separator=','>" +
            "      (#{tw.projectCode}, #{tw.projectName}, #{tw.jobType}, #{tw.taskType}, #{tw.jobUuid}, #{tw.jobCn}," +
            "      #{tw.taskUuid}, #{tw.relationTaskUuid}, #{tw.taskKey}, #{tw.taskRemark}, #{tw.cronExpression}, " +
            "      #{tw.loadBalance}, #{tw.retryType}, #{tw.batchType}, #{tw.rely}, #{tw.retryCount},  #{tw.failover}, " +
            "      #{tw.timeoutThreshold}, #{tw.userParams}, #{tw.innerParams}, #{tw.triggerTime}) " +
            "   </foreach>" +
            "</script>")
    int insertBatch(@Param("tws") List<TaskWaiting> taskWaitingList, @Param("cluster") String cluster);

    /**
     * 删除等待执行任务
     *
     * @param taskUuid
     * @param cluster
     * @return
     */
    @Delete("delete from " + table + " where task_uuid = #{taskUuid} ")
    int deleteOne(@Param("taskUuid") String taskUuid, @Param("cluster") String cluster);

    @Delete("delete from " + table +
            "where job_uuid = #{jobUuid} " +
            "and trigger_time &lt;= #{lastGenerateTriggerTime} ")
    int deleteExpiredTask(@Param("jobUuid") String jobUuid,
                          @Param("lastGenerateTriggerTime") Long lastGenerateTriggerTime,
                          @Param("cluster") String cluster);

    /**
     * 通过projectCode 查询等待推送任务数量根据项目标识
     *
     * @param projectCode
     * @param cluster
     * @return
     */
    @Select("select count(1) " +
            "from " + table +
            "where project_code = #{projectCode} ")
    int selectCountByProjectCode(@Param("projectCode") String projectCode, @Param("cluster") String cluster);

    /**
     * 通过projectCode 分页查询等待执行任务信息
     *
     * @param projectCode
     * @param start
     * @param limit
     * @param cluster
     * @return
     */
    @Select("select " + column +
            "from " + table +
            "where project_code = #{projectCode} " +
            "order by trigger_time asc " +
            "limit ${start},${limit} ")
    List<TaskWaiting> selectPageByProjectCode(@Param("projectCode") String projectCode,
                                              @Param("start") Integer start,
                                              @Param("limit") Integer limit,
                                              @Param("cluster") String cluster);

    /**
     * 删除未来等待推送的作业 通过JobUuid和projectCode
     *
     * @param jobUuid
     * @param projectCode
     * @param cluster
     * @return
     */
    @Delete("delete from " + table +
            "where job_uuid = #{jobUuid} and project_code = #{projectCode} ")
    int deleteByJobUuidAndProjectCode(@Param("jobUuid") String jobUuid,
                                      @Param("projectCode") String projectCode,
                                      @Param("cluster") String cluster);

    /**
     * 触发等待执行的任务
     *
     * @param triggerTime
     * @param taskUuid
     * @param projectCode
     * @param cluster
     * @return
     */
    @Update("update " + table +
            "set trigger_time = #{triggerTime} " +
            "where task_uuid = #{taskUuid} and project_code = #{projectCode}")
    int triggerTaskWaiting(@Param("triggerTime") Long triggerTime,
                           @Param("taskUuid") String taskUuid,
                           @Param("projectCode") String projectCode,
                           @Param("cluster") String cluster);

    /**
     * 删除等待推送任务
     *
     * @param taskUuid
     * @param projectCode
     * @param cluster
     * @return
     */
    @Delete("delete from " + table +
            "where task_uuid = #{taskUuid} and project_code = #{projectCode}")
    int deleteByTaskUuidAndProjectCode(@Param("taskUuid") String taskUuid,
                                       @Param("projectCode") String projectCode,
                                       @Param("cluster") String cluster);
}
