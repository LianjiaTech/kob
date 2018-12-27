package com.ke.kob.admin.core.model.oz;

import com.ke.kob.basic.model.ClientData;
import com.ke.kob.basic.model.ClientPath;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/8/22 下午5:22
 */

public @NoArgsConstructor @Getter @Setter class ClientInfo implements Serializable {

    private static final long serialVersionUID = -3291465885897049041L;
    private String path;
    private ClientPath clientPath;
    private ClientData clientData;

    public ClientInfo(String path, ClientPath clientPath, ClientData clientData) {
        this.path = path;
        this.clientPath = clientPath;
        this.clientData = clientData;
    }
}
