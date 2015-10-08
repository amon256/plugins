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
			<p class="text-primary">${app.name }(${app.url })</p>
			<form action="${ctx }/index" method="get" class="form-inline">
				<input type="hidden" name="_m" value="${activeMenu.id }"/>
				<input type="hidden" name="appid" value="${app.id }"/>
				<input type="hidden" name="_p" value="${cus:pageId('/app/linkuser') }"/>
				<div class="form-group">
				    <label for="name">姓名:</label>
				    <input type="text" class="form-control" id=""name"" name="name" placeholder="姓名" value="${user.name }">
			  	</div>
				<div class="form-group">
				    <label for="account">账号:</label>
				    <input type="text" class="form-control" id=""name"" name="account" placeholder="账号" value="${user.account }">
			  	</div>
				<button type="submit" class="btn btn-default">查询</button>
			</form>
		</div>
		<div class="contentpanel">
			<table class="table table-striped table-hover table-bordered">
				<thead>
					<tr>
						<th>序号</th>
						<th>姓名</th>
						<th>账号</th>
						<th>关联日期</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty datas }">
							<c:forEach items="${datas }" var="d" varStatus="s">
								<tr>
									<td>${pagination.startIndex + s.index + 1}</td>
									<td>${d.user.name}</td>
									<td>${d.user.account}</td>
									<td><fmt:formatDate value="${d.createTime }" pattern="yyyy.MM.dd"/></td>
									<td>
										<c:choose>
											<c:when test="${d.linked == true }">
												<a role="button" class="btn btn-danger btn-xs" href="javascript:void(0)" appid="${app.id }" userid="${d.user.id }" username="${d.user.name }" linkType="0">解除</a>
											</c:when>
											<c:otherwise>
												<a role="button" class="btn btn-primary btn-xs" href="javascript:void(0)" appid="${app.id }" userid="${d.user.id }" username="${d.user.name }" linkType="1">关联</a>
											</c:otherwise>
										</c:choose>
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
								<jsp:param value="${cus:url(activeMenu.id,'/app/linkuser') }&name=${user.name }&account=${user.account }&appid=${app.id }" name="url"/>
							</jsp:include>
						</td>
					</tr>
				</tfoot>
			</table>
			<script type="text/javascript">
				$(function(){
					$('a[linkType]').each(function(i,a){
						var that = $(this);
						that.bind('click',function(){
							var appid = that.attr('appid');
							var userid = that.attr('userid');
							var username = that.attr('username');
							var linkType = that.attr('linkType');
							var url = ctx + "/app/linksave";
							var msg = "确定关联用户:" + username;
							if(linkType == '0'){
								var url = ctx + "/app/unlink";
								msg = "确定解除用户关联:" + username;
							}
							if(confirm(msg)){
								$.post(url,{appid:appid,userid:userid},function(res){
									alert(res.msg);
									if(res.status == true){
										window.location.href = window.location.href;
									}
								});
							}
						});
					});
				});
			</script>
		</div>
	</div>
