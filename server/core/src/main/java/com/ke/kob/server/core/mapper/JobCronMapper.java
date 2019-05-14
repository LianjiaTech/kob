package com.ke.kob.server.core.mapper;

import com.ke.kob.server.core.model.db.JobCron;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * mybatis mapper 操作数据库表kob_job_cron_ cron作业表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午3:22
 */
@Mapper
public interface JobCronMapper {

    String COLUMN = " id, project_code, project_name, job_uuid, job_type, job_cn, task_key, task_remark, task_type, load_balance, " +
            "suspend, batch_type, retry_type, rely, user_params, inner_params, cron_expression, last_generate_trigger_time, " +
            "timeout_threshold, retry_count, failover, gmt_created, gmt_modified ";

    String TABLE = " kob_job_cron_${cluster} ";

    /**
     * 单条保存cron类型作业
     *
     * @param jobCron cron类型作业
     * @param cluster 集群名称
     * @return 影响结果集条数
     */
    @Insert("insert into " + TABLE +
            "(project_code, project_name, job_uuid, job_type, job_cn, task_key, " +
            "task_remark, task_type, load_balance, suspend, batch_type, retry_type, " +
            "rely, user_params, inner_params, cron_expression, timeout_threshold, " +
            "retry_count, failover) " +
            "values " +
            "(#{jc.projectCode}, #{jc.projectName}, #{jc.jobUuid}, #{jc.jobType},  #{jc.jobCn}, #{jc.taskKey}," +
            "#{jc.taskRemark}, #{jc.taskType}, #{jc.loadBalance}, #{jc.suspend}, #{jc.batchType}, #{jc.retryType}," +
            "#{jc.rely}, #{jc.userParams}, #{jc.innerParams}, #{jc.cronExpression}, #{jc.timeoutThreshold}," +
            "#{jc.retryCount}, #{jc.failover}) ")
    int insertOne(@Param("jc") JobCron jobCron, @Param("cluster") String cluster);

    /**
     * 更新cron作业的最后生成触发时间
     *
     * @param jobUuid                 作业唯一标识
     * @param cronExpression          cron表达式
     * @param lastGenerateTriggerTime 更新前的最后触发时间
     * @param timeAfter               要更新的最后触发时间
     * @param cluster                 集群名称
     * @return 影响结果集条数
     */
    @Update("<script>" +
            "   update " + TABLE +
            "   set last_generate_trigger_time = #{timeAfter} " +
            "   where job_uuid = #{jobUuid} and cron_expression = #{cronExpression} " +
            "   <choose>" +
            "      <when test='lastGenerateTriggerTime != null'> " +
            "          and last_generate_trigger_time = #{lastGenerateTriggerTime} " +
            "       </when> " +
            "       <otherwise> " +
            "           and last_generate_trigger_time is null " +
            "       </otherwise> " +
            "   </choose>" +
            "</script>")
    int updateRunningJobCronLastGenerateTriggerTime(@Param("jobUuid") String jobUuid,
                                                    @Param("cronExpression") String cronExpression,
                                                    @Param("lastGenerateTriggerTime") Long lastGenerateTriggerTime,
                                                    @Param("timeAfter") Long timeAfter,
                                                    @Param("cluster") String cluster);

    /**
     * 根据状态查询cron类型作业
     *
     * @param suspend 暂停状态 暂停true|运行false
     * @param cluster 集群名称
     * @return
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where suspend = #{suspend} ")
    List<JobCron> findCronJobBySuspend(@Param("suspend") boolean suspend, @Param("cluster") String cluster);

    /**
     * 通过projectCode 查询cron作业数量根据项目标识
     *
     * @param projectCode
     * @param cluster
     * @return
     */
    @Select("select count(1) " +
            "from " + TABLE +
            "where project_code = #{projectCode} ")
    int selectCountByProjectCode(@Param("projectCode") String projectCode, @Param("cluster") String cluster);

    /**
     * 通过projectCode 分页查询cron类型作业信息
     *
     * @param projectCode
     * @param start
     * @param limit
     * @param cluster
     * @return
     */
    @Select("select " + COLUMN +
            "from  " + TABLE +
            "where project_code = #{projectCode} " +
            "order by suspend asc, id desc " +
            "limit ${start},${limit} ")
    List<JobCron> selectPageJobCronByProject(@Param("projectCode") String projectCode,
                                             @Param("start") Integer start,
                                             @Param("limit") Integer limit,
                                             @Param("cluster") String cluster);

    /**
     * 修改suspend状态
     *
     * @param exclamationSuspend
     * @param jobUuid
     * @param projectCode
     * @param suspend
     * @param cluster
     * @return
     */
    @Update("update " + TABLE +
            "set suspend = #{exclamationSuspend}, last_generate_trigger_time = null " +
            "where job_uuid = #{jobUuid} and project_code = #{projectCode} and suspend = #{suspend} ")
    int updateSuspend(@Param("exclamationSuspend") Boolean exclamationSuspend,
                      @Param("jobUuid") String jobUuid,
                      @Param("projectCode") String projectCode,
                      @Param("suspend") Boolean suspend,
                      @Param("cluster") String cluster);

    /**
     * 删除任务
     *
     * @param jobUuid
     * @param projectCode
     * @param cluster
     */
    @Delete("delete from " + TABLE +
            "where job_uuid = #{jobUuid} " +
            "and project_code = #{projectCode} ")
    void deleteByJobUuidAndProjectCode(@Param("jobUuid") String jobUuid,
                                       @Param("projectCode") String projectCode,
                                       @Param("cluster") String cluster);

    /**
     * 更新cron作业
     *
     * @param taskRemark
     * @param cronExpression
     * @param userParams
     * @param jobUuid
     * @param projectCode
     * @param cluster
     * @return
     */
    @Update("update " + TABLE +
            "set task_remark = #{taskRemark}, cron_expression = #{cronExpression}, user_params = #{userParams}, " +
            "last_generate_trigger_time = null " +
            "where job_uuid = #{jobUuid} and project_code = #{projectCode} ")
    int updateOne(@Param("taskRemark") String taskRemark,
                  @Param("cronExpression") String cronExpression,
                  @Param("userParams") String userParams,
                  @Param("jobUuid") String jobUuid,
                  @Param("projectCode") String projectCode,
                  @Param("cluster") String cluster);
}
