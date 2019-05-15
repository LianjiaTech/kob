package com.ke.schedule.basic.support;

import java.util.Random;
import java.util.UUID;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/6/25 下午8:28
 */

public class UuidUtils {
    private static final int MIN_INT_5 = 1679616;
    private static final int MAX_INT_5 = 60466175;

    private static String Random5() {
        return Long.toString(new Random(UUID.randomUUID().hashCode()).nextInt(MAX_INT_5 - MIN_INT_5) + MIN_INT_5, Character.MAX_RADIX);
    }

    private static String Random10() {
        return Random5() + Random5();
    }

    public static String builder(UuidUtils.AbbrType abbr) {
        String prefix = Random10();
        String suffix = Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

        if (abbr == null) {
            abbr = AbbrType.DF;
        }
        return prefix + abbr.name() + suffix;
    }

    public static enum AbbrType {
        /**
         * 默认
         */
        DF,
        /**
         * JobCron cron作业使用
         */
        JC,
        /**
         * TaskWaiting 等待执行任务使用
         */
        TW,
        /**
         * AppendRetry 追加重试
         */
        AR,
        /**
         * ClientData 客户端
         */
        CI,
        /**
         * ServerNode 服务端
         */
        SN,
        /**
         * LogUuid 日志唯一标识
         */
        LU
    }
}


