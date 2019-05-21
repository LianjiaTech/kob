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

    public static final List<String> OFFICIAL_ENV_LIST = new ArrayList<String>() {{
        add(ClientEnvConfiguration.ClientEnv.official_develop.name());
    }};

    public static final Map<ClientEnvConfiguration.ClientEnv, ClientProperties> OFFICIAL_ENV_MAP = new HashMap<ClientEnvConfiguration.ClientEnv, ClientProperties>() {{
        put(ClientEnvConfiguration.ClientEnv.official_develop, new OfficialDevelop().build());
    }};
}
