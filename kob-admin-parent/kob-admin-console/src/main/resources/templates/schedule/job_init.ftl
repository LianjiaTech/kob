<br>
<div class="animated fadeIn">
    <form id="form">
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading" style="min-height: 56px">
                        <div class="col-lg-9">
                            <h4>作业管理 - 作业创建</h4>
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
                                    <span class="input-group-addon">已挂载任务</span>
                                    <select name="task_key" class="form-control selectpicker">
                                        <option value="" data-remark="" selected>请选择 要执行的作业</option>
                                    <#list taskMap?keys as key>
                                        <option value="${key}" data-remark="${taskMap[key]}">${key} - ${taskMap[key]}</option>
                                    </#list>
                                    </select>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">任务备注</span>
                                    <input name="task_remark" class="form-control">
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">作业名称</span>
                                    <input name="job_cn" class="form-control">
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">作业类型</span>
                                    <select name="job_type" class="form-control selectpicker">
                                        <option value="REAL_TIME" selected>实时任务</option>
                                        <option value="CRON">CRON任务</option>
                                    </select>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">预计执行时间</span>
                                    <input name="timeout_threshold" value="60" class="form-control">
                                    <span class="input-group-addon">分钟</span>
                                </div>
                                <div class="form-group input-group kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">重试类型</span>
                                    <select name="retry_type" class="form-control selectpicker">
                                        <option value="NONE" selected>不重试</option>
                                        <option value="FAIL">失败重试</option>
                                    </select>
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">批处理任务</span>
                                    <select name="batch_type" class="form-control selectpicker" disabled>
                                        <option value="NONE" selected>关闭</option>
                                        <option value="DAG">有序无环图（DAG）</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-lg-1"></div>
                            <div class="col-lg-5">
                                <div class="form-group input-group kob-rt-gp kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">追加重试任务次数</span>
                                    <input name="retry_count" class="form-control" value="1" disabled>
                                    <span class="input-group-addon">次</span>
                                </div>
                                <div class="form-group input-group kob-rt-gp kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">失败节点迁移</span>
                                    <select name="failover" class="form-control selectpicker">
                                        <option value="1">尝试迁移</option>
                                        <option value="0" selected>关闭</option>
                                    </select>
                                </div>
                                <div class="form-group input-group kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">是否依赖上一周期</span>
                                    <select name="rely" class="form-control selectpicker">
                                        <option value="0" selected>否</option>
                                        <option value="1">是</option>
                                    </select>
                                </div>
                                <div class="form-group input-group kob-jtr-gp kob-an-gp kob-an-">
                                    <span class="input-group-addon">指定客户端节点</span>
                                    <select name="designated_node" class="form-control selectpicker">
                                        <option value="">&nbsp;</option>
                                    </select>
                                </div>
                            <#list taskNodeMap?keys as key>
                                <div class="form-group input-group kob-jtr-gp kob-an-gp kob-an-${key}" style="display: none">
                                    <span class="input-group-addon">指定客户端节点</span>
                                    <select name="designated_node" class="form-control selectpicker">
                                        <#list taskNodeMap[key] as client>
                                            <option value="${client}">${client}</option>
                                        </#list>
                                        <option value="RANDOM">随机路由至过载保护</option>
                                    </select>
                                </div>
                            </#list>
                                <div class="form-group input-group kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">路由规则</span>
                                    <select name="load_balance" class="form-control selectpicker">
                                        <option value="NODE_HASH">节点哈希推荐节点执行</option>
                                        <option value="RANDOM">随机路由至过载保护</option>
                                    </select>
                                </div>
                                <div class="form-group input-group kob-jtc-gp" style="display: none">
                                    <span class="input-group-addon">CRON 表达式</span>
                                    <input name="cron_expression" class="form-control" placeholder="例如:0 */30 * * * ? , 三十分钟执行一次">
                                </div>
                                <div class="form-group input-group">
                                    <span class="input-group-addon">自定义参数</span>
                                    <input name="user_params" class="form-control" placeholder="【非必填】 JSON格式, 例如:{&#34;k1&#34;:&#34;v1&#34;,&#34;k2&#34;:&#34;v2&#34;}">
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
        $(document).on("change", "select[name='task_key']", function () {
            var remark = $(this).find("option:selected").data('remark');
            $("input[name='task_remark']").val(remark);
            $("input[name='job_cn']").val(remark+'作业');
            if("REAL_TIME"==$("select[name='job_type']").val()){
                $(".kob-an-gp").hide();
                $(".kob-an-"+$(this).val()).show();
            }
        });
        $(document).on("change", "select[name='retry_type']", function () {
            $(this).val() =='NONE' ? $(".kob-rt-gp").hide() : $(".kob-rt-gp").show();
        });
        $(document).on("change", "select[name='job_type']", function () {
            var _job_type = $(this).val();
            if(_job_type=="CRON"){
                $(".kob-jtc-gp").show();
                $(".kob-jtr-gp").hide();
                $("select[name='retry_type']").val() =='NONE' ? $(".kob-rt-gp").hide() : $(".kob-rt-gp").show();
            }
            if(_job_type=="REAL_TIME"){
                $(".kob-jtc-gp").hide();
                $(".kob-jtr-gp").show();
                var _task_key = $("select[name='task_key']").find("option:selected").val();
                $(".kob-an-gp").hide();
                $(".kob-an-"+_task_key).show();
            }
        });
        $(document).on("click", "#saveBtn", function () {
            var params = {};
            $.each($('#form').parent().find(".form-control"), function () {
                var name = $(this).attr("name");
                var value = $(this).val();
                params[name] = value;
            });
            var _task_key = $("select[name='task_key']").find("option:selected").val();
            var _designated_node = $(".kob-an-"+_task_key).find("option:selected").val();
            params['designated_node'] = _designated_node;
            post('/schedule/job_add.json', params, function (data) {
                if (data && data.success) {
                    swal('保存成功');
                } else {
                    swal('保存失败 ' + data.message);
                }
            });
        });
    });
</script>


