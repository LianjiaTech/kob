package com.ke.schedule.client.spring.startup;

import com.ke.schedule.client.spring.constant.ClientConstant;
import com.ke.schedule.basic.model.ZkAuthInfo;
import com.ke.schedule.basic.support.KobUtils;
import com.ke.schedule.client.spring.env.ClientEnvConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午12:27
 */

public @NoArgsConstructor @Getter @Setter class ClientProperties {
    private String env;
    private String projectCode;
    private String zkConnectString;
    private String zkPrefix;
    private Integer zkSessionTimeout;
    private Integer zkConnectionTimeout;
    private String adminUrl;
    private String systemLogPath;
    private String serviceLogPath;
    private Integer threads;
    private List<ZkAuthInfo> zkAuthInfo;
    private Boolean logWarnEnable;
    /**
     * 需要大于等于30秒 才生效
     */
    private Integer expireRecyclingSec;
    private Double loadFactor;
    private Long initialDelay;
    private Long heartbeatPeriod;

    public ClientProperties(String env, String zkPrefix, String zkConnectString, String adminUrl, boolean logWarnEnable) {
        this.env = env;
        this.zkPrefix = zkPrefix;
        this.zkConnectString = zkConnectString;
        this.adminUrl = adminUrl;
        this.logWarnEnable = logWarnEnable;
    }

    public ClientProperties build() {
        if (ClientConstant.OFFICIAL_ENV_LIST.contains(this.env)) {
            ClientProperties official = ClientConstant.OFFICIAL_ENV_MAP.get(ClientEnvConfiguration.ClientEnv.valueOf(env));
            if (KobUtils.isEmpty(this.zkPrefix)) {
                this.zkPrefix = official.zkPrefix;
            }
            if (KobUtils.isEmpty(this.zkConnectString)) {
                this.zkConnectString = official.zkConnectString;
            }
            if (KobUtils.isEmpty(this.adminUrl)) {
                this.adminUrl = official.adminUrl;
            }
            if (KobUtils.isEmpty(this.logWarnEnable)) {
                this.logWarnEnable = official.logWarnEnable;
            }
        }
        return this;
    }
}
