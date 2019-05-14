package com.ke.kob.server.core.configuration;

import com.alibaba.fastjson.JSON;
import com.ke.kob.server.core.model.oz.CoreProperties;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * zk配置类
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 上午11:00
 */
@Configuration
public class ZkConfiguration {

    @Resource(name = "kobCoreProperties")
    private CoreProperties coreProperties;

    @Bean(name = "zkClient")
    public ZkClient zkClient() {
        return new ZkClient(coreProperties.getZkServers(), coreProperties.getZkSessionTimeout(), coreProperties.getZkConnectionTimeout(), new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                if (data instanceof String) {
                    return ((String) data).getBytes();
                }
                return JSON.toJSONString(data).getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });
    }

}
