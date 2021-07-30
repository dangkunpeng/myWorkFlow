/**
 * $ajax的二次封裝
 */
function $ajax(url, method, param, contentType, successCallback, errorCallback) {
	$.ajax({
		url : __contextPath + url,
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
				if (successCallback && typeof (successCallback) === "function") {
					successCallback(data);
				} else {
					alert("ERROR!!!");
				}
			}
		},

		error : function() {
			if (errorCallback && typeof (errorCallback) === "function") {
				errorCallback();
			} else {
				alert("ERROR!!!");
			}
		}
	});
}

function $ajaxPost(url, param, contentType, successCallback, errorCallback) {
	$ajax(url, "post", param, contentType, successCallback, errorCallback);
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
