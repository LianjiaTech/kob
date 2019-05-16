package com.ke.schedule.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public @Getter @Setter @NoArgsConstructor class InnerParams {
    /**
     * cron类型作业生成节点
     */
    private String cronTaskGenerateNode;
    /**
     * 依赖未执行完成的任务标识
     */
    private String relyUndoTaskUuid;
    /**
     * 追加重试任务的任务标识
     */
    private String appendRetryTaskUuid;
    /**
     * 任务推送节点
     */
    private String taskPushNode;
    /**
     * 指定节点 用于实时任务 灰度发布
     */
    private String designatedNode;
    /**
     * 推荐节点 node_hash
     */
    private String recommendNode;
    /**
     * 尝试排除节点 如：103.126.211.130-wqkksdz8evCIjm33e9i9,102.133.39.168-z1vwh6i6leCIjm35te0q
     */
    private String tryToExclusionNode;
    /**
     * 创建创建作业人员
     */
    private String createUserName;
}
