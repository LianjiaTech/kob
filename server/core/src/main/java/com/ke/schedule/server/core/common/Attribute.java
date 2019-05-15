package com.ke.schedule.server.core.common;

/**
 * request session 中的属性key
 *
 * @Author: zhaoyuguang
 * @Date: 2018/7/26 下午6:16
 */
public interface Attribute {
    /**
     * session中的user key
     */
    String SESSION_USER = "session_user";
    String PROJECT_SELECTED = "project_selected";
    String PROJECT_LIST = "project_list";

    /**
     * index页 主操作页面screen 参数地址
     */
    String INDEX_SCREEN = "index_screen";
}
