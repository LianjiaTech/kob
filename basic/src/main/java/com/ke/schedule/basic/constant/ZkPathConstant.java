package com.ke.schedule.basic.constant;

public class ZkPathConstant {
    public static final String SERVER = "/server";
    public static final String CLIENT = "/client";
    public static final String BACKSLASH = "/";
    public static final String HYPHEN = "-";
    public static final String TASK = "/task";
    public static final String NODE = "/node";
    public static final String CONFIG = "/config";
    public static final String CRON = "/cron";
    public static final String WAIT = "/wait";

    public static String clientTaskPath(String zp, String pc) {
        return BACKSLASH + zp + CLIENT + BACKSLASH + pc + TASK;
    }

    public static String clientNodePath(String zp, String pc) {
        return BACKSLASH + zp + CLIENT + BACKSLASH + pc + NODE;
    }

    public static String serverNodePath(String zp) {
        return BACKSLASH + zp + SERVER + NODE;
    }

    public static String serverConfigPath(String zp) {
        return BACKSLASH + zp + SERVER + CONFIG;
    }

    public static String serverCronPath(String zp) {
        return BACKSLASH + zp + SERVER + CONFIG + CRON;
    }

    public static String serverWaitPath(String zp) {
        return BACKSLASH + zp + SERVER + CONFIG + WAIT;
    }
}
