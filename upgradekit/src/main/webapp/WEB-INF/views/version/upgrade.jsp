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
	function executeComplete(status){
		appendMsg('升级结束');
		$('#msgTip').empty();
		$('<a href="javascript:void(0)">觉得升级失败？点这里。</a>').bind('click',function(){
			$.ligerDialog.confirm('确认升级失败', function (ok) {
				if(ok){
					$.post(webCtx+'/version/confirmResult',{id:'${version.id}',status:'FAIL'},function(data){
						if(data.status == 'success'){
							$.ligerDialog.success("己将升级结果置为失败");
						}else{
							$.ligerDialog.alert(data.msg);
						}
					});
				}
			});
		}).appendTo('#buttons');
		art.dialog.tips("升级结束");
	}
	var colors = ['#6F6C5C','#8B7C40'];
	var colorIdx = 0;
	function appendMsg(msg){
		var color = colors[colorIdx];
		colorIdx = colorIdx==0?1:0;
		$('#upgradeMsg').append("<nobr style=\"color:"+color+"\">" + msg + "</nobr></br>");
		$('#msgContainer').scrollTop( $('#upgradeMsg').height() );
	}
	function executeUpdate(){
		$.ligerDialog.confirm('确认升级', function (ok) {
			if(ok){
				$('#submitBtn').attr('disabled','disabled');
				$('#msgTip').text("升级进行中，请勿关闭窗口");
				$('#upgradeExecuteForm').submit();
			}
		});
	}
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 600px;min-height: 430px;">
	<div id="main">
		<div position="center">
				<div>
					<span id="buttons">
						<button id="submitBtn" onclick="executeUpdate()">开始升级</button><span id="msgTip"></span>
					</span>
				</div>
				<div id="msgContainer" style="padding:5px;margin-top:5px;height: 400px;overflow: scroll;" class="l-panel" >
					<div id="upgradeMsg" />
				</div>
			</div>
        </div> 
	</div>
	<div style="display: none">
		<form id="upgradeExecuteForm" action="${ctx }/version/executeVersionUpdate" target="updateMsgFrame">
			<input type="text" name="id" value="${version.id }"/>
			<input type="text" name="msgFunctionName" value="appendMsg"/>
			<input type="text" name="completeFunctionName" value="executeComplete"/>
		</form>
		<iframe name='updateMsgFrame'></iframe>
	</div>
</body>
</html>