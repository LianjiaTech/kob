package com.ke.schedule.client.spring.core;


import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.model.TaskContext;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.logger.OkHttpLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author zhaoyuguang
 */

public @Slf4j
enum ClientFunction {

    //INSTANCE
    INSTANCE;

    public Function<String, TaskContext.Path> convertPath() {
        return child -> {
            try {
                String dec = URLDecoder.decode(child, "UTF-8");
                return JSONObject.parseObject(dec, TaskContext.Path.class);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    public Predicate<Object[]> fireExpire() {
        return objects -> {
            TaskContext.Path path = (TaskContext.Path) objects[0];
            ClientContext client = (ClientContext) objects[1];
            String full = (String) objects[2];
            if (System.currentTimeMillis() - path.getTriggerTime() > client.getExpireRecyclingTime()) {
                if (client.getZkClient().delete(full)) {
                    OkHttpLogger.INSTANCE.systemLog(client, path, TaskRecordStateConstant.EXPIRE_RECYCLING);
                    return true;
                }
            }
            return false;
        };
    }

    public Consumer<Integer> sleep() {
        return sec -> {
            try {
                TimeUnit.SECONDS.sleep(sec);
            } catch (InterruptedException e) {
                log.error(ClientLogConstant.error510(), e);
            }
        };
    }

    public Predicate<Object[]> tryToExclusionNodeHasMe() {
        return objects -> {
            TaskContext.Path path = (TaskContext.Path) objects[0];
            ClientContext client = (ClientContext) objects[1];
            return !StringUtils.isEmpty(path.getTryToExclusionNode())
                    && path.getTryToExclusionNode().contains(client.getData().getIdentification());
        };
    }

    public Predicate<ClientContext> checkPoolSize() {
        return context -> {
            int active = context.getPool().getActiveCount();
            int max = context.getPool().getMaximumPoolSize();
            double value = new BigDecimal((float) active / max).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            double loadFactor = 0.6;
            if (value > loadFactor) {
                if (context.getData().getLogWarnEnable()) {
                    log.warn(ClientLogConstant.warn404(loadFactor, context.getData().getIdentification(), active, max));
                }
                return true;
            }
            return false;
        };
    }
}
