package com.ke.kob.server.core.mapper;

import com.ke.kob.server.core.model.db.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * mybatis mapper 操作数据库表kob_user_ 用户表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 下午5:54
 */
@Mapper
public interface UserMapper {

    String COLUMN = " id, code, name, pwd, configuration, version, gmt_created, gmt_modified ";

    String TABLE = " kob_user_${cluster} ";

    /**
     * 根据 code & pwd 查询用户
     *
     * @param code    用户标识
     * @param pwd     用户密码
     * @param cluster 集群
     * @return 用户对象
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where code=#{code} and pwd=#{pwd} ")
    User selectByCodeAndPwd(@Param("code") String code, @Param("pwd") String pwd, @Param("cluster") String cluster);

    /**
     * 根据 code 查询用户
     *
     * @param code    用户标识
     * @param cluster 集群
     * @return 用户对象
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where code=#{code} ")
    User selectOneByCode(@Param("code") String code, @Param("cluster") String cluster);

    /**
     * 查询用户根据用户Code
     *
     * @param codes   用户标识列表 Collection
     * @param cluster 集群
     * @return 用户列表
     */
    @Select("<script>" +
            "   select " + COLUMN +
            "   from " + TABLE +
            "   where code in " +
            "       <foreach collection='codes' item='code' separator=',' open='(' close=')'> " +
            "           #{code} " +
            "       </foreach> " +
            "</script>")
    List<User> selectByCodes(@Param("codes") Set<String> codes, @Param("cluster") String cluster);
}
