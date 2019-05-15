package com.ke.schedule.server.core.mapper;

import com.ke.schedule.server.core.model.db.LogCollect;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 日志手机 mapper
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/17 下午2:32
 */
@Mapper
public interface LogCollectMapper {

    String COLUMN = " id, state, log_uuid, project_code, task_uuid, log_mode, log_level, client_identification," +
            "log_time, gmt_created, gmt_modified ";

    String TABLE = " ${prefix}_log_collect ";

    /**
     * 保存日志
     *
     * @param logCollect 收集日志
     * @param prefix    集群
     * @return 影响行数
     */
    @Insert("insert into " + TABLE +
            "(state, log_uuid, project_code, task_uuid, log_mode, log_level, " +
            "client_identification, log_time, msg) " +
            "values " +
            "(#{lc.state}, #{lc.logUuid}, #{lc.projectCode}, #{lc.taskUuid}, #{lc.logMode}, #{lc.logLevel}, " +
            "#{lc.clientIdentification}, #{lc.logTime}, #{lc.msg}) ")
    int insertOne(@Param("lc") LogCollect logCollect, @Param("prefix") String prefix);

    /**
     * 分页查询日志总量
     *
     * @param projectCode 项目标识
     * @param taskUuid    任务标识
     * @param prefix     集群
     * @return 总量
     */
    @Select("<script>" +
            "   select count(1) " +
            "   from " + TABLE +
            "   where project_code = #{projectCode} " +
            "   <if test='taskUuid != null'> and task_uuid = #{taskUuid} </if> " +
            "</script>")
    int selectCountByProjectCode(@Param("projectCode") String projectCode, @Param("taskUuid") String taskUuid, @Param("prefix") String prefix);

    /**
     * 分页查询日志
     *
     * @param projectCode 项目标识
     * @param taskUuid    任务标识
     * @param start       起始
     * @param limit       偏移量
     * @param prefix     集群
     * @return 日志列表
     */
    @Select("<script>" +
            "   select " + COLUMN +
            "   from " + TABLE +
            "   where project_code = #{projectCode} " +
            "   <if test='taskUuid != null'> and task_uuid = #{taskUuid} </if> " +
            "   order by id " +
            "   limit ${start},${limit} " +
            "</script>")
    List<LogCollect> selectPageByProjectCodeAndTaskUuid(@Param("projectCode") String projectCode,
                                                        @Param("taskUuid") String taskUuid,
                                                        @Param("start") Integer start,
                                                        @Param("limit") Integer limit,
                                                        @Param("prefix") String prefix);
}
