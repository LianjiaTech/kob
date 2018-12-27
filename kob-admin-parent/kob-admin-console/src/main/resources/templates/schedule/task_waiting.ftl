<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>作业管理 - 等待推送任务</h4>
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
            <th>&nbsp; &nbsp; &nbsp;作业名称</th>
            <th>挂载任务</th>
            <th>任务备注</th>
            <th>作业类型</th>
            <th>CRON表达式</th>
            <th>触发时间</th>
            <th>操作</th>
            <th data-hide="all">所属项目</th>
            <th data-hide="all">任务标识</th>
            <th data-hide="all">路由规则</th>
            <th data-hide="all">故障转移</th>
            <th data-hide="all">上一周期</th>
            <th data-hide="all">重试类型</th>
            <th data-hide="all">重试次数</th>
            <th data-hide="all">超时阈值</th>
            <th data-hide="all">批处理</th>
            <th data-hide="all">创建时间</th>
            <th data-hide="all">用户参数</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.jobCn}}</td>
            <td>{{row.taskKey}}</td>
            <td>{{row.taskRemark}}</td>
            <td>{{row.jobType | format:'jobTypeFormat',row.jobType}}</td>
            <td>{{row.cronExpression}}</td>
            <td>{{row.triggerTime | format:'timestampFormat',row.triggerTime}}</td>
            <td>{{row.opt | format:'optFormat',row}}</td>
            <td>{{row.projectName}} - {{row.projectCode}}</td>
            <td>{{row.taskUuid}}</td>
            <td>{{row.loadBalance | format:'loadBalanceFormat',row.loadBalance}}</td>
            <td>{{row.failover | format:'failoverFormat',row.failover}}</td>
            <td>{{row.rely | format:'relyFormat',row.rely}}</td>
            <td>{{row.retryType | format:'retryTypeFormat',row.retryType}}</td>
            <td>{{row.retryCount}}</td>
            <td>{{row.timeoutThreshold}}分钟</td>
            <td>{{row.batchType | format:'batchTypeFormat',row.batchType}}</td>
            <td>{{row.gmtCreated | format:'timestampFormat',row.gmtCreated}}</td>
            <td>{{row.userParams}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="18">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="18">
                <span>共{{results}}条记录，每页展示{{pageSize}}条</span>
                <ul class="pagination-sm pull-right"></ul>
            </td>
        </tr>
        </tfoot>
    </table>
</script>

<script>
    $(document).ready(function () {
        FMT.colFormatter.optFormat = function (v, row) {
            return '<a href="javascript:;" class="kob-opt-trigger" data-task_uuid="'+row.taskUuid+'"><span class="label label-primary"><i class="fa fa-play-circle-o"></i> 触发</span></a> '
                    + '<a href="javascript:;" class="kob-opt-del" data-task_uuid="'+row.taskUuid+'"><span class="label label-danger"><i class="fa fa-trash-o"></i> 删除</span></a>';
        }

        var _table = $("#tableContainer").fooTable({
            url: '/schedule/task_waiting_list.json',
            templateId: 'foo_table'
        });
        _table.post({}, 1);

        $(document).on("click", ".kob-opt-trigger", function () {
            var params = {};
            params['taskUuid'] = $(this).data('task_uuid');
            post('/schedule/task_trigger_opt.json', params, function (data) {
                if (data && data.success) {
                    swal({
                        title: '操作成功',
                        timer: 2000,
                    }).then(function() {
                        _table.post({}, 1);
                    });
                } else {
                    swal('操作失败 ' + data.message);
                }
            });
        });

        $(document).on("click", ".kob-opt-del", function () {
            var params = {};
            params['taskUuid'] = $(this).data('task_uuid');
            post('/schedule/task_del_opt.json', params, function (data) {
                if (data && data.success) {
                    swal({
                        title: '操作成功',
                        timer: 2000,
                    }).then(function() {
                        _table.post({}, 1);
                    });
                } else {
                    swal('操作失败 ' + data.message);
                }
            });
        });
    });
</script>
