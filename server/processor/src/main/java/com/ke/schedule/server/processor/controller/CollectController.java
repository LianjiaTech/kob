package com.ke.schedule.server.processor.controller;

import com.alibaba.fastjson.JSONObject;
import com.ke.schedule.server.core.model.oz.ResponseData;
import com.ke.schedule.server.core.service.CollectService;
import com.ke.schedule.basic.model.LogContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/29 上午10:14
 */
@RequestMapping("/collect")
@Controller
public @Slf4j class CollectController {

    @Resource
    private CollectService collectService;

    /**
     * 系统级日志访问地址
     *
     * @return ResponseData
     */
    @RequestMapping(value = "/system_collect.json")
    @ResponseBody
    public ResponseData systemCollect(@RequestBody JSONObject json) {
        try {
            log.info("system_logger request parameters: " + json.toJSONString());
            LogContext context = JSONObject.parseObject(json.toJSONString(), LogContext.class);
            collectService.handleLogger(context);
        } catch (Exception e){
            log.error("error", e);
        }
        return new ResponseData();
    }

    /**
     * 业务级日志访问地址
     *
     * @return ResponseData
     */
    @RequestMapping(value = "/service_collect.json")
    @ResponseBody
    public ResponseData serviceCollect(@RequestBody JSONObject json) {
        System.out.println("service_logger request parameters:" + json.toJSONString());
        return new ResponseData();
    }

    /**
     * ping
     *
     * @return pong
     */
    @RequestMapping(value = "/ping.json")
    @ResponseBody
    public String ping() {
        log.info("ping~");
        return "pong!";
    }
}
