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
		var form = $('#addForm').ligerForm({
			inputWidth: 170, labelWidth: 60, space: 40,
			validate : true,
			fields: [
				{ display: '姓名', name: 'name',validate : {required:true}},
				{ display: '账号', name: 'account',validate : {required:true}},
				{ display: '手机', name: 'mobile',validate : {required:true}}
			],
			buttons : [
				{text: '保存', width: 60, click: function(){
					var submitData = form.getData();
					$.ligerDialog.waitting('正在保存中,请稍候...');
					$.post(webCtx + '/adminuser/add',submitData,function(data){
						$.ligerDialog.closeWaitting();
						if(data.status == 'success'){
							$.ligerDialog.success("保存成功","提示",function(){
								art.dialog.close();
							});
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
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 400px;min-height: 160px;">
	<div id="main">
		<div position="center">
				<form id="addForm" >
				</form>
			</div>
        </div> 
	</div>
</body>
</html>