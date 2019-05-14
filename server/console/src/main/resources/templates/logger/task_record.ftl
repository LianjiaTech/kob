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
                                    <span class="input-group-addon">触发时间（启）</span>
                                    <input class="form-control" name="trigger_time_start" value="${trigger_time_start}">
                                </div>
                            </div>
                            <div class="col-lg-3 col-lg-offset-1">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">触发时间（止）</span>
                                    <input class="form-control" name="trigger_time_end" value="${trigger_time_end}">
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-3">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">作业标识</span>
                                    <input class="form-control" name="job_uuid" value="${job_uuid}">
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
            <th>任务标识</th>
            <th>任务备注</th>
            <th>执行节点</th>
            <th>触发时间</th>
            <th>完成时间</th>
            <th>任务状态</th>
            <th>完结</th>
            <th>操作</th>
            <th data-hide="all">所属项目</th>
            <th data-hide="all">作业标识</th>
            <th data-hide="all">任务标识</th>
            <th data-hide="all">关联任务标识</th>
            <th data-hide="all">指定客户端节点</th>
            <th data-hide="all">推荐客户端节点</th>
            <th data-hide="all">依赖未完成的任务标识</th>
            <th data-hide="all">作业类型</th>
            <th data-hide="all">任务类型</th>
            <th data-hide="all">CRON表达式</th>
            <th data-hide="all">路由规则</th>
            <th data-hide="all">故障转移</th>
            <th data-hide="all">上一周期</th>
            <th data-hide="all">重试类型</th>
            <th data-hide="all">重试次数</th>
            <th data-hide="all">超时阈值</th>
            <th data-hide="all">批处理</th>
            <th data-hide="all">创建时间</th>
            <th data-hide="all">用户参数</th>
            <th data-hide="all">最后返回信息</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.jobCn}}</td>
            <td>{{row.taskKey}}</td>
            <td>{{row.taskRemark}}</td>
            <td>{{row.clientIdentification}}</td>
            <td>{{row.triggerTime | format:'timestampFormat',row.triggerTime}}</td>
            <td>{{row.executeEndTime | format:'timestampFormat',row.executeEndTime}}</td>
            <td>{{row.state | format:'stateFormat',row.state}}</td>
            <td>{{row.complete | format:'completeFormat',row.complete}}</td>
            <td>{{row.opt | format:'optFormat',row}}</td>
            <td>{{row.projectName}} - {{row.projectCode}}</td>
            <td>{{row.jobUuid}}</td>
            <td>{{row.taskUuid}}</td>
            <td>{{row.relationTaskUuid}}</td>
            <td>{{row.innerParams | format:'designatedNodeFormat',row.innerParams}}</td>
            <td>{{row.innerParams | format:'recommendNodeFormat',row.innerParams}}</td>
            <td>{{row.innerParams | format:'relyUndoTaskUuidFormat',row.innerParams}}</td>
            <td>{{row.jobType | format:'jobTypeFormat',row.jobType}}</td>
            <td>{{row.taskType | format:'taskTypeFormat',row.taskType}}</td>
            <td>{{row.cronExpression}}</td>
            <td>{{row.loadBalance | format:'loadBalanceFormat',row.loadBalance}}</td>
            <td>{{row.failover | format:'failoverFormat',row.failover}}</td>
            <td>{{row.rely | format:'relyFormat',row.rely}}</td>
            <td>{{row.retryType | format:'retryTypeFormat',row.retryType}}</td>
            <td>{{row.retryCount}}</td>
            <td>{{row.timeoutThreshold}}分钟</td>
            <td>{{row.batchType | format:'batchTypeFormat',row.batchType}}</td>
            <td>{{row.gmtCreated | format:'timestampFormat',row.gmtCreated}}</td>
            <td>{{row.userParams}}</td>
            <td>{{row.msg}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="24">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="24">
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
            return '<a target="_blank" href="/logger/log_collect.htm?task_uuid='+row.taskUuid+'"><span class="label label-info"><i class="fa fa-file-code-o"></i> 详细</span></a> ';
        }

        var _table = $("#tableContainer").fooTable({
            url: '/logger/task_record_list.json',
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
