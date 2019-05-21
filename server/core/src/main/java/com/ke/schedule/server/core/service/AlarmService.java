package com.ke.schedule.server.core.service;

import com.ke.schedule.server.core.model.db.TaskRecord;

/**
 * @author zhaoyuguang
 */

public interface AlarmService {

    void send(TaskRecord record);

    void run(TaskRecord record);

    void end(TaskRecord record);
}
