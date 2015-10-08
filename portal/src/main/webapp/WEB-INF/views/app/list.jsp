<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/custom/lib" prefix="cus" %>

	<div class="container-fluid">
		<c:if test="${not empty msg }">
			<div class="alert alert-warning alert-dismissible" role="alert">
			  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			  	${msg }
			</div>
		</c:if>
		<c:if test="${not empty succ }">
			<div class="alert alert-success alert-dismissible" role="alert">
			  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			  	${succ }
			</div>
		</c:if>
		<div class="querypanel">
			<form action="${ctx }/index" method="get" class="form-inline">
				<input type="hidden" name="_m" value="${activeMenu.id }"/>
				<input type="hidden" name="_p" value="${cus:pageId('/app/list') }"/>
				<div class="form-group">
				    <label for=""name"">应用名:</label>
				    <input type="text" class="form-control" id=""name"" name="name" placeholder="应用名" value="${user.name }">
			  	</div>
				<button type="submit" class="btn btn-default">查询</button>
				<a role="button" class="btn btn-info" href="${ctx }/${cus:url(activeMenu.id,'/app/toadd') }">新增</a>
			</form>
		</div>
		<div class="contentpanel">
			<table class="table table-striped table-hover table-bordered">
				<thead>
					<tr>
						<th>序号</th>
						<th>应用名</th>
						<th>URL</th>
						<th>加入日期</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty datas }">
							<c:forEach items="${datas }" var="d" varStatus="s">
								<tr>
									<td>${pagination.startIndex + s.index + 1}</td>
									<td>${d.name}</td>
									<td>${d.url}</td>
									<td><fmt:formatDate value="${d.createTime }" pattern="yyyy.MM.dd"/></td>
									<td>
										<a href="${ctx }/${cus:url(activeMenu.id,'/app/toupdate') }&id=${d.id}">修改</a>
										<a href="${ctx }/${cus:url(activeMenu.id,'/app/linkuser') }&appid=${d.id}">关联用户</a>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="5">没有更多数据</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5">
							<jsp:include page="../common/pagination.jsp" flush="true">
								<jsp:param value="${cus:url(activeMenu.id,'/app/list') }&name=${app.name }" name="url"/>
							</jsp:include>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>