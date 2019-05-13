package com.ke.kob.basic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午2:32
 */

public @NoArgsConstructor @Getter @Setter class ZkAuthInfo {
    private String scheme;
    private String auth;
}
