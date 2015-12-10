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
		$('#addForm').ajaxForm();
	});
	function ajaxSubmit(){
		$('#addForm').ajaxSubmit({
			dataType:'json',
			beforeSubmit: function() {
                //表单提交前做表单验证
                if(!($('#number').val())){
                	$.ligerDialog.error("版本号不熊空");
                	return false;
                }
                if(!($('#versionFile').val())){
                	$.ligerDialog.error("版本文件不能为空");
                	return false;
                }
                $('#submitBtn').attr('disabled','disabled');
                $.ligerDialog.waitting('正在保存中,请稍候...');
                return true;
            },
            success: function(data){
            	if(data.status == 'success'){
	            	$('#addForm').clearForm();
	            	$.ligerDialog.alert("保存成功",function(){
	            		art.dialog.close();
	            	});
            	}else{
            		$.ligerDialog.error(data.msg);
            		$('#submitBtn').removeAttr('disabled');
            	}
            },
            complete: function(){
            	$.ligerDialog.closeWaitting();
            },
            error: function(){
            	$.ligerDialog.error("服务器异常");
            	$('#submitBtn').removeAttr('disabled');
            }
		});
	}
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 400px;min-height: 320px;">
	<div id="main">
		<div position="center">
				<form id="addForm" action="${ctx }/version/addSave" onsubmit="return false;" enctype="multipart/form-data" method="post">
					<input type="hidden" name="application.id" value="${app.id }"/>
					<table>
						<tr>
							<td align="left" width="80">版本号: </td>
							<td><input type="text" id="number" name="number"/></td>
						</tr>
						<tr>
							<td>升级附件</td>
							<td><input type="file" id="versionFile" name="versionFileUpload"/></td>
						</tr>
						<tr>
							<td>参数文件</td>
							<td><input type="file" id="parameterFile" name="parameterFileUpload"/></td>
						</tr>
						<tr>
							<td>
								<button type="button" class="l_button" id="submitBtn" onclick="ajaxSubmit()">确定保存</button>
							</td>
						</tr>
					</table>
				</form>
			</div>
        </div> 
	</div>
</body>
</html>