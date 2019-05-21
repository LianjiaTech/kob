<div class="navbar-default sidebar" role="navigation">
    <div class="sidebar-nav navbar-collapse">
        <ul class="nav" id="side-menu">
            <li>
                <a href="/index.htm"><i class="fa fa-dashboard fa-fw"></i> 首页</a>
            </li>
            <li>
                <a href="#"><i class="fa fa-th fa-fw"></i> 项目管理<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="/manager/project_access.htm">项目接入</a>
                    </li>
                <#if Session["project_selected"]??>
                    <li>
                        <a href="/manager/project_user.htm">人员管理</a>
                    </li>
                    <li>
                        <a href="/manager/person_config.htm">个人配置</a>
                    </li>
                </#if>
                </ul>
            </li>
            <li>
                <a href="#"><i class="fa fa-th-list fa-fw"></i> 节点信息<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="/node/server_node.htm">服务端节点</a>
                    </li>
                <#if Session["project_selected"]??>
                    <li>
                        <a href="/node/client_node.htm">客户端节点</a>
                    </li>
                </#if>
                </ul>
            </li>
        <#if Session["project_selected"]??>
            <li>
                <a href="#"><i class="fa fa-random fa-fw"></i> 作业管理<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="/schedule/job_init.htm">作业调度创建</a>
                    </li>
                    <li>
                        <a href="/schedule/job_cron.htm">CRON 作业</a>
                    </li>
                    <li>
                        <a href="/schedule/task_waiting.htm">等待推送任务</a>
                    </li>
                </ul>
            </li>
        </#if>
            <li>
                <a href="#"><i class="fa fa-file-text-o fa-fw"></i> 日志<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                <#if Session["project_selected"]??>
                    <li>
                        <a href="/logger/task_record.htm">任务记录</a>
                    </li>
                    <li>
                        <a href="/logger/log_collect.htm">上报信息</a>
                    </li>
                </#if>
                    <li>
                        <a href="/logger/log_opt.htm">操作日志</a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</div>