<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>作业管理 - 操作日志</h4>
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
                                    <span class="input-group-addon">耗时</span>
                                    <input class="form-control" name="cost_time" value="">
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
            <th>&nbsp; &nbsp; &nbsp;用户名称</th>
            <th>url</th>
            <th>耗时</th>
            <th>操作时间</th>
            <th data-hide="all">request</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.userName}}</td>
            <td>{{row.optUrl}}</td>
            <td>{{row.costTime}}ms</td>
            <td>{{row.gmtCreated | format:'timestampFormat',row.gmtCreated}}</td>
            <td>{{row.request}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="5">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="5">
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
            url: '/logger/log_opt_list.json',
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
