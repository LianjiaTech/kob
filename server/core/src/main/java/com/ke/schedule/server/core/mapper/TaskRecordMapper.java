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

    @Update("update " + TABLE +
            "set state = #{state}, complete = 1 " +
            "where task_uuid = #{taskUuid} ")
    int updateStateByTaskUuid(@Param("state") int state, @Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    @Select("select " + COLUMN +
            "from " + TABLE +
            "where task_uuid = #{taskUuid} ")
    TaskRecord findByTaskUuid(@Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    @Select("select count(1) " +
            "from " + TABLE +
            "where complete = 0 and trigger_time+timeout_threshold*1000 < #{now} ")
    int selectCountExpireTaskRecord(@Param("now") long now, @Param("prefix") String prefix);

    @Select("select " + COLUMN +
            "from " + TABLE +
            "where complete = 0 and trigger_time+timeout_threshold*1000 < #{now} " +
            "order by id desc " +
            "limit ${start}, ${limit} ")
    List<TaskRecord> selectListExpireTaskRecord(@Param("now") long now, @Param("start") int start, @Param("limit") int limit, @Param("prefix") String prefix);

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

    @Select("select " + COLUMN +
            "from " + TABLE +
            "where  job_uuid = #{jobUuid} and state != '" + TaskRecordStateConstant.RELY_UNDO + "' " +
            "order by id desc " +
            "limit 1")
    TaskRecord selectLastUndoTaskByJobUuid(@Param("jobUuid") String jobUuid, @Param("prefix") String prefix);
}
