<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<jsp:include page="../common/common.jsp"></jsp:include>
</head>
<script type="text/javascript">
	$(function(){
		var form = $('#editForm').ligerForm({
			inputWidth: 170, labelWidth: 60, space: 40,
			validate : true,
			prefixID : 'field_',
			fields: [
				{name: 'id', type: 'hidden', options: {value: '${entity.id}'}},
				{ display: '主机名', name: 'name', validate : {required:true,rangelength:[3,20]}, options: {value: '${entity.name}'}},
				{ display: '地址', name: 'host', validate : {required:true}, options: {value: '${entity.host}'}},
				{ display: '端口', name: 'port', validate : {required:true,range:[1000,65535]}, options: {value: '${entity.port}'}},
				{ display: '描述', name: 'description', type: 'textarea',validate : {rangelength:[0,500]}, options: {value: '${entity.description}'}}
			],
			buttons : [
				{text: '保存', width: 60, click: function(){
					var submitData = form.getData();
					$.ligerDialog.waitting('正在保存中,请稍候...');
					$.post(webCtx + '/hostmachine/saveEdit',submitData,function(data){
						$.ligerDialog.closeWaitting();
						if(data.status == 'success'){
							$.ligerDialog.success("保存成功","提示",function(){
								art.dialog.close();
							});
						}else{
							var msg = data.msg;
							if(data.validateErrors && data.validateErrors.length > 0){
								for(var i = 0; i < data.validateErrors.length; i++){
									var err = data.validateErrors[i];
									msg += '<br/><span style="color:red;"><label style="color:blue;">'+err.fieldName+'</label>'+err.message+'</span>';
								}
							}
							$.ligerDialog.error(msg);
						}
					});
				}},
				{text: '取消', width: 60, click: function(){
					art.dialog.close();
				}}
			]
		});
	});
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 400px;min-height: 160px;">
	<div id="main">
		<div position="center">
			<form id="editForm" />
        </div> 
	</div>
</body>
</html>