package com.ke.kob.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/15 下午8:14
 */

public @NoArgsConstructor @Getter @Setter class LogContext implements Serializable {

    private static final long serialVersionUID = -6918526595897276922L;

    private Integer taskRecordState;
    private String projectCode;
    private String cluster;
    private String logUuid;
    private String taskUuid;
    private String logMode;
    private String logLevel;
    private String clientIdentification;
    private String msg;
    private Long logTime;
}
