package com.ke.schedule.server.core.model.db;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.model.InnerParams;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午10:45
 */

public @NoArgsConstructor @Getter @Setter class TaskRecord implements Serializable {

    private static final long serialVersionUID = -4248471694521667743L;

    /**
     * id
     */
    private Long id;
    /**
     * 项目标识
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 作业标识
     */
    private String jobUuid;
    /**
     * 作业类型
     */
    private String jobType;
    /**
     * 作业名称
     */
    private String jobCn;
    /**
     * 任务方法key
     */
    private String taskKey;
    /**
     * 任务备注
     */
    private String taskRemark;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务标识
     */
    private String taskUuid;
    /**
     * 依赖任务标识
     */
    private String relationTaskUuid;
    /**
     * 路由规则
     */
    private String loadBalance;
    /**
     * 批处理类型
     */
    private String batchType;
    /**
     * 重试类型
     */
    private String retryType;
    /**
     * 作业配置 任务是否依赖上一周期
     */
    private Boolean rely;
    /**
     * 是否是祖先
     */
    private Boolean ancestor;
    /**
     * 用户自定义参数
     */
    private String userParams;
    /**
     * 内部参数
     */
    private String innerParams;
    /**
     * 最后返回结果
     */
    private String msg;
    /**
     * cron表达式
     */
    private String cronExpression;
    /**
     * 超时时间
     */
    private Integer timeoutThreshold;
    /**
     * 当前状态
     */
    private Integer state;
    /**
     * 是否完成
     */
    private Boolean complete;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 故障迁移
     */
    private Boolean failover;
    /**
     * 客户端执行节点
     */
    private String clientIdentification;
    /**
     * 触发时间
     */
    private Long triggerTime;
    /**
     * 客户端消费时间
     */
    private Date consumptionTime;
    /**
     * 客户端执行时间
     */
    private Date executeStartTime;
    /**
     * 客户端完成时间
     */
    private Date executeEndTime;
    /**
     * 乐观锁
     */
    private Integer version;
    /**
     * 创建时间
     */
    private Date gmtCreated;
    /**
     * 最后更新时间
     */
    private Date gmtModified;

    public InnerParams getInnerParamsBean() {
        return JSONObject.parseObject(this.innerParams, InnerParams.class);
    }
}
