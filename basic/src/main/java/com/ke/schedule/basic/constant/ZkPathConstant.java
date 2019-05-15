package com.ke.schedule.basic.constant;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 上午11:16
 */

public class ZkPathConstant {
    public static final String SERVER = "/server";
    public static final String CLIENT = "/client";
    public static final String BACKSLASH = "/";
    public static final String HYPHEN = "-";
    public static final String TASK = "/task";
    public static final String NODE = "/node";

    public static String clientTaskPath(String zp, String pc) {
        return BACKSLASH + zp + CLIENT + BACKSLASH + pc + TASK;
    }

    public static String clientNodePath(String zp, String pc) {
        return BACKSLASH + zp + CLIENT + BACKSLASH + pc + NODE;
    }

    public static String serverNodePath(String cluster) {
        return BACKSLASH + cluster + SERVER + NODE;
    }
}
