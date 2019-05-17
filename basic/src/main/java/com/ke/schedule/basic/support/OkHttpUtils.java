package com.ke.schedule.basic.support;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoyuguang on on 2017/10/12
 */
public @Slf4j class OkHttpUtils {

    private static final OkHttpClient okHttpClient;

    public static Response post(String url, RequestBody body) {
        Request request = (new Request.Builder()).url(url).post(body).build();

        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException var4) {
            log.error("Http Post请求失败,url:" + url + ", body:" + JSONObject.toJSONString(body), var4);
            return null;
        }
    }

    static {
        okHttpClient = (new okhttp3.OkHttpClient.Builder()).connectTimeout(6L, TimeUnit.SECONDS).readTimeout(6L, TimeUnit.SECONDS).writeTimeout(6L, TimeUnit.SECONDS).build();
    }
}
