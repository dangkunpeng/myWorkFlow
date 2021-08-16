/**
 * 权限管理
 */
var pageCurr;
var form;
var laydate;
$(function () {
    layui.use(['table', 'laydate'], function () {
        var table = layui.table;
        form = layui.form;
        laydate = layui.laydate;
        laydate.render({
            elem: '#classTime',
            type: 'time',
            format: 'HH:mm',
            min: '00:00:00',
            max: '20:00:00'
        })
        tableIns = table.render({
            id: 'classId',
            elem: '#tableList',
            url: '/k12Class/pageList',
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
                , {field: 'className', title: '课程名称', align: 'left'/*,width:"10%"*/}
                , {field: 'teacherId', title: '老师', align: 'left'/*,width:"10%"*/}
                , {field: 'classTypeId', title: '课程类型', align: 'center'/*,width:"10%"*/}
                , {field: 'classWeek', title: '周期', align: 'center'/*,width:"10%"*/}
                , {field: 'classTime', title: '课程时间', align: 'center'/*,width:"10%"*/}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar'/*,width:"25%"*/}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                fun.formatField("classWeek", weekMap);
                fun.formatField("teacherId", teacherMap);
                fun.formatField("classTypeId", classTypeMap);

            }
        });


        //监听工具条
        table.on('tool(tableList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                //删除
                del(data, data.classId);
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
    $ajaxPost("/k12Class/save", $("#editForm").formData());
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
            classTime

        }
    });
}

function cleanForm() {
    //回显数据
    form.val("editForm", {
        "classId": "",
        "className": "",
        "classTypeId": "",
        "teacherId": "",
        "classWeek": "",
        "classTime": "",
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
        $ajaxPost("/k12Class/delete", {"classId": id});
    }, function () {
        layer.closeAll();
    });
}
