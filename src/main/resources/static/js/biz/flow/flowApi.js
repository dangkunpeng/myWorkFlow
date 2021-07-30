/**
 * 权限管理
 */
var pageCurr;
var form;

$(function () {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;

        tableIns = table.render({
            id: 'bizLineId',
            elem: '#tableList',
            url: '/flowApi/pageList',
            method: 'post', //默认：get请求
            cellMinWidth: 80,
            page: true,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response: {
                statusName: 'code', //数据状态的字段名称，默认：code
                statusCode: 200, //成功的状态码，默认：0
                countName: 'totals', //数据总数的字段名称，默认：count
                dataName: 'list' //数据列表的字段名称，默认：data
            },
            cols: [[
                {type: 'numbers'/*,width:"5%"*/}
                , {field: 'bizId', title: '业务', align: 'left', width:"20%"}
                , {field: 'lineId', title: '名称', align: 'left'}
                , {field: 'stepId', title: '节点', align: 'left'}
                , {field: 'status', title: '状态', align: 'left'}
                , {field: 'timeStart', title: '开始时间', align: 'left'}
                , {field: 'timeEnd', title: '结束时间', align: 'left'}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar', width:"20%"}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                $("[data-field='lineId']").children().each(function () {
                    const txt = lineMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
                $("[data-field='stepId']").children().each(function () {
                    const txt = stepMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
            }
        });

        //监听工具条
        table.on('tool(tableList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'viewDetail') {
                //编辑
                viewDetail(data);
            } else if (obj.event === 'next') {
                //编辑
                next(data);
            } else if (obj.event === 'forceNext') {
                //删除
                forceNext(data);
            } else if (obj.event === 'forceTerminate') {
                //编辑
                forceTerminate(data);
            }
        });

        //监听提交
        form.on('submit(editSubmit)', function (data) {
            formSubmit(data);
            return false;
        });
        //监听搜索框
        form.on('submit(searchSubmit)', function (data) {
            //重新加载table
            searchList(data);
            return false;
        });
    });

});

//提交表单
function formSubmit(obj) {
    $.ajax({
        type: "post",
        data: $("#editForm").serialize(),
        url: "/flowApi/start",
        success: function (data) {
            layer.closeAll();
            layer.msg(data.msg);
            if (data.code == 200) {
                loadList(obj);
            }
        },
        error: function () {
            layer.alert("操作请求错误，请您稍后再试", function () {
                layer.closeAll();
                loadList(obj);
            });
        }
    });
}

//新增
function add() {
    edit(null, "新增");
}

//打开编辑框
function edit(data, title) {
    cleanForm();
    if (data != null) {
        form.val("editForm", data)
    }
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);

    layer.open({
        type: 1,
        title: title,
        fixed: false,
        resize: true,
        shadeClose: true,
        maxmin: true,
        area: ['600px', '400px'],
        content: $('#editDetail'),
        end: function () {
            cleanForm();
        }
    });
}

function cleanForm() {
    //回显数据
    form.val("editForm", {
        "lineId": "",
        "bizId": "",
    })
}

//重新加载table
function loadList(obj) {
    tableIns.reload();
}

//重新加载table
function searchList(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field,
        page: {
            curr: pageCurr //从当前页码开始
        }
    });
}
//删除
function next(obj) {
    layer.confirm('您确定要手动下一步吗？', {
        btn: ['确认', '返回'] //按钮
    }, function () {
        $.ajax({
            type: "post",
            data: {"bizId": obj.bizId, "result":"Approve"},
            url: "/flowApi/vote",
            success: function (data) {
                layer.closeAll();
                layer.msg(data.msg);
                if (data.code == 200) {
                    loadList(obj);
                }
            },
            error: function (e) {
                console.log(e);
                layer.alert("操作请求错误，请您稍后再试", function () {
                    layer.closeAll();
                    loadList(obj);
                });
            }
        });
    }, function () {
        layer.closeAll();
    });
}
//删除
function forceNext(obj) {
    layer.confirm('您确定要强制下一步吗？', {
        btn: ['确认', '返回'] //按钮
    }, function () {
        $.ajax({
            type: "post",
            data: {"bizId": obj.bizId},
            url: "/flowApi/forceNext",
            success: function (data) {
                layer.closeAll();
                layer.msg(data.msg);
                if (data.code == 200) {
                    loadList(obj);
                }
            },
            error: function () {
                layer.alert("操作请求错误，请您稍后再试", function () {
                    layer.closeAll();
                    loadList(obj);
                });
            }
        });
    }, function () {
        layer.closeAll();
    });
}

function forceTerminate(obj) {

    layer.confirm('您确定要强制下一步吗？', {        btn: ['确认', '返回']     }, function () {
        $.ajax({
            type: "post",
            data: {"bizId": obj.bizId},
            url: "/flowApi/forceTerminate",
            success: function (data) {
                layer.closeAll();
                layer.msg(data.msg);
                if (data.code == 200) {
                    loadList(obj);
                }
            },
            error: function () {
                layer.alert("操作请求错误，请您稍后再试", function () {
                    layer.closeAll();
                    loadList(obj);
                });
            }
        });
    }, function () {
        layer.closeAll();
    });
}


//打开编辑框
function viewDetail(data) {
    initLogTable(data);
    layer.open({
        type: 1,
        title: "工作流详细",
        fixed: false,
        resize: true,
        shadeClose: true,
        maxmin: true,
        area: ['1400px', '800px'],
        content: $('#logDetail'),
        end: function () {

        }
    });
}

function initLogTable(data) {
    layui.use('table', function () {
        var table = layui.table;
        form = layui.form;

        var param = {
            lineId: data.lineId,
            bizId: data.bizId
        }
        table.render({
            id: 'bizLogId',
            elem: '#logTableList',
            url: '/flowApi/logList',
            where: param,
            method: 'post', //默认：get请求
            page: true,
            cellMinWidth: 50,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response: {
                statusName: 'code', //数据状态的字段名称，默认：code
                statusCode: 200, //成功的状态码，默认：0
                countName: 'totals', //数据总数的字段名称，默认：count
                dataName: 'list' //数据列表的字段名称，默认：data
            },
            cols: [[
                {type: 'numbers'}
                , {field: 'userIdTo', title: '审批人', align: 'left', width: "10%"}
                , {field: 'stepId', title: '节点', align: 'left', width: "10%"}
                , {field: 'status', title: '节点状态', align: 'left', width: "10%"}
                , {field: 'timeStart', title: '开始', align: 'left', width: "15%"}
                , {field: 'timeEnd', title: '结束', align: 'left', width: "15%"}
                // , {field: 'timeCost', title: '耗费', align: 'right', width: "10%"}
                , {field: 'result', title: '结果', align: 'left', width: "10%"}
                , {field: 'note', title: '备注', align: 'left'}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                $("[data-field='lineId']").children().each(function () {
                    const txt = lineMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
                $("[data-field='stepId']").children().each(function () {
                    const txt = stepMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
                $("[data-field='userIdFrom']").children().each(function () {
                    const txt = userMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
                $("[data-field='userIdTo']").children().each(function () {
                    const txt = userMap[$(this).text()];
                    if (txt) {
                        $(this).text(txt);
                    }
                });
                $("[data-field='timeCost']").children().each(function () {
                    if ($(this).text() != '耗费') {
                        var txt = '';
                        var costSeconds = Math.floor($(this).text() / 1000);
                        if (costSeconds > 86400) {
                            const days = Math.floor(costSeconds / 86400);
                            txt = txt + days + "天 ";
                            costSeconds = costSeconds - 86400 * days;
                        }
                        if (costSeconds > 3600) {
                            const hours = Math.floor(costSeconds / 3600);
                            txt = txt + hours + "小时 ";
                            costSeconds = costSeconds - 3600 * hours;
                        }
                        if (costSeconds > 60) {
                            const minutes = Math.floor(costSeconds / 60);
                            txt = txt + minutes + "分 ";
                            costSeconds = costSeconds - 60 * minutes;
                        }
                        txt = txt + costSeconds + "秒 ";
                        $(this).text(txt);
                    }
                });
            }
        });
    });
}