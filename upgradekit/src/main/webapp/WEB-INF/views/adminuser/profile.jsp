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
		$.metadata.setType("attr", "validate");
		var userId = '${user.id}';
		$.post(webCtx + '/adminuser/updateData',{id:userId},function(json){
			$('#editForm').ligerForm({
				inputWidth: 170, labelWidth: 60, space: 40,
				validate : true,
				fields: [
					{ display: '姓名', name: 'name', type: 'text',validate : {required:true}},
					{ display: '手机', name: 'mobile', type: 'text',validate : {required:true}}
				],
				buttons : [
					{text: '保存', width: 60, click: function(){
						var submitData = form.getData();
						$.ligerDialog.waitting('正在保存中,请稍候...');
						$.post(webCtx + '/adminuser/profile',submitData,function(data){
							$.ligerDialog.closeWaitting();
							if(data.status == 'success'){
								$.ligerDialog.success("保存成功","提示");
							}else{
								$.ligerDialog.error(data.msg);
							}
						});
					}},
					{text: '取消', width: 60, click: function(){
						art.dialog.close();
					}}
				]
			});
		});
	});
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 600px;min-height: 160px;">
	<div id="main">
		<div position="center">
			<form id="editForm" />
        </div> 
	</div>
</body>
</html>