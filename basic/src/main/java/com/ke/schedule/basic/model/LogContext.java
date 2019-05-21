package com.ke.schedule.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public @NoArgsConstructor @Getter @Setter class LogContext implements Serializable {

    private static final long serialVersionUID = -6918526595897276922L;

    private Integer state;
    private String projectCode;
    private String ip;
    private String logUuid;
    private String taskUuid;
    private String token;
    private String clientIdentification;
    private String msg;
    private String version;
    private Long logTime;
}
