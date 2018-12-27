<!DOCTYPE html>
<html lang="cn">
<#include "./layout/header.ftl">

<body>
<div id="wrapper">

    <nav class="navbar navbar-default navbar-static-top" style="margin-bottom:0;">
    <#include "./layout/topbar.ftl">
        <div class="animated fadeIn">
        <#include "./layout/left.ftl">
        </div>
    </nav>

    <div id="page-wrapper">
    <#if Request['index_screen']??>
        <#include "${Request['index_screen']}">
    </#if>
    </div>
</div>

</body>
</html>
