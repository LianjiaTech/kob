<br>
<div class="animated fadeIn">
    <form id="form">
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading" style="min-height: 56px">
                        <div class="col-lg-9">
                            <h4>项目管理 - 个人配置</h4>
                        </div>
                        <div class="col-lg-3">
                            <button type="button" class="btn btn-success" id="saveBtn">保存</button>
                        </div>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="form-group input-group">
                                    <span class="input-group-addon">所属项目</span>
                                    <input class="form-control" value="${Session['project_selected'].projectName} - ${Session['project_selected'].projectCode}" disabled>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">推送任务</span>
                                    <select name="send" class="form-control selectpicker">
                                        <option value="1" <#if config.userConfiguration.send=='1'>selected</#if>>发送</option>
                                        <option value="0" <#if config.userConfiguration.send=='0'>selected</#if>>关闭</option>
                                    </select>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">执行任务</span>
                                    <select name="run" class="form-control selectpicker">
                                        <option value="1" <#if config.userConfiguration.send=='1'>selected</#if>>发送</option>
                                        <option value="0" <#if config.userConfiguration.send=='0'>selected</#if>>关闭</option>
                                    </select>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">执行完成</span>
                                    <select name="end" class="form-control selectpicker">
                                        <option value="1" <#if config.userConfiguration.send=='1'>selected</#if>>发送</option>
                                        <option value="0" <#if config.userConfiguration.send=='0'>selected</#if>>关闭</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<script>
    $(document).ready(function () {
        $(document).on("click", "#saveBtn", function () {
            var params = {};
            $.each($('#form').parent().find(".form-control"), function () {
                var name = $(this).attr("name");
                var value = $(this).val();
                params[name] = value;
            });
            post('/manager/person_config_save.json', params, function (data) {
                if (data && data.success) {
                    swal('保存成功').then(function() {
                        window.location.replace("/manager/person_config.htm");
                    });
                } else {
                    swal('保存失败 ' + data.message);
                }
            });
        });
    });
</script>


