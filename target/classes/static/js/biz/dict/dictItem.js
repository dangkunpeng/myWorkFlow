/**
 * 用户管理
 */
var pageCurr;
var form;
$(function () {
    layui.use(['table'], function () {
        var table = layui.table;
        form = layui.form;
        tableIns = table.render({
            elem: '#dictItemList',
            url: '/dictItem/list',
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
                , {field: 'typeId', title: '字典名', align: 'center'}
                , {field: 'lineIndex', title: '项顺序', align: 'center'}
                , {field: 'value', title: '项编码', align: 'center'}
                , {field: 'text', title: '项名称', align: 'center'}
                , {field: 'note', title: '备注', align: 'center'}
                , {title: '操作', align: 'center', toolbar: '#optBar'}
            ]],
            done: function (res, curr, count) {
                pageCurr = curr;
                fun.formatField("typeId", dictTypeMap);
            }
        });
        //监听工具条
        table.on('tool(dictItemList)', function (obj) {
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
            searchDictItem(data);
            return false;
        });
        //监听搜索框
        form.on('submit(dictItemReset)', function (data) {
            //重新加载table
            loadDictItem(data);
            return false;
        });
        //监听提交
        form.on('submit(dictItemSubmit)', function (data) {
            formSubmitDictItem(data);
            return false;
        });
    });
});


//开通用户
function addDict() {
    openDict(null, "新增");
}

function openDict(data, title) {
    cleanDict();
    if (data == null || data == "") {
        $("#typeId").val("");
    } else {
        form.val("dictItemForm", data);
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
        content: $('#setDictItem'),
        end: function () {
            cleanDict();
        }
    });
}

//提交表单
function formSubmitDictItem(param) {
    $ajaxPost("/dictItem/save", $("#dictItemForm").formData());
}

function delDict(param) {
    layer.confirm(fun.confirmMsg, fun.confirmBtns, function () {
        $ajaxPost("/dictItem/del", param);
    }, function () {
        layer.closeAll();
    });
}


function cleanDict() {
    form.val("dictItemForm", {
        "typeId": "",
        "itemId": "",
        "value": "",
        "text": "",
        "lineIndex": "",
        "note": "",
    });
}

function reloadTab() {
    //重新加载table
    tableIns.reload();
}

function searchDictItem(obj) {
    //重新加载table
    tableIns.reload({
        where: obj.field,
        page: {
            curr: pageCurr //从当前页码开始
        }
    });
}