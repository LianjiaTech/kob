package com.ke.schedule.server.core.model.db;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.server.core.model.oz.UserConfiguration;
import com.ke.schedule.basic.support.KobUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户数据库实体类 对应数据表 kob_user_ 用户表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/6/14 下午5:07
 */

public @NoArgsConstructor @Getter @Setter class User implements Serializable {
    private static final long serialVersionUID = -1518163329205751092L;
    /**
     * id 数据库主键
     */
    private Integer id;
    /**
     * 账号 唯一索引
     */
    private String code;
    /**
     * 姓名 用于展示
     */
    private String name;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 个人配置信息，预留JSON字段可以存邮箱手机号之类
     */
    private String configuration;
    /**
     * version 乐观锁
     */
    private Integer version;
    /**
     * 创建时间 CURRENT_TIMESTAMP
     */
    private Date gmtCreated;
    /**
     * 更新时间 CURRENT_TIMESTAMP
     */
    private Date gmtModified;

    /**
     * Json配置信息
     *
     * @return Json配置信息
     */
    public UserConfiguration getUserConfiguration() {
        return KobUtils.isEmpty(this.configuration) ? null : JSONObject.parseObject(this.configuration, UserConfiguration.class);
    }
}
