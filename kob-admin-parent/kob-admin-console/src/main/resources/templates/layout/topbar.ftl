<div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="/index.htm">作业平台 KOB</a>
</div>
<ul class="nav navbar-top-links navbar-right">
    <li class="dropdown">
        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
            ${Session['session_user'].name}
            <#if (Session['project_selected'])??>
                — ${Session['project_selected'].projectName} ${Session['project_selected'].projectCode}
            </#if>
            <i class="fa fa-user fa-fw"></i> <i class="fa fa-caret-down"> </i>
        </a>
        <ul class="dropdown-menu dropdown-alerts">
            <#if Session['project_list']?? && (Session['project_list']?size > 0) >
                <#list Session['project_list'] as project>
                    <#if project_index != 0>
                        <li class="divider"></li>
                    </#if>
                    <li>
                        <a href="/change_project.htm?project_code=${project.projectCode}">
                            <div>
                            ${project.projectName} <span class="pull-right text-muted small"> ${project.projectCode}</span>
                            </div>
                        </a>
                    </li>
                </#list>
            </#if>
            <li class="divider"></li>
            <li>
                <a class="text-center" href="/logout.htm">
                    <strong>退出登录</strong>
                    <i class="fa fa-sign-out fa-fw"></i>
                </a>
            </li>
        </ul>
    </li>
</ul>