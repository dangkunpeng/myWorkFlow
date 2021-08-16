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
            elem: '#dateCreate',
            type: 'date',
            format: 'yyyy-MM-dd',
            min: -7,
            max: 7,
        })
        tableIns = table.render({
            id: 'logId',
            elem: '#tableList',
            url: '/k12Log/pageList',
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

                , {field: 'studentId', title: '学生姓名', align: 'left'/*,width:"10%"*/}
                , {field: 'classId', title: '课程名称', align: 'left', width: "20%"}
                , {field: 'dateCreate', title: '上课日期', align: 'left'/*,width:"10%"*/}
                , {field: 'dateUpdate', title: '签到时间', align: 'center'/*,width:"10%"*/}
                , {field: 'attended', title: '状态', align: 'center'/*,width:"10%"*/}
                , {fixed: 'right', title: '操作', align: 'center', toolbar: '#optBar'/*,width:"25%"*/
                //templet:function(d){return d.attended                }
            }
            ]],
            done: function (res, curr, count) {
                console.log(res);
                pageCurr = curr;
                fun.formatField("classId", classMap);
                fun.formatField("studentId", studentMap);
                fun.formatField("attended", attendedMap);
            }
        });


        //监听工具条
        table.on('tool(tableList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'mark') {
                //删除
                mark(data, data.logId);
            } else if (obj.event === 'delete') {
                //编辑
                del(data, data.logId);
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
    $ajaxPost("/k12Log/save", $("#editForm").formData())
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
        }
    });
}

function cleanForm() {
    //回显数据
    form.val("editForm", {
        "logId": "",
        "studentId": "",
        "classId": "",
        "attended": "",
        "dateCreate": "",
        "dateUpdate": "",
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
function mark(obj, id) {
    layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
        $ajaxPost("/k12Log/mark", {"logId": id});
    }, function () {
        layer.closeAll();
    });
}

//删除
function del(obj, id) {
    if (obj.attended) {
        layer.msg('已参加,不可删除记录!');
    } else {
        layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
            $ajaxPost("/k12Log/delete/" + id, "");
        }, function () {
            layer.closeAll();
        });
    }
}
