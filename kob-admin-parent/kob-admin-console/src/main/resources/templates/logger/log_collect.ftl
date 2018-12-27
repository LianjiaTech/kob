<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>作业管理 - 等待推送任务</h4>
                    </div>
                    <div class="col-lg-3">
                        <button type="button" class="btn btn-success" id="search_button">搜索</button>
                    </div>
                </div>
                <div class="panel-body">
                    <form id="form" method="post" onsubmit="return false">
                        <div class="row">
                            <div class="col-lg-3">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">所属项目</span>
                                    <input class="form-control" value="${Session['project_selected'].projectName} - ${Session['project_selected'].projectCode}" disabled>
                                </div>
                            </div>
                            <div class="col-lg-3 col-lg-offset-1">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">任务标识</span>
                                    <input class="form-control" name="task_uuid" value="${task_uuid}">
                                </div>
                            </div>
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
            <th>&nbsp; &nbsp; &nbsp;日志时间</th>
            <th>上报节点</th>
            <th>上报状态</th>
            <th>任务标识</th>
            <th>日志模式</th>
            <th data-hide="all">项目标识</th>
            <th data-hide="all">日志标识</th>
            <th data-hide="all">回传结果</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.logTime | format:'timestampFormat',row.logTime}}</td>
            <td>{{row.clientIdentification}}</td>
            <td>{{row.state | format:'stateFormat',row.state}}</td>
            <td>{{row.taskUuid}}</td>
            <td>{{row.logMode}}</td>
            <td>{{row.projectCode}}</td>
            <td>{{row.logUuid}}</td>
            <td>{{row.msg}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="14">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="14">
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
            url: '/logger/log_collect_list.json',
            templateId: 'foo_table'
        });

        $(document).on("click", "#search_button", function () {
            var params = {};
            $.each($('#form').parent().find(".form-control"), function () {
                var name = $(this).attr("name");
                var value = $(this).val();
                params[name] = value;
            });
            _table.post(params, 1);
        });
        $("#search_button").trigger("click");
    });
</script>
