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
		var form = $('#editForm').ligerForm({
			inputWidth: 170, labelWidth: 60, space: 40,
			validate : true,
			fields: [
				{ name: 'userId',type:"hidden",options:{value:'${user.id}'}},
				{ display: '密码', name: 'password',type:"password",attr:{id:"password"},validate : {required:true}},
				{ display: '重复密码', name: 'repassword',type:'password',attr:{id:"repassword"},validate : {required:true,equalTo:'#password'}}
			],
			buttons : [
				{text: '激活', width: 60, click: function(){
					var submitData = form.getData();
					submitData.id = submitData.userId;
					$.ligerDialog.waitting('正在激活中,请稍候...');
					$.post(webCtx + '/adminuser/effect',submitData,function(data){
						$.ligerDialog.closeWaitting();
						if(data.status == 'success'){
							$.ligerDialog.success("激活成功","提示",function(){
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
<body  style="overflow:hidden;padding: 5px;min-width: 400px;min-height: 120px;">
	<div id="main">
		<div position="center">
				<form id="editForm" >
				</form>
			</div>
        </div> 
	</div>
</body>
</html>