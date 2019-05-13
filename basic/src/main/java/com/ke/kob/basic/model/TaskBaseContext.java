package com.ke.kob.basic.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 上午11:20
 */

public @NoArgsConstructor class TaskBaseContext implements Comparable<TaskBaseContext>{

    /**
     * 定义ZK path信息
     */
    private @Getter @Setter Path path;
    /**
     * 定义ZK data信息
     */
    private @Getter @Setter Data data;

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
        int triggerCompare = Long.compare(this.path.triggerTime, o.path.getTriggerTime());
        return triggerCompare == 0 ? this.data.taskUuid.compareTo(o.data.getTaskUuid()) : triggerCompare;
    }

    /**
     * 定义ZK path信息
     */
    static class Path{

        /**
         * 任务方法
         */
        private @Getter @Setter String taskKey;
        /**
         * 触发时间
         */
        private @Getter @Setter Long triggerTime;
    }

    /**
     * 定义ZK data信息
     */
    static class Data{
        /**
         * 可放一些跟踪信息
         */
        private @Getter @Setter JSONObject header;
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
    }
}
