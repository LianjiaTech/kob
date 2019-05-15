package com.ke.schedule.server.core.mapper;

import com.ke.schedule.server.core.model.db.LogOpt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/13 下午2:09
 */
@Mapper
public interface LogOptMapper {
    String COLUMN = " id, user_code, user_name, opt_url, request, response, cost_time, gmt_created, gmt_modified";

    String TABLE = " ${prefix}_log_opt ";

    /**
     * 保存操作日志
     *
     * @param logOpt
     * @param prefix
     * @return
     */
    @Insert("insert into " + TABLE +
            "(user_code, user_name, opt_url, request, response, cost_time) " +
            "values " +
            "(#{lo.userCode}, #{lo.userName}, #{lo.optUrl}, #{lo.request}, #{lo.response}, #{lo.costTime})")
    int insertOne(@Param("lo") LogOpt logOpt, @Param("prefix") String prefix);

    @Select("<script>" +
            "   select count(1) " +
            "   from " + TABLE +
            "   where 1 = 1 <if test='costTime != null'> and cost_time >= #{costTime} </if>" +
            "</script>")
    int selectCountByCostTime(@Param("costTime") Integer costTime, @Param("prefix") String prefix);

    @Select("<script>" +
            "   select " + COLUMN +
            "   from " + TABLE +
            "   where 1 = 1 <if test='costTime != null'> and cost_time >= #{costTime} </if>" +
            "   order by id desc " +
            "   limit ${start},${limit}" +
            "</script>")
    List<LogOpt> selectPageByCostTime(@Param("costTime") Integer costTime,
                                      @Param("start") Integer start,
                                      @Param("limit") Integer limit,
                                      @Param("prefix") String prefix);
}
