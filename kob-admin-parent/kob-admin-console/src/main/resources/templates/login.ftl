<!DOCTYPE html>
<html lang="cn">
<#include "./layout/header.ftl">

<body>
<div class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4">
            <div class="login-panel panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">欢迎使用 作业平台 KOB</h3>
                </div>
                <div class="panel-body">
                    <form method="post" id="form" onsubmit="return false">
                        <fieldset>
                            <div class="form-group">
                                <input class="form-control" placeholder="账号" name="code" value="xiaoming" autofocus>
                            </div>
                            <div class="form-group">
                                <input class="form-control" placeholder="密码" name="pwd" type="password" value="xiaoming">
                            </div>
                            <button class="btn btn-success btn-block" id="loginBtn">登陆</button>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        $(document).on("click", "#loginBtn", function () {
            var _params = {};
            $.each($('#form').parent().find(".form-control"), function () {
                var _name = $(this).attr("name");
                var _value = $(this).val();
                _params[_name] = _value;
            });
            post('/login.json', _params, function (data) {
                if (data && data.success) {
                    window.location.replace("/index.htm");
                } else {
                    swal('登陆失败 ' + data.message);
                }
            });
        });
    });
</script>
</body>

</html>
