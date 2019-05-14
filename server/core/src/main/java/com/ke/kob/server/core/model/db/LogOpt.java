package com.ke.kob.server.core.model.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/13 下午1:34
 */

public @NoArgsConstructor @Getter @Setter class LogOpt implements Serializable {
    private static final long serialVersionUID = -7080428279319600936L;
    private Long id;
    private String userCode;
    private String userName;
    private String optUrl;
    private String request;
    private String response;
    private Long costTime;
    private Date gmtCreated;
    private Date gmtModified;
}
