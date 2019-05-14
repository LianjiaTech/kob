<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>项目管理 - 人员管理</h4>
                    </div>
                    <div class="col-lg-3">
                        <button type="button" class="btn btn-success" id="inviteBtn">邀请</button>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-lg-3">
                            <div class="form-group input-group">
                                <span class="input-group-addon">所属项目</span>
                                <input name="master" value="<#if Session['project_selected'].owner>master</#if>" hidden>
                                <input name="project_code" class="form-control" value="${Session['project_selected'].projectName} - ${Session['project_selected'].projectCode}" disabled>
                            </div>
                        </div>
                    </div>
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
            <th data-toggle="true">&nbsp; &nbsp; &nbsp;项目名称</th>
            <th>用户标识</th>
            <th>姓名</th>
            <th>角色</th>
            <th>操作</th>
            <th data-hide="all">配置</th>
        </tr>
        </thead>
        <tbody>
        {{each rows as row index}}
        <tr>
            <td>{{row.projectName}}</td>
            <td>{{row.userCode}}</td>
            <td>{{row.userName}}</td>
            <td>{{row.owner | format:'ownerFormat',row.owner}}</td>
            <td>{{row.opt | format:'optFormat',row}}</td>
            <td>{{row.configuration}}</td>
        </tr>
        {{/each}}
        {{if results == 0}}
        <tr>
            <td colspan="6">暂无数据</td>
        </tr>
        {{/if}}
        </tbody>
        <tfoot>
        <tr>
            <td colspan="6">
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
            url: '/manager/project_user_list.json',
            templateId: 'foo_table'
        });
        FMT.colFormatter.ownerFormat = function (v, owner) {
            return owner ? 'owner' : 'developer';
        };
        var master = $("input[name='master']").val();
        FMT.colFormatter.optFormat = function (v, row) {
            return (!row.owner && master) ? '<a href="javascript:;" class="kob-opt-user-del-btn" data-id="'+row.id+'"><span class="label label-warning"><i class="fa fa-trash-o"></i> 删除</span></a>' : '';
        }
        $(document).on("click", "#inviteBtn", function () {
            $("#modalFrame").modal("show");
        });
        _table.post({}, 1);
        $(document).on("click", "#saveBtn", function () {
            var _params = {};
            _params['user_code'] = $("input[name='user_code']").val();
            post('/manager/project_user_invite.json', _params, function (data) {
                if (data && data.success) {
                    $("#modalFrame").modal("hide");
                    swal('保存成功');
                    _table.post({}, 1);
                } else {
                    swal('保存失败 ' + data.message);
                }
            });
        });
        $(document).on("click", ".kob-opt-user-del-btn", function () {
            var _params = {};
            _params['id'] = $(this).data("id");
            post('/manager/project_user_delete.json', _params, function (data) {
                if (data && data.success) {
                    swal('删除成功');
                    _table.post({}, 1);
                } else {
                    swal('删除失败 ' + data.message);
                }
            });
        });
    });
</script>


<div class="modal" id="modalFrame" role="dialog" style="display: none;">
    <div class="modal-dialog" style="min-width: 260px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span>×</span></button>
                <h2 class="modal-title">邀请用户加入</h2>
            </div>
            <div class="modal-body">
                <form>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="ibox">
                                </br>
                                <div class="form-group" style="-moz-box-sizing: border-box;margin-left: -45px; margin-bottom: 15px; min-height: 34px">
                                    <label class="col-sm-4" style="padding-top: 7px;text-align:right;">用户标识: </label>
                                    <div class="col-sm-6">
                                        <input class="form-control" name="user_code" placeholder="请输入 用户标识 如：xiaohong">
                                    </div>
                                </div>
                                </br>
                                <div class="form-group">
                                    <div class="col-sm-1 col-sm-offset-4" style="width:70px;">
                                        <button class="btn btn-primary" type="button" id="saveBtn">
                                            添加
                                        </button>
                                    </div>
                                    <div class="col-sm-1">
                                        <button class="btn btn-warning" type="reset">
                                            重置
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>