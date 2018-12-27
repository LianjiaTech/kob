package com.ke.kob.basic.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:20
 */

public @NoArgsConstructor class TaskBaseContext implements Comparable<TaskBaseContext>{
    /**
     * 项目标识
     */
    private @Getter @Setter String projectCode;
    /**
     * 作业唯一标识
     */
    private @Getter @Setter String jobUuid;
    /**
     * 作业名称
     */
    private @Getter @Setter String jobCn;
    /**
     * 任务唯一标识
     */
    private @Getter @Setter String taskUuid;
    /**
     * 任务方法
     */
    private @Getter @Setter String taskKey;
    /**
     * 触发时间
     */
    private @Getter @Setter Long triggerTime;
    /**
     * 指定执行节点 用于实时任务的灰度发布
     */
    private @Getter @Setter String designatedNode;
    /**
     * 推荐执行节点 用于节点哈希算出的推荐节点
     */
    private @Getter @Setter String recommendNode;
    /**
     * 逗号分隔排除执行节点
     */
    private @Getter @Setter String tryToExclusionNode;
    /**
     * 用户自定义参数可以用于分片或一些其他场景
     */
    private @Setter JSONObject userParam;
    /**
     * 节点path 尽可能不使用clientPath对象反向生成path
     */
    private @Getter @Setter String path;

    /**
     * 获取自定义参数 这里不建议 因为你要是使用key的话 需要判空
     *
     * @return 自定义参数对象
     */
    @Deprecated
    public JSONObject getUserParam() {
        return userParam;
    }

    /**
     * 获取自定义参数中key的value值
     *
     * @param key key
     * @return value
     */
    public String getUserParamValue(String key) {
        return userParam == null ? null : String.valueOf(userParam.get(key));
    }

    /**
     * 排序用于触发
     *
     * @param o 用于比较的作业事件
     * @return 比较结果
     */
    @Override
    public int compareTo(TaskBaseContext o) {
        if (this == o) {
            return 0;
        }
        int triggerCompare = Long.compare(this.triggerTime, o.getTriggerTime());
        return triggerCompare == 0 ? this.taskUuid.compareTo(o.getTaskUuid()) : triggerCompare;
    }
}
