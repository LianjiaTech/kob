<br>
<div class="animated fadeIn">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" style="min-height: 56px">
                    <div class="col-lg-9">
                        <h4>项目管理 - 项目接入</h4>
                    </div>
                    <div class="col-lg-3">
                        <button type="button" class="btn btn-success" id="saveBtn">保存</button>
                    </div>
                </div>
                <div class="panel-body">
                    <form method="post" id="form" onsubmit="return false">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">项目Master姓名</span>
                                    <input class="form-control" value=${Session['session_user'].name} disabled>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">项目Master账号</span>
                                    <input class="form-control" value=${Session['session_user'].code} disabled>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">项目Master邮箱</span>
                                    <input class="form-control" value=${Session['session_user'].userConfiguration.mail} disabled>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">接入项目标识</span>
                                    <input class="form-control" placeholder="注意正则  ([A-Z]|[a-z]|_){6,60}" name="project_code">
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">接入项目名称</span>
                                    <input class="form-control" placeholder="建议使用中文  length < 60" name="project_name">
                                </div>
                            </div>
                            <div class="col-lg-7"></div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        $(document).on("click", "#saveBtn", function () {
            var _params = {};
            $.each($('#form').parent().find(".form-control"), function () {
                var _name = $(this).attr("name");
                var _value = $(this).val();
                _params[_name] = _value;
            });
            post('/manager/save_project_access.json', _params, function (data) {
                if (data && data.success) {
                    swal({
                        title: '保存成功',
                        timer: 2000,
                    }).then(function() {
                        window.location.replace("/change_project.htm?project_code="+_params['project_code']);
                    });
                } else {
                    swal('保存失败 ' + data.message);
                }
            });
        });
    });
</script>
