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
	<div class="container">
		<form action="${pageContext.request.contextPath }/database/tablestructure/tables" method="post">
		  <div class="form-group">
		    <label for="dbType">数据库类型</label>
		    <select class="form-control"  id="dbType" name="dbType">
		    	<c:forEach items="${dbTypes }" var="dbType">
		    		<option value="${dbType }">${dbType.dbType }</option>
		    	</c:forEach>
		    </select>
		  </div>
		  <div class="form-group">
		    <label for="username">用户名</label>
		    <input type="text" class="form-control" name="username" id="username" value="ndmpcs3">
		  </div>
		  <div class="form-group">
		    <label for="password">密码</label>
		    <input type="text" class="form-control" name="password" id="password" value="ndmpcs3">
		  </div>
		  <div class="form-group">
		    <label for="url">连接串</label>
		    <input type="text" class="form-control" name="url" id="url" value="jdbc:oracle:thin:@10.12.12.52:1522:ora10g" placeholder="${dbTypes[0].urlPattern }">
		  </div>
		  <div class="form-group">
		    <label for="filter">表名过滤(表名LIKE参数，多个以空格分隔)</label>
		    <textarea class="form-control" name="filter" id="filter">NDMP%</textarea>
		    <p class="text-success">
		    	例如  :  需要匹配"abc%"或"%cd%"，则填写 "abc%  %cd%"
		    </p>
		  </div>
		  <button type="submit" class="btn btn-default">下一步</button>
		</form>
	</div>
</body>
</html>