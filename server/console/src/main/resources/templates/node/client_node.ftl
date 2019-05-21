<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>节点信息 - 客户端节点</h4>
                    </div>
                    <div class="col-lg-3">
                        <button type="button" class="btn btn-warning" id="refreshBtn">刷新</button>
                    </div>
                </div>
                <div class="panel-body">
                    <form id="form" method="post" onsubmit="return false">
                        <div class="row">
                            <div class="col-lg-3">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">所属项目</span>
                                    <input name="project_code" class="form-control" value="${Session['project_selected'].projectName} - ${Session['project_selected'].projectCode}" disabled>
                                </div>
                            </div>
                            <div class="col-lg-1"></div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <br>
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    列表信息
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-lg-12">
                            <div id="tableContainer">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script id="foo_table" type="text/html">
    <table class="table table-stripped toggle-circle-filled footable">
        <thead>
        <tr>
            <th data-toggle="true">&nbsp; &nbsp; &nbsp;节点标识</th>
            <th>正在工作线程数</th>
            <th>总工作线程数</th>
            <th>挂载作业数量</th>
            <th>项目启动时间</th>
            <th>项目最后心跳时间</th>
            <th data-hide="all">项目标识</th>
            <th data-hide="all">客户端IP</th>
            <th data-hide="all">客户端版本</th>
            <#--<th data-hide="all">客户端过期回收阈值(秒)</th>-->
            <#--<th data-hide="all">工作线程池负载因子</th>-->
            <#--<th data-hide="all">心跳周期</th>-->
            <#--<th data-hide="all">LOG WARN开关</th>-->
            <th data-hide="all">挂载作业详情</th>
            <th data-hide="all">正在执行的任务</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.clientPath.identification}}</td>
            <td>{{row.clientData.workers}}</td>
            <td>{{row.clientData.threads}}</td>
            <td>{{row.clientPath.taskSize}}</td>
            <td>{{row.clientData.created | format:'timestampFormat',row.clientData.created}}</td>
            <td>{{row.clientData.modified | format:'timestampFormat',row.clientData.modified}}</td>
            <td>{{row.clientData.projectCode}}</td>
            <td>{{row.clientData.ip}}</td>
            <td>{{row.clientData.version}}</td>
            <#--<td>{{row.clientData.expireRecyclingTime}}</td>-->
            <#--<td>{{row.clientData.loadFactor}}</td>-->
            <#--<td>{{row.clientData.heartbeatPeriod}}</td>-->
            <#--<td>{{row.clientData.logWarnEnable}}</td>-->
            <td>{{row.clientPath.tasksDesc}}</td>
            <td>{{row.clientData.runningTaskDesc}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="17">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="17">
                <span>共{{results}}条记录，每页展示{{pageSize}}条</span>
                <ul class="pagination-sm pull-right"></ul>
            </td>
        </tr>
        </tfoot>
    </table>
</script>

<script>
    $(document).ready(function () {
        var _table = $("#tableContainer").fooTable({
            url: '/node/client_node_list.json',
            templateId: 'foo_table',
            pageSize: 100
        });

        $(document).on("click", "#refreshBtn", function () {
            _table.post({}, 1);
        });
        $("#refreshBtn").trigger("click");
    });
</script>
