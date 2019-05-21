package com.ke.schedule.client.spring.logger;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.LogContext;
import com.ke.schedule.basic.support.UuidUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author zhaoyuguang
 */

public class LoggerBuilder {
    private LogContext log = new LogContext();

    public LoggerBuilder client(ClientData client) {
        this.log.setClientIdentification(client.getIdentification());
        this.log.setProjectCode(client.getProjectCode());
        this.log.setIp(client.getIp());
        this.log.setVersion(client.getVersion());
        this.log.setToken(client.getToken());
        return this;
    }

    public LoggerBuilder setMessage(Throwable t) {
        try {
            if (t != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                this.log.setMsg(sw.getBuffer().toString());
                sw.close();
                pw.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }


    public LoggerBuilder setMessage(String msg) {
        this.log.setMsg(msg);
        return this;
    }

    public LoggerBuilder taskUuid(String taskUuid) {
        this.log.setTaskUuid(taskUuid);
        return this;
    }

    public LoggerBuilder uuid() {
        this.log.setLogUuid(UuidUtils.builder(UuidUtils.AbbrType.LU));
        return this;
    }

    public RequestBody build() {
        MediaType media = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(media, JSONObject.toJSONString(this.log));
    }

    public LoggerBuilder state(int state) {
        this.log.setState(state);
        return this;
    }

    public LoggerBuilder now() {
        this.log.setLogTime(System.currentTimeMillis());
        return this;
    }

}
