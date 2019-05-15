package com.ke.schedule.client.spring.env;

import com.ke.schedule.client.spring.startup.ClientProperties;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/26 下午8:59
 */

public class OfficialDevelop {

    private ClientEnvConfiguration.ClientEnv env = ClientEnvConfiguration.ClientEnv.official_develop;
    private String cluster = "incubator";
    private String zkServers = "localhost_zk:2801";
    private String adminUrl = "http://localhost:8668";
    private boolean logWarnEnable = true;

    public ClientProperties build() {
        return new ClientProperties(env.name(), cluster, zkServers, adminUrl, logWarnEnable);
    }
}
