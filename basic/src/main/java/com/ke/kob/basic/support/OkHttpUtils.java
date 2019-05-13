package com.ke.kob.basic.support;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang on on 2017/10/12
 */
public @Slf4j class OkHttpUtils {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS).writeTimeout(6, TimeUnit.SECONDS).build();

    public static Response get(String url) {
        return execute(url, null);
    }

    public static Response execute(String url, RequestBody body) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (builder != null) {
            builder.get();
        }
        Request request = builder.build();
        Response response = null;
        try {
            response = OK_HTTP_CLIENT.newCall(request).execute();
            return response;
        } catch (IOException e) {
            log.error("Http Post请求失败,url:" + url + ", body:" + JSONObject.toJSONString(body), e);
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
