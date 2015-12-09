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
		$('#upgradeForm').ajaxForm();
	});
	function executeComplete(status){
		appendMsg('升级结束');
		art.dialog.tips("升级结束");
	}
	function appendMsg(msg){
		$('#upgradeMsg').append("<span>" + msg + "</span></br>");
		$('#msgContainer').scrollTop( $('#upgradeMsg').height() );
	}
	function ajaxSubmit(){
		$('#upgradeForm').ajaxSubmit({
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
                appendMsg("数据提交中……");
                $('#submitBtn').attr('disabled','disabled');
                return true;
            },
            success: function(data){
            	if(data.status == 'success'){
	            	appendMsg("数据提交完成。");
	            	$('#upgradeForm').clearForm();
	            	appendMsg("升级脚本执行中,请勿关闭窗口。");
	            	executeUpdate(data.version.id)
            	}else{
            		$.ligerDialog.error(data.msg);
            		$('#submitBtn').removeAttr('disabled');
            	}
            },
            error: function(){
            	$.ligerDialog.error("服务器异常");
            	appendMsg("服务器异常。");
            	$('#submitBtn').removeAttr('disabled');
            }
		});
	}
	function executeUpdate(id){
		$('#versionId').val(id);
		$('#upgradeExecuteForm').submit();
	}
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 600px;min-height: 480px;">
	<div id="main">
		<div position="center">
				<form id="upgradeForm" action="${ctx }/application/upgrade" onsubmit="return false;" enctype="multipart/form-data" method="post">
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
								<button type="button" id="submitBtn" onclick="ajaxSubmit()">确定升级</button>
							</td>
						</tr>
					</table>
				</form>
				<div id="msgContainer" style="padding:5px;margin-top:5px;height: 400px;overflow: scroll;" class="l-panel" >
					<div id="upgradeMsg" />
				</div>
			</div>
        </div> 
	</div>
	<div style="display: none">
		<form id="upgradeExecuteForm" action="${ctx }/application/executeVersionUpdate" target="updateMsgFrame">
			<input type="text" name="id" id="versionId"/>
			<input type="text" name="msgFunctionName" value="appendMsg"/>
			<input type="text" name="completeFunctionName" value="executeComplete"/>
		</form>
		<iframe name='updateMsgFrame'></iframe>
	</div>
</body>
</html>