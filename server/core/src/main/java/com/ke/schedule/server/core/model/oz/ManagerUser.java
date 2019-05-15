package com.ke.schedule.server.core.model.oz;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/9/4 下午12:04
 */

import com.ke.schedule.server.core.model.db.ProjectUser;
import com.ke.schedule.server.core.model.db.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public @NoArgsConstructor @Getter @Setter class ManagerUser extends ProjectUser implements Serializable {
    private User user = new User();
    private Boolean master;
}
