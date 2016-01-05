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
		var params = [];
		var gridConfig = {
			url : webCtx + "/hostmachine/listData",
			params : params,
			columns: [
                { display: '主机名', name: 'name', width: 150, render : function(rowdata, index, value){
                	var rowId = rowdata.id;
                	var result = hrefCallbackLabel(value + '&nbsp;<span class="l-icon l-icon-edit">&nbsp;&nbsp;&nbsp;&nbsp;</span>',function(){
                		openDialog(webCtx + '/hostmachine/toEdit?id='+rowId,'修改',{});
                	});
                	return result;
                }},
                { display: '地址', name: 'host', width: 100,align:'left' }, 
                { display: '端口', name: 'port', width: 100,align:'right' }, 
                { display: '创建日期', name: 'createTime', width: 100,align : "center" ,render : gridDateFormatterFunction('yyyy-MM-dd')},
                { display: '描述', name: 'description', width: 180,align:'left' }
            ]
        };
		var dataGrid = $("#dataGrid").ligerGrid($.extend(true,{},DefaultConfig.dataGridConfig,gridConfig)); 
		$.metadata.setType("attr", "validate");
		$('#queryForm').validate({
			debug: true,
			submitHandler: function (){
				dataGrid.setParm('keyword',$('#keyword').val());
				dataGrid.loadData();
				dataGrid.changePage('first');
            }
		});
		$('#addBtn').bind('click',function(){
			openDialog(webCtx + '/hostmachine/toAdd','新增');
		});
		function openDialog(url,title,config){
			config = config || {};
			art.dialog.open(url,$.extend({
    			title : title,
    			lock : true,
				opacity : 0.2,
				close : function(){
					$('#searchBtn').trigger('click');
				}
    		},config),false);
		}
	});
</script>
<body  style="overflow:hidden;padding: 5px;">
	<div id="main">
		<div position="center">
			<div id="queryPanel" class="l-panel" style="height:60px;padding-left: 0px;padding-right: 0px;">
				<form id="queryForm" class="l-form" onsubmit="return false;">
					<div class="l-form-container">
						<ul>
							<li style="width: 60px; text-align: left;">关键字：</li>
							<li style="width: 180px; text-align: left;">
								<div class="l-text" style="width: 178px;">
									<input type="text" name="keyword" id="keyword" class="l-text-field" style="width: 174px;" placeholder="关键字 "/>
								</div>
							</li>
							<li style="width: 80px; text-align: center;">
								<button type="submit" id="searchBtn" name="searchBtn" class="l-button">搜索</button>
							</li>
							<li style="width: 80px; text-align: center;">
								<button type="reset" id="clearBtn" name="clearBtn" class="l-button">清空</button>
							</li>
							<li style="width: 80px; text-align: center;">
								<button type="button" id="addBtn" name="addBtn" class="l-button">新增</button>
							</li>
						</ul>
					</div>
				</form>
			</div>
			<div class="-clear"/>
			 <div id="dataGrid"></div>
        </div> 
	</div>
	<div id="msgContainer" style="width:400px;height: 400px;overflow: scroll;" class="l-panel" >
		<div id="msgArea" />
	</div>
	<div style="display: none">
		<form id="cmdExecuteForm" target="remoteCommandFrame">
			<input type="text" name="id" id="applicationId"/>
			<input type="text" name="msgFunctionName" value="appendMsg"/>
			<input type="text" name="completeFunctionName" value="executeComplete"/>
		</form>
		<iframe name='remoteCommandFrame'></iframe>
	</div>
</body>
</html>