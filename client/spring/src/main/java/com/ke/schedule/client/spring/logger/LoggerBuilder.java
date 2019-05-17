package com.ke.schedule.client.spring.logger;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.basic.model.ClientData;
import com.ke.schedule.basic.model.LogMode;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author zhaoyuguang
 */

public class LoggerBuilder {
    private JSONObject json = new JSONObject();

    public LoggerBuilder client(ClientData client) {
        this.json.put(LogParam.client_id.name(), client.getIdentification());
        this.json.put(LogParam.project.name(), client.getProjectCode());
        this.json.put(LogParam.ip.name(), client.getIp());
        this.json.put(LogParam.version.name(), client.getVersion());
        this.json.put(LogParam.token.name(), client.getToken());
        return this;
    }

    public LoggerBuilder setLogMode(LogMode mode) {
        this.json.put(LogParam.log_mode.name(), mode.name());
        return this;
    }

    public LoggerBuilder setResultMode(ResultMode mode) {
        this.json.put(LogParam.result_mode.name(), mode.name());
        return this;
    }

    public LoggerBuilder setMessage(String msg) {
        this.json.put(LogParam.msg.name(), msg);
        return this;
    }

    public LoggerBuilder setNow() {
        this.json.put(LogParam.now.name(), System.currentTimeMillis());
        return this;
    }

    public RequestBody build() {
        MediaType media = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(media, this.json.toJSONString());
    }
}
