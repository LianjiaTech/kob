package com.ke.kob.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * client 在zk的path 用于快速完成注册发现并降低 注册中心的qps
 *
 * @Author: zhaoyuguang
 * @Date: 2018/8/3 上午10:28
 */

public @NoArgsConstructor
@Getter
@Setter
class ClientPath implements Serializable {

    private String ip;
    private String identification;
    private String projectCode;
    private Map<String, String> tasks;


    public ClientPath(String ip, String identification, String projectCode, Map<String, String> tasks) {
        this.ip = ip;
        this.identification = identification;
        this.projectCode = projectCode;
        this.tasks = tasks;
    }
}
