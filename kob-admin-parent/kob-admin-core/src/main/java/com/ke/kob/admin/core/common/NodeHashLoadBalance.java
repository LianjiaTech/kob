package com.ke.kob.admin.core.common;

import com.ke.kob.basic.support.KobUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 负载均衡算法 节点哈希
 *
 * @Author: zhaoyuguang
 * @Date: 2018/9/27 上午10:16
 */
public class NodeHashLoadBalance {

    /**
     * 节点唯一标识hash算法
     * 将字符串变成数字 根据长度取余数形成环装选择进行选取 排序后节点数组
     *
     * @param nodeList 节点标识列表
     * @param uuid     作业uuid
     * @return 选取节点
     */
    public static String doSelect(List<String> nodeList, String uuid) {
        if (KobUtils.isEmpty(nodeList)) {
            return null;
        }
        if (nodeList.size() == 1) {
            return nodeList.get(0);
        }
        String lowerCaseUuid = uuid.toLowerCase();
        String bigInt = "";
        int endIndex = 5;
        while (true) {
            if (!KobUtils.isEmpty(lowerCaseUuid) && lowerCaseUuid.length() > endIndex) {
                String sub = lowerCaseUuid.substring(0, endIndex);
                bigInt = bigInt + String.valueOf(Long.valueOf(sub, Character.MAX_RADIX));
                lowerCaseUuid = lowerCaseUuid.substring(endIndex, lowerCaseUuid.length());
            } else {
                bigInt = bigInt + String.valueOf(Long.valueOf(lowerCaseUuid, Character.MAX_RADIX));
                break;
            }
        }
        BigDecimal b = new BigDecimal(bigInt);
        Collections.sort(nodeList);
        int rem = b.divideAndRemainder(new BigDecimal(nodeList.size()))[1].intValue();
        return nodeList.get(rem);
    }
}
