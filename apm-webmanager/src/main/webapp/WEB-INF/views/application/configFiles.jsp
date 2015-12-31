<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<jsp:include page="../common/common.jsp"></jsp:include>
	<link href="${ctx }/zTree_v3/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
	<script type="text/javascript" src="${ctx }/zTree_v3/js/jquery.ztree.core-3.5.js"></script>
</head>
<script type="text/javascript">
	var appId = '${application.id}';
	var setting = {
		view: {
			selectedMulti: false
		},
		async: {
			enable: false
		},
		callback: {
			onClick: function(event, treeId, treeNode) {
			    if(treeNode.file){
			    	$('#filePath').val(treeNode.path);
	    			art.dialog({
						title: "文件编辑-"+treeNode.name,
						lock : true,
						opacity : 0.2,
						padding: '3px',
						content: document.getElementById('editDiv'),
						ok: function(){
							saveEditFile();
							return false;
						},
						okVal: "保存",
						cancel: true,
						cancelVal: "取消",
						init: function(){
							loadFileContent(appId, treeNode.path);
						}
					});
			    }
			}
		}
	};

	function loadFileContent(){
		var filePath = $('#filePath').val();
		var charset = $('#fileCharset').val();
		var param = {id:appId,filePath:filePath};
		if(charset){
			param.charset = charset;
		}
		$.ligerDialog.waitting('文件内容正在加载中,请稍候...');
		$.post(webCtx+'/application/fileContent',param,function(data){
			$.ligerDialog.closeWaitting();
    		if(data.status == 'success'){
    			$('#fileContent').val(data.content);
    			$('#fileCharset').val(data.charset);
    		}else{
    			$.ligerDialog.warn(data.msg);
    		}
    	});
	}
	
	$(function(){
		$.post(webCtx+'/application/configFiles',{id : appId},function(data){
			if(data.status == 'success'){
				$.fn.zTree.init($("#tree"), setting,data.datas);
			}else{
				$.ligerDialog.warn(data.msg);
			}
		});
		$('#fileCharset').bind('change',function(){
			loadFileContent();
		});
	});
	
	function saveEditFile(){
		$('#editForm').ajaxSubmit({
			dataType:'json',
			beforeSubmit: function() {
                $.ligerDialog.waitting('正在保存中,请稍候...');
                return true;
            },
            success: function(data){
            	if(data.status == 'success'){
	            	$.ligerDialog.alert("保存成功");
            	}else{
            		$.ligerDialog.error(data.msg);
            	}
            },
            complete: function(){
            	$.ligerDialog.closeWaitting();
            },
            error: function(){
            	$.ligerDialog.error("服务器异常");
            }
		});
	}
</script>
<body  style="padding: 5px;">
	<div id="main">
		<div position="center" class="l-panel">
			 <div id="tree" class="ztree"></div>
        </div> 
	</div>
	<div id="editDiv" style="display: none;width:800px;height: 400px;">
		<form id="editForm" action="${ctx}/application/saveFile" method="post" style="width: 100%;height: 100px;">
			<input type="hidden" name="id" id="applicationId" value="${application.id}"/>
			<input type="hidden" name="filePath" id="filePath"/>
			<select id="fileCharset" name="charset">
				<option>UTF-8</option>
				<option>GBK</option>
				<option>GB2312</option>
			</select>
			<textarea name="fileContent" id="fileContent" style="height: 370px;width: 100%;word-wrap:normal;overflow:auto;"></textarea>
		</form>
	</div>
</body>
</html>