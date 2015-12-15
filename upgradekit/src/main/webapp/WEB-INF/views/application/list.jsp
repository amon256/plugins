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
			url : webCtx + "/application/listData",
			params : params,
			columns: [
                { display: '应用名', name: 'name', width: 100},
                { display: '编号', name: 'number', width: 100,align:'left' }, 
                { display: '创建日期', name: 'createTime', width: 75,align : "right" ,render : gridDateFormatterFunction('yyyy-MM-dd')},
                { display: '最后更新时间', name: 'lastUpdateTime',width : 130 ,align : "right",render : gridDateFormatterFunction('yyyy-MM-dd HH:mm:ss')},
                { display: '描述', name: 'description', width: 100,align:'left' }, 
                { display: '操作', name: 'operation',width : 100,isSort : false,render : function(rowdata, index, value){
                	var rowId = rowdata.id;
                	var result = hrefCallbackLabel('版本更新',function(){
                		openDialog(webCtx + '/version/list?appId='+rowId,'版本更新',{width:900,height:500});
                	});
                	return result;
                }}
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
			openDialog(webCtx + '/application/toAdd','新增');
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
</body>
</html>