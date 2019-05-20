<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>节点信息 - 服务端节点</h4>
                    </div>
                </div>
                <div class="panel-body">
                    <form id="form" method="post" onsubmit="return false">
                        <div class="row">
                            <div class="col-lg-3">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">zk 前缀</span>
                                    <input class="form-control" value="kob" disabled>
                                </div>
                            </div>
                            <div class="col-lg-1"></div>
                            <div class="col-lg-3">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">mysql 前缀</span>
                                    <input class="form-control" value="kob" disabled>
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
            <th data-toggle="true">节点标识</th>
            <th>集群名称</th>
            <th>节点IP</th>
            <th>创建时间</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.identification}}</td>
            <td>{{row.zp}}</td>
            <td>{{row.ip}}</td>
            <td>{{row.created | format:'timestampFormat',row.created}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="4">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="4">
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
            url: '/node/server_node_list.json',
            templateId: 'foo_table',
            pageSize: 100
        });
        _table.post({}, 1);
    });
</script>
