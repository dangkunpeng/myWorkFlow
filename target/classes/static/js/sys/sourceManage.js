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
            id: 'id',
            elem: '#sourceList',
            url: '/source/sourceList',
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
                , {field: 'pid', title: '上级菜单', align: 'left'/*,width:"10%"*/}
                , {field: 'lineIndex', title: '顺序', align: 'left'/*,width:"15%"*/}
                , {field: 'sourceName', title: '菜单名称', align: 'left'/*,width:"10%"*/}
                , {field: 'url', title: '菜单url', align: 'left'/*,width:"15%"*/}
                , {field: 'createTime', title: '创建时间', align: 'center'/*,width:"10%"*/}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar'/*,width:"25%"*/}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                fun.formatField("pid", sourceMap);
            }
        });


        //监听工具条
        table.on('tool(sourceTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                //删除
                del(data, data.sourceId);
            } else if (obj.event === 'edit') {
                //编辑
                edit(data);
            }
        });

        //监听提交
        form.on('submit(sourceSubmit)', function (data) {
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
    $ajaxPost("/source/setSource", $("#sourceForm").formData());
}

//新增
function add() {
    edit(null, "新增");
}

//打开编辑框
function edit(data, title) {
    cleanSource();
    if (data != null) {
        form.val("sourceForm", data)
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
        area: fun.layerArea,
        content: $('#setSource'),
        end: function () {
            cleanSource();
        }
    });
}

function cleanSource() {
    //回显数据
    form.val("sourceForm", {
        "sourceId": "",
        "pid": "",
        "lineIndex": "",
        "sourceName": "",
        "url": "",
    })
}

//重新加载table
function reloadTab(obj) {
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
function del(obj, sourceId) {
    layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
        $ajaxPost("/source/del", {"sourceId": sourceId});
    }, function () {
        layer.closeAll();
    });
}