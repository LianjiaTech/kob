package com.ke.kob.server.core.model.oz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/21 下午9:03
 */

public @NoArgsConstructor @Getter @Setter class MasterElectorNotice implements Serializable {
    private static final long serialVersionUID = 8664641081803071359L;
    private String serverIdentification;
    private Long date;

    public MasterElectorNotice(String serverIdentification) {
        this.serverIdentification = serverIdentification;
        this.date = System.currentTimeMillis();
    }
}
