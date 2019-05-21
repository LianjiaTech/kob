package com.ke.schedule.basic.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TaskContext {

    private @Getter @Setter Path path;
    private @Getter @Setter Data data;

    public TaskContext() {
        this.path = new Path();
        this.data = new Data();
    }

    public TaskContext(Path path, Data data) {
        this.path = path;
        this.data = data;
    }

    public String getZkPath() throws UnsupportedEncodingException {
        return URLEncoder.encode(JSONObject.toJSONString(this.path), "UTF-8");
    }

    /**
     * 定义ZK path信息
     */
    public @NoArgsConstructor static class Path implements Comparable<TaskContext.Path>{

        /**
         * 任务方法
         */
        private @Getter @Setter String taskKey;
        /**
         * 任务唯一标识
         */
        private @Getter @Setter String taskUuid;
        /**
         * 触发时间
         */
        private @Getter @Setter Long triggerTime;
        /**
         * 指定执行节点 用于实时任务的灰度发布
         */
        private @Getter @Setter String designatedNode;
        /**
         * 指定执行节点 用于实时任务的灰度发布
         */
        private @Getter @Setter String designatedIP;
        /**
         * 推荐执行节点 用于节点哈希算出的推荐节点
         */
        private @Getter @Setter String recommendNode;
        /**
         * 逗号分隔排除执行节点
         */
        private @Getter @Setter String tryToExclusionNode;

        private @Getter @Setter String path;

        @Override
        public int compareTo(Path o) {
            return Long.compare(this.triggerTime, o.getTriggerTime());
        }
    }

    /**
     * 定义ZK data信息
     */
    public static class Data{
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
