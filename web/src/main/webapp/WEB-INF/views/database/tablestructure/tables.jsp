<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="${pageContext.request.contextPath }/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath }/bootstrap/3.3.4/css/bootstrap-theme.min.css" rel="stylesheet">
	<script type="text/javascript" src="${pageContext.request.contextPath }/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath }/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<title>我的工具箱</title> 
</head>
<body>
	<jsp:include page="../../menu.jsp" flush="true">
		<jsp:param value="menu_tablestructure" name="menuId"/>
	</jsp:include>
	<div style="display: none;">
		<form id="exportForm" action="${pageContext.request.contextPath }/database/tablestructure/export" method="post" target="exportFrame">
			<textarea name="filter">${sessionScope.ConnectionInfo.filter }</textarea>
		</form>
		<iframe id="exportFrame" name="exportFrame"></iframe>
	</div>
	<div class="container">
		<div>
			<a href="${pageContext.request.contextPath }/database/tablestructure/start" class="btn btn-info">返回</a>
			<a href="javascript:$('#exportForm').submit();" class="btn btn-success">导出静态HTML</a>
		</div>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th width="10%" align="center">序号</th>
					<th width="5%" align="center"><input type="checkbox" id="checkedAll"/></th>
					<th width="35%" align="center">名称</th>
					<th width="50%" align="center">描述</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${dbTables }" var="dbTable" varStatus="status">
					<tr>
						<td>${status.index +1 }</td>
						<td><input type="checkbox" name="tableName" value="${dbTable.name }"/></td>
						<td><a href="${pageContext.request.contextPath }/database/tablestructure/table?tableName=${dbTable.name }" target="_blank">${dbTable.name }</a></td>
						<td>${dbTable.comments }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>