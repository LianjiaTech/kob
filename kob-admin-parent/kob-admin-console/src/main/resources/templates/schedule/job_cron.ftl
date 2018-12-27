<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>作业管理 - CRON 作业</h4>
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
            <th data-toggle="true">&nbsp; &nbsp; &nbsp;作业名称</th>
            <th>任务标识</th>
            <th>任务备注</th>
            <th>CRON表达式</th>
            <th>路由规则</th>
            <th>上一周期</th>
            <th>批处理</th>
            <th>状态</th>
            <th>操作</th>
            <th data-hide="all">所属项目</th>
            <th data-hide="all">作业标识</th>
            <th data-hide="all">重试类型</th>
            <th data-hide="all">重试次数</th>
            <th data-hide="all">超时阈值</th>
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
            <td>{{row.cronExpression}}</td>
            <td>{{row.loadBalance | format:'loadBalanceFormat',row.loadBalance}}</td>
            <td>{{row.rely | format:'relyFormat',row.rely}}</td>
            <td>{{row.batchType | format:'batchTypeFormat',row.batchType}}</td>
            <td>{{row.suspend | format:'suspendFormat',row.suspend}}</td>
            <td>{{row.opt | format:'optFormat',row}}</td>
            <td>{{row.projectName}} - {{row.projectCode}}</td>
            <td>{{row.jobUuid}}</td>
            <td>{{row.retryType | format:'retryTypeFormat',row.retryType}}</td>
            <td>{{row.retryCount}}</td>
            <td>{{row.timeoutThreshold}}分钟</td>
            <td>{{row.gmtCreated | format:'timestampFormat',row.gmtCreated}}</td>
            <td>{{row.userParams}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="16">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="16">
                <span>共{{results}}条记录，每页展示{{pageSize}}条</span>
                <ul class="pagination-sm pull-right"></ul>
            </td>
        </tr>
        </tfoot>
    </table>
</script>
<div class="modal" id="editFrame" role="dialog" style="display: none;">
    <div class="modal-dialog" style="min-width: 660px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span>×</span></button>
                <h2 class="modal-title">编辑CRON作业信息</h2>
            </div>
            <div class="modal-body">
                </br>
                <input name="job_uuid" class="form-control hidden" value="">
                <div class="row">
                    <div class="col-lg-offset-1 col-lg-9">
                        <div class="form-group input-group">
                            <span class="input-group-addon">任务备注</span>
                            <input name="task_remark" class="form-control" value="">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-offset-1 col-lg-9">
                        <div class="form-group input-group">
                            <span class="input-group-addon">CRON 表达式</span>
                            <input name="cron_expression" class="form-control" placeholder="例如:0 */30 * * * ? , 三十分钟执行一次">
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-offset-1 col-lg-9">
                        <div class="form-group input-group">
                            <span class="input-group-addon">自定义参数</span>
                            <input name="user_params" class="form-control" placeholder="【非必填】 JSON格式, 例如:{&#34;k1&#34;:&#34;v1&#34;,&#34;k2&#34;:&#34;v2&#34;}">
                        </div>
                    </div>
                </div>
                </br>
                <div class="row">
                    <div class="form-group">
                        <div class="col-sm-1 col-sm-offset-3" style="width:150px;">
                            <button class="btn btn-success" type="button" id="editBtn">
                                编辑
                            </button>
                        </div>
                        <div class="col-sm-1">
                            <button class="btn btn-warning" data-dismiss="modal">关闭</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        FMT.colFormatter.optFormat = function (v, row) {
            var opt_0 = '<a target="_blank" href="/logger/task_record.htm?job_uuid='+row.jobUuid+'"><span class="label label-info"><i class="fa fa-file-code-o"></i> 日志</span></a> ' +
                    '<a href="javascript:;" class="kob-opt-edit"><span class="label label-success"><i class="fa fa-edit"></i> 编辑</span><span class="hidden row-data">'+JSON.stringify(row)+'</span></a> ';
            if(row.suspend){
                opt_0 = opt_0 + '<a href="javascript:;" class="kob-opt-suspend" data-job_uuid="'+row.jobUuid+'" data-suspend="'+row.suspend+'"><span class="label label-primary"><i class="fa fa-unlock-alt"></i> 运行</span></a> ';
            } else {
                opt_0 = opt_0 + '<a href="javascript:;" class="kob-opt-suspend" data-job_uuid="'+row.jobUuid+'" data-suspend="'+row.suspend+'"><span class="label label-warning"><i class="fa fa-lock"></i> 暂停</span></a> ';
            }
            return opt_0 + '<a href="javascript:;" class="kob-opt-del" data-job_uuid="'+row.jobUuid+'"><span class="label label-danger"><i class="fa fa-trash-o"></i> 删除</span></a>';
        }

        var _table = $("#tableContainer").fooTable({
            url: '/schedule/job_cron_list.json',
            templateId: 'foo_table'
        });
        _table.post({}, 1);

        $(document).on("click", ".kob-opt-suspend", function () {
            var params = {};
            params['job_uuid'] = $(this).data('job_uuid');
            params['suspend'] = $(this).data('suspend');
            post('/schedule/job_suspend_opt.json', params, function (data) {
                if (data && data.success) {
                    swal('操作成功');
                    _table.post({}, 1);
                } else {
                    swal('操作失败 ' + data.message);
                }
            });
        });

        $(document).on("click", ".kob-opt-del", function () {
            var params = {};
            params['job_uuid'] = $(this).data('job_uuid');
            post('/schedule/job_del_opt.json', params, function (data) {
                if (data && data.success) {
                    swal('操作成功');
                    _table.post({}, 1);
                } else {
                    swal('操作失败 ' + data.message);
                }
            });
        });

        $(document).on("click", ".kob-opt-edit", function () {
            var _job = JSON.parse($(this).children("span.row-data").text());
            $.each($('#editFrame').find(".form-control"), function () {
                var name = toHump($(this).attr("name"));
                $(this).val(_job[name]);
            });
            $("#editFrame").modal("show");
        });

        $(document).on("click", "#editBtn", function () {
            var params = {};
            $.each($('#editFrame').find(".form-control"), function () {
                var name = $(this).attr("name");
                params[name] = $(this).val();
            });
            post('/schedule/job_edit_opt.json', params, function (data) {
                if (data && data.success) {
                    swal('操作成功');
                    $("#editFrame").modal("hide");
                    _table.post({}, 1);
                } else {
                    swal('操作失败 ' + data.message);
                }
            });
        });
    });
</script>
