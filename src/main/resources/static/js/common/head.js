/**
 * 菜单
 * */
//获取路径uri
var pathUri = window.location.href;
$(function () {
    layui.use('element', function () {
        var element = layui.element;
        // 左侧导航区域（可配合layui已有的垂直导航）
        $.get("/source/getUserPerms", function (data) {
            if (data != null) {
                getMenus(data.data);
                element.render('nav');
            } else {
                layer.alert("权限不足，请联系管理员", function () {
                    //退出
                    window.location.href = "/logout";
                });
            }
        });
    });
})
var getMenus = function (data) {
    if (!data || !data.length || data.length == 0) {
        console.log("no menu");
        return;
    }
    //回显选中
    var ul = $("<ul class='layui-nav layui-nav-tree' lay-filter='test'></ul>");
    for (var i = 0; i < data.length; i++) {
        var node = data[i];
        var li = $("<li class='layui-nav-item' flag='" + node.sourceId + "'></li>");
        var a = $("<a class='' href='javascript:;'>" + node.sourceName + "</a>");
        li.append(a);
        //获取子节点
        var childArry = node.childrens;
        if (childArry.length > 0) {
            a.append("<span class='layui-nav-more'></span>");
            var dl = $("<dl class='layui-nav-child'></dl>");
            for (var y in childArry) {
                var dd = $("<dd><a href='" + childArry[y].url + "'>" + childArry[y].sourceName + "</a></dd>");
                //判断选中状态
                if (pathUri.indexOf(childArry[y].url) > 0) {
                    li.addClass("layui-nav-itemed");
                    dd.addClass("layui-this")
                }
                dl.append(dd);
            }
            li.append(dl);
        }
        ul.append(li);
    }
    $(".layui-side-scroll").append(ul);
}

//根据菜单主键id获取下级菜单
//id：菜单主键id
//arry：菜单数组信息
function getParentArry(id, arry) {
    var newArry = new Array();
    for (var x in arry) {
        if (arry[x].pId == id)
            newArry.push(arry[x]);
    }
    return newArry;
}


function updateUsePwd() {
    layer.open({
        type: 1,
        title: "修改密码",
        fixed: false,
        resize: false,
        shadeClose: true,
        maxmin: true,
        area: ['600px', '400px'],
        content: $('#pwdDiv')
    });
}