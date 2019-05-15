package com.ke.schedule.server.core.mapper;

import com.ke.schedule.server.core.model.db.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * mybatis mapper 操作数据库表_user 用户表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 下午5:54
 */
@Mapper
public interface UserMapper {

    String COLUMN = " id, code, name, pwd, configuration, version, gmt_created, gmt_modified ";

    String TABLE = " ${prefix}_user ";

    /**
     * 根据 code & pwd 查询用户
     *
     * @param code   用户标识
     * @param pwd    用户密码
     * @param prefix prefix
     * @return 用户对象
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where code=#{code} and pwd=#{pwd} ")
    User selectByCodeAndPwd(@Param("code") String code, @Param("pwd") String pwd, @Param("prefix") String prefix);

    /**
     * 根据 code 查询用户
     *
     * @param code   用户标识
     * @param prefix 集群
     * @return 用户对象
     */
    @Select("select " + COLUMN +
            "from " + TABLE +
            "where code=#{code} ")
    User selectOneByCode(@Param("code") String code, @Param("prefix") String prefix);

    /**
     * 查询用户根据用户Code
     *
     * @param codes  用户标识列表 Collection
     * @param prefix prefix
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
    List<User> selectByCodes(@Param("codes") Set<String> codes, @Param("prefix") String prefix);
}
