<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String menuId = request.getParameter("menuId");
request.setAttribute("menuId", menuId);
%>
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span>
			</button>
			<span class="navbar-brand">工具箱</span>
		</div>
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li class="dropdown<c:if test="${menuId == 'menu_tablestructure' or menuId == 'menu_connecttest' or menuId == 'menu_scriptexecute' }"> active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">数据库<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li id="menu_tablestructure" class="<c:if test="${menuId == 'menu_tablestructure'}">active</c:if>"><a href="#">表结构报告</a></li>
						<li class="divider"></li>
						<li id="menu_connecttest" class="<c:if test="${menuId == 'menu_connecttest'}">active</c:if>"><a href="#">连接测试</a></li>
						<li class="divider"></li>
						<li id="menu_scriptexecute" class="<c:if test="${menuId == 'menu_scriptexecute' }">active</c:if>"><a href="#">脚本执行</a></li>
					</ul>
				</li>
				<li class="class="<c:if test="${menuId == 'other' }">active</c:if>""><a href="#">其它</a></li>
			</ul>
		</div>
	</div>
</nav>