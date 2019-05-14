</br>
<div class="row">
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-black">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-calendar-o fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">&nbsp;</div>
                        <div>昨日任务 总量：<#if tomorrow_record_count??>${tomorrow_record_count}</#if></div>
                    </div>
                </div>
            </div>
            <a <#if Session["project_selected"]??>href="/logger/task_record.htm"</#if>>
                <div class="panel-footer">
                    <span class="pull-left">点击 进入日志记录</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-black">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-leaf fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">&nbsp;</div>
                        <div>zookeeper状况：正常</div>
                    </div>
                </div>
            </div>
            <a href="#">
                <div class="panel-footer">
                    <span class="pull-left">点击 进入zookeeper管理</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-black">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-comments fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">&nbsp;</div>
                        <div>交流群: 837756322</div>
                    </div>
                </div>
            </div>
            <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=eb52e2e36d579a46db28b7373e8744b009bb3c691ace0f8376e3fc140863bb29">
                <div class="panel-footer">
                    <span class="pull-left">点击 加入QQ群</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-black">
            <div class="panel-heading" >
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-github fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">&nbsp;</div>
                        <div>Github 源码&文档</div>
                    </div>
                </div>
            </div>
            <a href="https://github.com/incubator-kob/kob-parent" target="_blank">
                <div class="panel-footer">
                    <span class="pull-left">点击 跳转github</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <i class="fa fa-rss fa-fw"></i> 项目简介
            </div>
            <div class="panel-body">
                <div id="morris-area-chart">
                    致力于公司级微服务项目集群接入并完成作业调度模块的快速解决方案。
                </div>
            </div>
        </div>
    </div>

    <div class="col-lg-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <i class="fa fa-map-o fa-fw"></i> 已接入 ${project?size}个项目概览
            </div>
            <div class="panel-body">
                <div class="list-group">
                <#list project as p>
                    <a href="#" class="list-group-item">
                        ${p.projectName} &nbsp;${p.projectCode}
                        <span class="pull-right text-muted small"> ${p.userName}</span>
                    </a>
                </#list>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <i class="fa fa-cogs fa-fw"></i> 环境信息
            </div>
            <div class="panel-body">
                <div class="panel-group" id="version">
                    <p>
                        <i class="fa fa-check-circle-o"></i> 生产环境：<a href="http://localhost:8669" target="_blank"> online_url</a><br/>
                        <ul>
                            <li>集群名称：incubator</li>
                            <li>zk地址：online_url:2801</li>
                        </ul>
                        <i class="fa fa-ban"></i> 线下环境：<a href="" target="_blank"> offline_url</a><br/>
                        <ul>
                            <li>集群名称：incubator</li>
                            <li>zk地址：offline_url:2801</li>
                        </ul>
                    </p>
                </div>
            </div>
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">
                <i class="fa fa-coffee fa-fw"></i> 客户端版本
            </div>
            <div class="panel-body">
                <div class="panel-group" id="version">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h5 class="panel-title">
                                <a data-toggle="collapse" data-parent="#version" href="#version_0_1_0_M0">0.1.0-M0</a>
                            </h5>
                        </div>
                        <div id="version_0_1_0_M0" class="panel-collapse collapse in">
                            <div class="panel-body">
                                <div class="alert alert-warning">此版本是初始化版本。</div>
                                <ol>
                                    <li>初始化版本</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>