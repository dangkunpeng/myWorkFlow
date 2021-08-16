/**
 * $ajax的二次封裝
 * 请定义一个方法reloadTab,用于刷新列表
 */
function $ajax(url, method, param, contentType, successCallback, errorCallback) {
	console.log(param);
	$.ajax({
		url : url,
		method : method,
		data : param,
		contentType : contentType,

		success : function(data) {
			//fieldErrors
			if (data && typeof(data) === "string" && data.indexOf("fieldErrors") >= 0) {
				var errors = JSON.parse(data)["fieldErrors"];
				var fieldErrors = "";
				$.each(errors, function(index, item) {
					fieldErrors = fieldErrors + item + "<br>";
				});

				if (__isShowInModal()) {
					__showDangerMessageInCurrentModal(fieldErrors);
				} else {
					showDangerMessage(fieldErrors);
				}
			} else {
				layer.closeAll();
				if (data.code == 200 && reloadTab && typeof (reloadTab) === "function") {
					layer.msg(data.msg);
					reloadTab();
				} else {
					layer.alert(data.msg);
				}
				if (successCallback && typeof (successCallback) === "function") {
					successCallback(data);
				}
			}
		},

		error : function(e) {
			console.error(e);
			layer.alert("操作请求错误，请您稍后再试", function () {
				layer.closeAll();
			});
			if (errorCallback && typeof (errorCallback) === "function") {
				errorCallback();
			}
		}
	});
}

function $ajaxPost(url, param) {
	$ajax(url, "post", JSON.stringify(param), "application/json");
}
function $ajaxPost(url, param, successCallback) {
	$ajax(url, "post", JSON.stringify(param), "application/json", successCallback);
}

function $ajaxGet(url, param, contentType, successCallback, errorCallback) {
	$ajax(url, "get", param, contentType, successCallback, errorCallback);
}

function __isShowInModal() {
	var showLocation = __getCurrentModal();
	return showLocation.is('div');
}

function __getCurrentModal() {
	var modalObject = $("div.modal.fade.in > .modal-dialog > .modal-content > .modal-body");
	return modalObject;
}

function __showDangerMessageInCurrentModal(msg) {
	$("#fieldErrorsModalDiv").remove();

	var html = "";
	html += '<div id="fieldErrorsModalDiv" class="note-box note note-danger note-box-dismiss">';
	html +=     '<button type="button" class="close" data-dismiss="note-box" aria-hidden="true" onclick="javascript:__hiddenMessageInCurrentModal();"></button>';
	html +=     '<span>';
	html +=         msg;
	html +=     '</span>';
	html += '</div>';

	var currentModal = __getCurrentModal();
	currentModal.prepend(html);
	window.setTimeout("$('#fieldErrorsModalDiv').hide()",60000);//3000毫秒后，隐藏你的DIV
}

function __hiddenMessageInCurrentModal() {
	$("#fieldErrorsModalDiv").remove();
}
