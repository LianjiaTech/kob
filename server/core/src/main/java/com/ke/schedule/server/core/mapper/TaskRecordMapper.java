package com.ke.schedule.server.core.mapper;

import com.ke.schedule.server.core.model.db.TaskRecord;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * mybatis mapper 操作数据库表_record_mapper 任务记录表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午10:45
 */
@Mapper
public interface TaskRecordMapper {

    String COLUMN = " id, project_code, project_name, job_uuid, job_type, job_cn, task_key, task_remark, " +
            "task_type, task_uuid, relation_task_uuid, load_balance, retry_type, rely, " +
            "ancestor, user_params, inner_params, msg, cron_expression, " +
            "timeout_threshold, state, complete, retry_count, batch_type, client_identification, " +
            "trigger_time, consumption_time, execute_start_time, execute_end_time, version, " +
            "gmt_created, gmt_modified ";

    String TABLE = " ${prefix}_task_record ";

    /**
     * 插入任务记录表
     *
     * @param taskRecord 任务记录对象
     * @param prefix    集群
     * @return 影响行数
     */
    @Insert("insert into " + TABLE +
            "(project_code, project_name, job_uuid, job_type, job_cn, task_key," +
            "task_remark, task_type, task_uuid, relation_task_uuid, load_balance, " +
            "retry_type, rely, ancestor, user_params, inner_params, msg, " +
            "cron_expression, timeout_threshold, complete, state, " +
            "retry_count, batch_type, client_identification, trigger_time) " +
            "values " +
            "(#{tr.projectCode}, #{tr.projectName}, #{tr.jobUuid}, #{tr.jobType}, #{tr.jobCn}, #{tr.taskKey}, " +
            "#{tr.taskRemark}, #{tr.taskType}, #{tr.taskUuid}, #{tr.relationTaskUuid}, #{tr.loadBalance}, " +
            "#{tr.retryType}, #{tr.rely}, #{tr.ancestor}, #{tr.userParams}, #{tr.innerParams}, #{tr.msg}, " +
            "#{tr.cronExpression}, #{tr.timeoutThreshold}, #{tr.complete}, #{tr.state}, " +
            "#{tr.retryCount}, #{tr.batchType}, #{tr.clientIdentification}, #{tr.triggerTime}) ")
    int insertOne(@Param("tr") TaskRecord taskRecord, @Param("prefix") String prefix);

    /**
     * 根据taskUuid 更新任务状态
     *
     * @param state    记录状态
     * @param taskUuid 任务唯一标识
     * @param prefix  集群
     * @return 影响行数
     */
    @Update("update " + TABLE +
            "set state = #{state}, complete = 1 " +
            "where task_uuid = #{taskUuid} ")
    int updateStateByTaskUuid(@Param("state") int state, @Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    /**
     * 根据任务唯一标识和集群，查询单条数据
     *
     * @param taskUuid 唯一标识
     * @param prefix  集群
     * @return 任务记录
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where task_uuid = #{taskUuid} ")
    TaskRecord findByTaskUuid(@Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    /**
     * 查询过期记录
     *
     * @param now     System.currentTimeMillis()
     * @param prefix 集群
     * @return 任务记录列表
     */
    @Select("select count(1) " +
            "from " + TABLE +
            "where complete = 0 and trigger_time+timeout_threshold*1000 < #{now} ")
    int selectCountExpireTaskRecord(@Param("now") long now, @Param("prefix") String prefix);

    /**
     * 查询过期记录
     *
     * @param now     System.currentTimeMillis()
     * @param start   起始行数
     * @param limit   数量
     * @param prefix 集群
     * @return 任务记录列表
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where complete = 0 and trigger_time+timeout_threshold*1000 < #{now} " +
            "order by id desc " +
            "limit ${start}, ${limit} ")
    List<TaskRecord> selectListExpireTaskRecord(@Param("now") long now, @Param("start") int start, @Param("limit") int limit, @Param("prefix") String prefix);


    /**
     * 更新完成状态和记录状态根据taskUuid
     *
     * @param param    参数
     * @param taskUuid 任务唯一标识
     * @param prefix  集群
     */
    @Update("<script>" +
            "   update " + TABLE +
            "   <set> " +
            "       <if test='param.complete != null'> complete = #{param.complete}, </if> " +
            "       <if test='param.state != null'> state = #{param.state}, </if> " +
            "       <if test='param.clientIdentification != null'> client_identification = #{param.clientIdentification}, </if> " +
            "       <if test='param.consumptionTime != null'> consumption_time = #{param.consumptionTime}, </if> " +
            "       <if test='param.executeStartTime != null'> execute_start_time = #{param.executeStartTime}, </if> " +
            "       <if test='param.executeEndTime != null'> execute_end_time = #{param.executeEndTime}, </if> " +
            "       <if test='param.innerParams != null'> inner_params = #{param.innerParams}, </if> " +
            "       <if test='param.msg != null'> msg = #{param.msg}, </if> " +
            "   </set> " +
            "   where task_uuid = #{taskUuid} " +
            "</script>")
    void updateByTaskUuid(@Param("param") Map<String, Object> param, @Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    /**
     * 任务记录数量
     *
     * @param param   查询参数
     * @param prefix 集群
     * @return 任务记录列表
     */
    @Select("<script>" +
            "   select count(1) " +
            "   from " + TABLE +
            "   where 1 = 1 " +
            "       <if test='param.projectCode != null'> and project_code = #{param.projectCode} </if> " +
            "       <if test='param.triggerTimeStart != null'> and trigger_time &gt;= #{param.triggerTimeStart} </if> " +
            "       <if test='param.triggerTimeEnd != null'> and trigger_time &lt;= #{param.triggerTimeEnd} </if> " +
            "       <if test='param.jobUuid != null'> and job_uuid = #{param.jobUuid} </if> " +
            "</script>")
    int selectCountByParam(@Param("param") Map<String, Object> param, @Param("prefix") String prefix);

    /**
     * 分页查询任务记录
     *
     * @param param   查询参数
     * @param start   起始行数
     * @param limit   数量
     * @param prefix 集群
     * @return 返回查询数量行数
     */
    @Select("<script>" +
            "   select " + COLUMN +
            "   from " + TABLE +
            "   where project_code = #{param.projectCode} " +
            "       <if test='param.triggerTimeStart != null'> and trigger_time &gt;= #{param.triggerTimeStart} </if> " +
            "       <if test='param.triggerTimeEnd != null'> and trigger_time &lt;= #{param.triggerTimeEnd} </if> " +
            "       <if test='param.jobUuid != null'> and job_uuid = #{param.jobUuid} </if> " +
            "   order by id desc " +
            "   limit ${start},${limit} " +
            "</script>")
    List<TaskRecord> selectPageByParam(@Param("param") Map<String, Object> param,
                                       @Param("start") Integer start,
                                       @Param("limit") Integer limit,
                                       @Param("prefix") String prefix);

    /**
     * 根据作业JobUuid查询最后最后执行任务用于作业是否依赖上一周期场景
     *
     * @param jobUuid 作业标识
     * @param prefix 集群
     * @return 任务记录
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where  job_uuid = #{jobUuid} and ancestor = 1 and state != '" + TaskRecordStateConstant.RELY_UNDO + "' " +
            "order by id desc " +
            "limit 1")
    TaskRecord selectLastTaskByJobUuid(@Param("jobUuid") String jobUuid, @Param("prefix") String prefix);
}
