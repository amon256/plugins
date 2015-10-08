<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/custom/lib" prefix="cus" %>
	<div class="container-fluid">
		<div class="editpanel">
			<form id="editUser" role="form" class="form-horizontal" action="${ctx }/${cus:url(activeMenu.id,'/app/update')}" method="post">
				<input type="hidden" name="id" value="${app.id }"/>
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
				<div class="form-group">
					<label for="name" class="col-sm-2 control-label">应用名：</label>
					<div class="col-sm-6">
						<input type="text" class="form-control" id="name" name="name" value="${app.name }" />
					</div>
				</div>
				<div class="form-group">
					<label for="url" class="col-sm-2 control-label">URL：</label>
					<div class="col-sm-6">
						<input type="url" class="form-control" id="url" name="url" value="${app.url }" />
					</div>
				</div>
				<div class="form-group">
					<label for="ticketName" class="col-sm-2 control-label">凭证KEY：</label>
					<div class="col-sm-6">
						<input type="text" class="form-control" id="ticketName" name="ticketName" value="${app.ticketName }" />
					</div>
				</div>
				<div class="form-group">
					<label for="description" class="col-sm-2 control-label">描述：</label>
					<div class="col-sm-6">
						<textarea rows="4" cols="120" class="form-control" id="description" name="description">${app.description }</textarea>
					</div>
				</div>
				<button type="submit" class="btn btn-primary">保存</button>
			</form>
		</div>
	</div>
