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
            id: 'stepId',
            elem: '#tableList',
            url: '/flowStep/pageList',
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
                , {field: 'lineId', title: '工作流名称', align: 'left'/*,width:"10%"*/}
                , {field: 'lineIndex', title: '序号', align: 'left'/*,width:"10%"*/}
                , {field: 'stepName', title: '节点名称', align: 'left'/*,width:"10%"*/}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar'/*,width:"25%"*/}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                fun.formatField("lineId", lineMap);
            }
        });


        //监听工具条
        table.on('tool(tableList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                //删除
                del(data, data.stepId);
            } else if (obj.event === 'edit') {
                //编辑
                edit(data);
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
    $ajaxPost("/flowStep/save", $("#editForm").formData());
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
        area: fun.layerArea,
        content: $('#editDetail'),
        end: function () {
            cleanForm();
        }
    });
}

function cleanForm() {
    //回显数据
    form.val("editForm", {
        "stepId": "",
        "stepName": "",
        "lineId": "",
        "lineIndex": "",
    })
}

//重新加载table
function reloadTab() {
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
function del(obj, id) {
    layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
        $ajaxPost("/flowStep/delete", {"stepId": id});
    }, function () {
        layer.closeAll();
    });
}