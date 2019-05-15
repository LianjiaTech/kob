package com.ke.schedule.server.core.model.oz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 服务端节点信息
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午5:47
 */

public @NoArgsConstructor @Getter @Setter class NodeServer implements Serializable {

    private static final long serialVersionUID = -1675592794365881127L;

    private String identification;
    private String cluster;
    private String ip;
    private String uuid;
    private String state;
    private long created;

    public NodeServer(String cluster, String ip, String uuid, long created) {
        this.identification = ip + "_" + uuid;
        this.cluster = cluster;
        this.ip = ip;
        this.uuid = uuid;
        this.created = created;
    }

}
