<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/custom/lib" prefix="cus" %>
<div class="container-fluid">
	<div class="editpanel">
		<form role="form" class="form-horizontal" action="${ctx }/${cus:url(activeMenu.id,'/user/register')}" method="post">
			<input type="hidden" name="__TOKEN__" value="${__TOKEN__ }"/>
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
				<label for="account" class="col-sm-2 control-label">账号：</label>
				<div class="col-sm-6">
					<input type="text" class="form-control" id="account" name="account"  value="${user.account }" />
				</div>
			</div>
			<div class="form-group">
				<label for="password" class="col-sm-2 control-label">密码：</label>
				<div class="col-sm-6">
					<input type="text" class="form-control" id="password" name="password"  value="${user.password }" />
				</div>
			</div>
			<div class="form-group">
				<label for="name" class="col-sm-2 control-label">姓名：</label>
				<div class="col-sm-6">
					<input type="text" class="form-control" id="name" name="name"  value="${user.name }" />
				</div>
			</div>
			<div class="form-group">
				<label for="nickName" class="col-sm-2 control-label">昵称：</label>
				<div class="col-sm-6">
					<input type="text" class="form-control" id="nickName" name="nickName" value="${user.nickName }" />
				</div>
			</div>
			<div class="form-group">
				<label for="mobile" class="col-sm-2 control-label">手机号码：</label>
				<div class="col-sm-6">
					<input type="text" class="form-control" id="mobile" name="mobile" value="${user.mobile }" />
				</div>
			</div>
			<div class="form-group">
				<label for="roles" class="col-sm-2 control-label">头像：</label>
				<div class="col-sm-6">
					<label class="radio-inline">
					  <input type="radio" name="headPhoto"  <c:if test="${user.headPhoto == '/adminLTE/img/avatar.png' or user.headPhoto == null}">checked="checked"</c:if> value="/adminLTE/img/avatar.png">
					  <img src="${ctx}/adminLTE/img/avatar.png" class="img-circle" style="width: 40px;height:40px;" alt="User Image" />
					</label>
					<label class="radio-inline">
					  <input type="radio" name="headPhoto"  <c:if test="${user.headPhoto == '/adminLTE/img/avatar2.png'}">checked="checked"</c:if> value="/adminLTE/img/avatar2.png">
					  <img src="${ctx}/adminLTE/img/avatar2.png" class="img-circle" style="width: 40px;height:40px;" alt="User Image" />
					</label>
					<label class="radio-inline">
					  <input type="radio" name="headPhoto"  <c:if test="${user.headPhoto == '/adminLTE/img/avatar3.png'}">checked="checked"</c:if> value="/adminLTE/img/avatar3.png">
					  <img src="${ctx}/adminLTE/img/avatar3.png" class="img-circle" style="width: 40px;height:40px;" alt="User Image" />
					</label>
					<label class="radio-inline">
					  <input type="radio" name="headPhoto"  <c:if test="${user.headPhoto == '/adminLTE/img/avatar04.png'}">checked="checked"</c:if> value="/adminLTE/img/avatar04.png">
					  <img src="${ctx}/adminLTE/img/avatar04.png" class="img-circle" style="width: 40px;height:40px;" alt="User Image" />
					</label>
					<label class="radio-inline">
					  <input type="radio" name="headPhoto"  <c:if test="${user.headPhoto == '/adminLTE/img/avatar5.png'}">checked="checked"</c:if> value="/adminLTE/img/avatar5.png">
					  <img src="${ctx}/adminLTE/img/avatar5.png" class="img-circle" style="width: 40px;height:40px;" alt="User Image" />
					</label>
				</div>
			</div>
			<button type="submit" class="btn btn-primary">确定注册</button>
		</form>
	</div>
</div>