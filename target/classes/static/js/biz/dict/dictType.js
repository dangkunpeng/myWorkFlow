/**
 * 用户管理
 */
var pageCurr;
var form;
$(function () {
    layui.use(['table', 'form'], function () {
        var table = layui.table,
            form = layui.form;

        tableIns = table.render({
            elem: '#dictTypeList',
            url: '/dictType/list',
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
                {type: 'numbers', title: '序号', align: 'center'}
                , {field: 'typeName', title: '字典名', align: 'center'}
                , {field: 'note', title: '备注', align: 'center'}
                , {title: '操作', align: 'center', toolbar: '#optBar'}
            ]],
            done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                //得到当前页码
                //得到数据总量
                pageCurr = curr;
            }
        });

        //监听工具条
        table.on('tool(dictTypeList)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                //删除
                delDict(data);
            } else if (obj.event === 'edit') {
                //编辑
                openDict(data, "编辑");
            }
        });


    });

    //搜索框
    layui.use(['form'], function () {
        var form = layui.form;
        //监听搜索框
        form.on('submit(searchSubmit)', function (data) {
            //重新加载table
            loadDictType(data);
            return false;
        });
        //监听搜索框
        form.on('submit(dictReset)', function (data) {
            //重新加载table
            loadDictType(data);
            return false;
        });
        //监听提交
        form.on('submit(dictSubmit)', function (data) {
            formSubmitDict(data);
            return false;
        });
    });
});

//提交表单
function formSubmitDict(obj) {
    $ajaxPost("/dictType/save", $("#dictForm").formData());
}

//开通用户
function addDict() {
    openDict(null, "新增");
}

function openDict(data, title) {
    var roleId = null;
    if (data == null || data == "") {
        $("#typeId").val("");
    } else {
        $("#typeId").val(data.typeId);
        $("#typeName").val(data.typeName);
        $("#note").val(data.note);
        roleId = data.roleId;
    }
    var pageNum = $(".layui-laypage-skip").find("input").val();
    $("#pageNum").val(pageNum);

    layer.open({
        type: 1,
        title: title,
        fixed: true,
        resize: false,
        shadeClose: true,
        maxmin: true,
        area: fun.layerArea,
        content: $('#setDict'),
        end: function () {
            cleanDict();
        }
    });
}

function delDict(data) {
    layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
        $ajaxPost("/dictType/del", data);
    }, function () {
        layer.closeAll();
    });
}

//恢复
function reloadTab() {
    tableIns.reload();
}

function loadDictType(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function cleanDict() {
    $("#typeId").val("");
    $("#typeName").val("");
    $("#note").val("");
}
