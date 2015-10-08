<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/custom/lib" prefix="cus" %>

<div class="container">
	<c:forEach items="${datas }" var="d">
		<div class="col-xs-12 col-md-4">
			<h2>${d.application.name }</h2>
			<p>
				${d.application.description }
				<button type="button" class="btn btn-xs btn-primary" appUserId="${d.id }" data-loading-text="Loading..." autocomplete="off">进入</button>
			</p>
		</div>
	</c:forEach>
	<script type="text/javascript">
		$(function(){
			$('button[appUserId]').each(function(){
				var that = $(this);
				var appUserId = that.attr('appUserId');
				that.bind("click",function(){
					$.post(ctx+"/portal/portalLogin",{id:appUserId},function(res){
						if(res.status == true){
							var url = res.url;
							window.location.href=url;
						}else{
							alert(res.msg);
						}
					});
				});
			});
		});
	</script>
</div>	
