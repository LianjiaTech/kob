var FMT = {
    colFormatter: {}
};

FMT.colFormatter.timestampFormat = function (v) {
    return v ? timestampToTime(v) : v;
};

FMT.colFormatter.jobTypeFormat = function (v) {
    return v == 'CRON'? 'CRON任务' : '实时任务';
};

FMT.colFormatter.taskTypeFormat = function (v) {
    return v == 'RETRY_FAIL'? '失败追加重试任务' : '正常';
};

FMT.colFormatter.loadBalanceFormat = function (v) {
    return v == 'RANDOM'? '随机路由' : (v == 'NODE_HASH'? '节点哈希' : ' ');
};

FMT.colFormatter.failoverFormat = function (v) {
    return v ? '尝试迁移' : '关闭';
};

FMT.colFormatter.relyFormat = function (v) {
    return v ? '依赖' : '不依赖';
};

FMT.colFormatter.batchTypeFormat = function (v) {
    return v == 'NONE'? '关闭' : '开启';
};

FMT.colFormatter.suspendFormat = function (v) {
    return v ? '暂停' : '运行中';
};

FMT.colFormatter.retryTypeFormat = function (v) {
    if(v == 'NONE'){
        return '关闭';
    }
    if(v == 'FAIL'){
        return '失败重试';
    }
};

FMT.colFormatter.stateFormat = function (v) {
    if(v == '10'){ return '等待推送'; }
    if(v == '11'){ return '依赖未完成'; }
    if(v == '30'){ return '推送成功'; }
    if(v == '31'){ return '推送失败'; }
    if(v == '32'){ return '积压回收'; }
    if(v == '50'){ return '接收任务'; }
    if(v == '51'){ return '开始执行'; }
    if(v == '52'){ return '超时回收'; }
    if(v == '70'){ return '执行成功'; }
    if(v == '71'){ return '执行失败'; }
    if(v == '72'){ return '执行异常'; }
    if(v == '73'){ return '执行过期'; }
    return '解析失败';
};

FMT.colFormatter.completeFormat = function (v) {
    if(v){ return '是'; }
    return '否';
};

FMT.colFormatter.designatedNodeFormat = function (v) {
    return JSON.parse(v)['designatedNode'];
};

FMT.colFormatter.recommendNodeFormat = function (v) {
    return JSON.parse(v)['recommendNode'];
};

FMT.colFormatter.relyUndoTaskUuidFormat = function (v) {
    return JSON.parse(v)['relyUndoTaskUuid'];
};

template.defaults.escape = false;

template.helper('format', function (v, colFormatter, row) {
    var formatterFn = FMT.colFormatter[colFormatter];
    return formatterFn ? formatterFn(v, row) : obj;
});

/**
 * 封装的分页表格
 */
function FooTable(options) {
    this.cachedParams = {};
    this.container = options.container;
    this.pageSize = options.pageSize || 10;
    this.templateId = options.templateId;
    this.url = options.url;
    var _this = this;

    _this.render = function (rows, results, params, curPage) {
        var html = template(_this.templateId, {rows: rows, results: results, pageSize: _this.pageSize});
        _this.container.html(html);
        _this.container.children('table').footable();

        if (results == 0) results = 1;
        _this.container.find(".pagination-sm").twbsPagination({
            totalPages: (results % _this.pageSize == 0) ? results / _this.pageSize : results / _this.pageSize + 1,
            visiblePages: 10,
            startPage: curPage,
            first: '«',
            prev: '‹',
            next: '›',
            last: '»',
            onPageClick: function (event, page) {
                _this.post(_this.cachedParams, page);
            }
        });
    };

    _this.post = function (params, curPage) {
        params['start'] = (curPage - 1) * _this.pageSize;
        params['limit'] = _this.pageSize;
        post(_this.url, params, function (data) {
            if (data && data.success) {
                _this.cachedParams = params;
                var results = data['results'];
                var rows = data['rows'];
                _this.render(rows, results, params, curPage);
            } else {
                swal(data['message']);
            }
        });
    };
}

jQuery.fn.extend({
    fooTable: function (options) {
        return new FooTable($.extend({}, options, {container: $(this)}));
    }
});

function post(url, params, callback) {
    $.ajax({
        url: url,
        type: 'POST',
        dataType: 'json',
        data: params,
        async:false,
        success: function (data) {
            callback(data);
        }
    });
}

function timestampToTime(timestamp) {
    var date = new Date(timestamp);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes()) + ':';
    var s = date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds();
    return Y+M+D+h+m+s;
}

// 下划线转换驼峰
function toHump(name) {
    return name.replace(/\_(\w)/g, function(all, letter){
        return letter.toUpperCase();
    });
}

// 驼峰转换下划线
function toLine(name) {
    return name.replace(/([A-Z])/g,"_$1").toLowerCase();
}








