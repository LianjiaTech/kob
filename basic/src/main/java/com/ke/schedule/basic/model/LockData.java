package com.ke.schedule.basic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyuguang
 */
@Data
public @NoArgsConstructor class LockData {
    private String id;
    private Long expire;

    public LockData(String id, Long expire) {
        this.id = id;
        this.expire = expire;
    }
}
