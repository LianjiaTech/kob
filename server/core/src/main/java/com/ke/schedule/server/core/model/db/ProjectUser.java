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
 * 用户数据库实体类 对应数据表 kob_project_user 项目成员表
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 下午5:12
 */

public @NoArgsConstructor @Getter @Setter class ProjectUser implements Serializable {
    private static final long serialVersionUID = -3472861132474171115L;
    /**
     * id 数据库主键
     */
    private Integer id;
    /**
     * 用户标识
     */
    private String userCode;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 项目标识
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目类型admin管理员项目和service普通业务项目
     */
    private String projectMode;
    /**
     * 是否属于某业务线的负责人，默认为0，如果为负责人，则为1
     */
    private Boolean owner;
    /**
     * 基于此项目的个人配置JSON
     */
    private String configuration;
    /**
     * 创建时间
     */
    private Date gmtCreated;
    /**
     * 更新时间
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
