package com.ke.kob.admin.core.model.oz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * api请求返回response对象
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/28 下午9:02
 */

public @NoArgsConstructor @Getter @Setter class ResponseData implements Serializable {
    private static final long serialVersionUID = -213286064058861615L;

    private boolean success;
    private String message;
    private int results;
    private Collection<?> rows;
    private Map<?, ?> others;

    public ResponseData(boolean success, int results) {
        this.success = success;
        this.results = results;
    }

    public ResponseData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseData(boolean success) {
        this.success = success;
    }


    public ResponseData(boolean success, int results, Collection<?> rows) {
        this.success = success;
        this.results = results;
        this.rows = rows;
    }

    public static ResponseData error(String message) {
        return new ResponseData(false, message);
    }

    public static ResponseData success() {
        return new ResponseData(true);
    }

    public static ResponseData success(int results, Collection<?> rows) {
        return new ResponseData(true, results, rows);
    }

    public static ResponseData success(int results) {
        return new ResponseData(true, results);
    }

    public static ResponseData success(Collection<?> rows) {
        return new ResponseData(true, rows.size(), rows);
    }
}
