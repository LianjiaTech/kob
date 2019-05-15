package com.ke.schedule.server.processor.kernel;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.server.core.model.oz.ClientInfo;
import com.ke.schedule.server.core.model.oz.ProcessorProperties;
import com.ke.schedule.basic.constant.ZkPathConstant;
import com.ke.schedule.basic.model.RunningTaskInfo;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KOB作业调动中心 存储数据容器
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/30 下午5:44
 */
@Component("serverContext")
public class ServerContext implements InitializingBean {

//    @Resource(name = "kobProcessorProperties")
    private ProcessorProperties processorProperties;
    @Value("${kob.cluster}")
    private String cluster;

    private @Getter MasterElector masterElector;
    private @Getter String masterPath;
    private @Getter Map<String, Map<String, ClientInfo>> clientNodeMap = new ConcurrentHashMap<>();
    private @Getter Set<String> projectCodeSet = new HashSet<>();
    private static Date now = new Date();

    public String getLocalNodePath() {
        return ZkPathConstant.serverNodePath(cluster) + ZkPathConstant.BACKSLASH + JSONObject.toJSONString(masterElector.getLocal());
    }

    public String getCluster(){
        return cluster;
    }

    public String getLocalNodeIdentification() {
        return masterElector.getLocal().getIdentification();
    }

    public boolean isMaster() {
        return masterElector.isMaster();
    }

    String getLocalIdentification() {
        return masterElector.getLocal().getIdentification();
    }

    public String getProjectBasePath(String cluster) {
        return null;//todo ZkPathConstant.KOB + ZkPathConstant.BACKSLASH + cluster + ZkPathConstant.CLIENT;
    }

    public boolean taskInRunning(String projectCode, String clientIdentification, String taskUuid) {
        Map<String, ClientInfo> clientInfoMap = clientNodeMap.get(projectCode);
        if(clientInfoMap == null){
            return false;
        }
        final ClientInfo clientInfo = clientInfoMap.get(clientIdentification);
        if(clientInfo==null){
            return false;
        }
        final Map<String, RunningTaskInfo> runningTask = clientInfo.getClientData().getRunningTask();
        if(runningTask == null){
            return false;
        }
        if(runningTask.get(taskUuid) == null){
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.masterElector = new MasterElector(cluster, now.getTime());
        this.masterPath = ZkPathConstant.serverNodePath(cluster);
    }
}
