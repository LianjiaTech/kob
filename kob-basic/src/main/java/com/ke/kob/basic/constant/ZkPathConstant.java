package com.ke.kob.basic.constant;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 上午11:16
 */

public class ZkPathConstant {
    public static final String KOB = "/kob";
    public static final String SERVER = "/server";
    public static final String CLIENT = "/client";
    public static final String BACKSLASH = "/";
    public static final String HYPHEN = "-";
    public static final String TASK = "/task";
    public static final String NODE = "/node";

    public static String clientTaskPath(String cluster, String projectCode) {
        return BACKSLASH + cluster + KOB + CLIENT + BACKSLASH + projectCode + TASK;
    }

    public static String clientNodePath(String cluster, String projectCode) {
        return BACKSLASH + cluster + KOB + CLIENT + BACKSLASH + projectCode + NODE;
    }

    public static String serverNodePath(String cluster) {
        return BACKSLASH + cluster + KOB + SERVER + NODE;
    }
}
