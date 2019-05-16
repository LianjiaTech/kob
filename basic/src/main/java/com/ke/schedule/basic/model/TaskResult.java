package com.ke.schedule.basic.model;

import com.ke.schedule.basic.constant.TaskRecordStateConstant;
import com.ke.schedule.basic.support.KobUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public @NoArgsConstructor @Getter @Setter class TaskResult {

    private Integer state;
    private String msg;

    public TaskResult(int state) {
        this.state = state;
    }


    public TaskResult(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public static TaskResult success() {
        return new TaskResult(TaskRecordStateConstant.EXECUTE_SUCCESS);
    }

    public static TaskResult success(String msg) {
        return new TaskResult(TaskRecordStateConstant.EXECUTE_SUCCESS, msg);
    }

    public static TaskResult fail() {
        return new TaskResult(TaskRecordStateConstant.EXECUTE_FAIL);
    }

    public static TaskResult fail(InterruptedException e) {
        return new TaskResult(TaskRecordStateConstant.EXECUTE_FAIL, KobUtils.exception2String(e));
    }

    public static TaskResult fail(String msg) {
        return new TaskResult(TaskRecordStateConstant.EXECUTE_FAIL, msg);
    }
}
