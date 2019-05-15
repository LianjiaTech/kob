package com.ke.schedule.server.core.model.oz;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/12 上午11:58
 */

import com.ke.schedule.server.core.common.AdminConstant;
import com.ke.schedule.basic.support.KobUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public @NoArgsConstructor @Getter @Setter class CoreProperties implements Serializable {
    private static final long serialVersionUID = 7205860990850436900L;
    private String zkServers;
    private Integer zkSessionTimeout;
    private Integer zkConnectionTimeout;
    private String mysqlPrefix;

    public void initialize() {
        if (KobUtils.isEmpty(this.zkConnectionTimeout)) {
            this.zkConnectionTimeout = AdminConstant.DEFAULT_ZK_CONNECTION_TIMEOUT;
        }
        if (KobUtils.isEmpty(this.zkSessionTimeout)) {
            this.zkSessionTimeout = AdminConstant.DEFAULT_ZK_SESSION_TIMEOUT;
        }
    }
}
