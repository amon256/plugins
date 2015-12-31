<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String port = request.getServerPort()==80?"":":"+request.getServerPort();
String ctx = request.getScheme()+"://"+request.getServerName()+port+request.getContextPath();
request.setAttribute("ctx", ctx);
%>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="shortcut icon" href="${ctx }/favicon.ico">
<link href="${ctx }/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet">
<link href="${ctx }/ligerUI/skins/Gray/css/all.css" rel="stylesheet">
<link href="${ctx }/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${ctx }/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${ctx }/jquery/jquery.form.js"></script>
<script type="text/javascript" src="${ctx }/ligerUI/js/ligerui.min.js"></script>
<script type="text/javascript" src="${ctx }/jquery-validation/jquery.validate.min.js"></script>
<script type="text/javascript" src="${ctx }/jquery-validation/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx }/jquery-validation/messages_cn.js"></script>
<script type="text/javascript" src="${ctx }/artDialog/artDialog.js?skin=simple"></script>
<script type="text/javascript" src="${ctx }/artDialog/plugins/iframeTools.js"></script>
<script type="text/javascript" src="${ctx }/js/json2.js"></script>
<script type="text/javascript" src="${ctx }/js/common.js"></script>
<script type="text/javascript">
	var webCtx = '${ctx }';
</script>
<title>应用管理套件</title>