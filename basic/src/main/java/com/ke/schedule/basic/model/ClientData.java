package com.ke.schedule.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public @NoArgsConstructor @Getter @Setter class ClientData implements Serializable {

    private static final long serialVersionUID = 1741453540074964061L;

    private String version;
    private String token;
    private String ip;
    private String identification;
    private String projectCode;
    private Long created;
    private Long modified;
    private Map<String, String> tasks = new HashMap<>();
    private Map<String, RunningTaskInfo> runningTask = new ConcurrentHashMap<>();
    private Boolean logWarnEnable;
    private Integer expireRecyclingTime;
    private Double loadFactor;
    private Integer workers;
    private Integer threads;
    private Long heartbeatPeriod;
}
