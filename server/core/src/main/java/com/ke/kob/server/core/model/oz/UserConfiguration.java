package com.ke.kob.server.core.model.oz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/20 下午8:31
 */

public @NoArgsConstructor @Getter @Setter class UserConfiguration implements Serializable {
    private static final long serialVersionUID = -7869787963771552886L;

    private String mail;
}
