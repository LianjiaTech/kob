package com.ke.schedule.client.spring.constant;

import com.ke.schedule.client.spring.env.ClientEnvConfiguration;
import com.ke.schedule.client.spring.env.OfficialDevelop;
import com.ke.schedule.client.spring.startup.ClientProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午7:54
 */

public class ClientConstant {

    public static final String VERSION = "1.0.0-SNAPSHOT";
    public static final int DEFAULT_ZK_SESSION_TIMEOUT = 10000;
    public static final int DEFAULT_ZK_CONNECTION_TIMEOUT = 16000;
    public static final String DEFAULT_TASK_LOG_PATH = "/collect/system_collect.json";
    public static final String DEFAULT_SERVICE_LOG_PATH = "/collect/service_collect.json";
    public static final int DEFAULT_CLIENT_WORKS = 72;
    public static final int MIN_EXPIRE_RECYCLING_TIME = 30;
    public static final long DEFAULT_INITIAL_DELAY = 16L;
    public static final long DEFAULT_HEARTBEAT_PERIOD = 60L;
    public static final boolean DEFAULT_LOG_WARN_ENABLE = false;
    public static final int DEFAULT_EXPIRE_RECYCLING_SEC = 30;
    public static final double DEFAULT_LOAD_FACTOR = 0.6;

    public static final List<String> OFFICIAL_ENV_LIST = new ArrayList<String>() {{
        add(ClientEnvConfiguration.ClientEnv.official_develop.name());
    }};

    public static final Map<ClientEnvConfiguration.ClientEnv, ClientProperties> OFFICIAL_ENV_MAP = new HashMap<ClientEnvConfiguration.ClientEnv, ClientProperties>() {{
        put(ClientEnvConfiguration.ClientEnv.official_develop, new OfficialDevelop().build());
    }};
}
