package com.ke.schedule.client.spring.core;


import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.client.spring.constant.ClientLogConstant;
import com.ke.schedule.client.spring.logger.OkHttpLogger;
import com.ke.schedule.client.spring.model.TaskContext;
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
    //1
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
                    OkHttpLogger.INSTANCE.expireRecycling(client, path);
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
                    && path.getTryToExclusionNode().contains(client.getClient().getIdentification());
        };
    }

    public Predicate<ClientContext> checkPoolSize() {
        return context -> {
            int active = context.getPool().getActiveCount();
            int max = context.getPool().getMaximumPoolSize();
            double value = new BigDecimal((float) active / max).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            double loadFactor = context.getClient().getLoadFactor();
            if (value > loadFactor) {
                if (context.getClient().getLogWarnEnable()) {
                    log.warn(ClientLogConstant.warn404(loadFactor, context.getClient().getIdentification(), active, max));
                }
                return true;
            }
            return false;
        };
    }
}
