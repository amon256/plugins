<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
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
<script type="text/javascript" src="${ctx }/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${ctx }/ligerUI/js/ligerui.min.js"></script>
<title>应用管理套件 Application Management Suite</title>
<script type="text/javascript">
	var webCtx = '${ctx }';
</script>
<style type="text/css">
body, html {
	height: 100%;
}

body {
	padding: 0px;
	margin: 0;
	overflow: hidden;
}

.l-link {
	display: block;
	height: 26px;
	line-height: 26px;
	padding-left: 10px;
	text-decoration: underline;
	color: #333;
}

.l-link2 {
	text-decoration: underline;
	color: white;
	margin-left: 2px;
	margin-right: 2px;
}

.l-layout-top {
	background: #102A49;
	color: White;
}

.l-layout-bottom {
	background: #E5EDEF;
	text-align: center;
}

#pageloading {
	position: absolute;
	left: 0px;
	top: 0px;
	background: white url('imgs/loading.gif') no-repeat center;
	width: 100%;
	height: 100%;
	z-index: 99999;
}

.l-link {
	display: block;
	line-height: 22px;
	height: 22px;
	padding-left: 16px;
	border: 1px solid white;
	margin: 4px;
}

.l-link-over {
	background: #FFEEAC;
	border: 1px solid #DB9F00;
}

.l-winbar {
	background: #2B5A76;
	height: 30px;
	position: absolute;
	left: 0px;
	bottom: 0px;
	width: 100%;
	z-index: 99999;
}

.space {
	color: #E7E7E7;
}
/* 顶部 */
.l-topmenu {
	margin: 0;
	padding: 0;
	height: 31px;
	line-height: 31px;
	background: url('imgs/top.jpg') repeat-x bottom;
	position: relative;
	border-top: 1px solid #1D438B;
}

.l-topmenu-logo {
	color: #E7E7E7;
	padding-left: 35px;
	line-height: 26px;
	background: url('imgs/topicon.gif') no-repeat 10px 5px;
}

.l-topmenu-welcome {
	position: absolute;
	height: 24px;
	line-height: 24px;
	right: 30px;
	top: 2px;
	color: #070A0C;
}

.l-topmenu-welcome a {
	color: #E7E7E7;
	text-decoration: underline
}

.body-gray2014 #framecenter {
	margin-top: 3px;
}

.viewsourcelink {
	background: #B3D9F7;
	display: block;
	position: absolute;
	right: 10px;
	top: 3px;
	padding: 6px 4px;
	color: #333;
	text-decoration: underline;
}

.viewsourcelink-over {
	background: #81C0F2;
}

.l-topmenu-welcome label {
	color: white;
}

#skinSelect {
	margin-right: 6px;
}
</style>
<script type="text/javascript">
	var tab = null;
	var tree = null;
	var tabItems = [];
	$(function() {
		//布局
		$("#mainLayout").ligerLayout({
			leftWidth : 190,
			height : '100%',
			heightDiff : -5,
			space : 4,
			onHeightChanged : windowHeightChanged
		});
		var height = $(".l-layout-center").height();
		//Tab
		$("#framecenter").ligerTab({
			height : height,
			showSwitchInTab : false,
			showSwitch : false,
			onAfterAddTabItem : function(tabdata) {
				tabItems.push(tabdata);
			},
			onAfterRemoveTabItem : function(tabid) {
				for (var i = 0; i < tabItems.length; i++) {
					var o = tabItems[i];
					if (o.tabid == tabid) {
						tabItems.splice(i, 1);
						break;
					}
				}
			}
		});

		$(".l-link").hover(function() {
			$(this).addClass("l-link-over");
		}, function() {
			$(this).removeClass("l-link-over");
		});
		tab = liger.get("framecenter");
		$("#pageloading").hide();
	});
	function windowHeightChanged(options) {
		if (tab){
			tab.addHeight(options.diff);
		}
	}
	function mainTabAdd(tabid, text, url) {
		
	}
	function changePwd(){
		var tabid = '10001';
		tab.addTabItem({
			tabid : tabid,
			text : "密码修改",
			url : webCtx+'/adminuser/toPassword'
		});
	}
</script>
</head>
<body style="padding: 0px; background: #EAEEF5;">
	<div id="pageloading"></div>
	<div id="topmenu" class="l-topmenu">
		<div class="l-topmenu-logo">应用管理套件 Application Management Suite</div>
		<div class="l-topmenu-welcome">
			<a href="javascript:void(0);" class="l-link2" target="_blank" onclick="changePwd();">密码修改</a>
			<a href="${ctx }/logout" class="l-link2">注销</a>
		</div>
	</div>
	<div id="mainLayout" style="width: 99.2%; margin: 0 auto; margin-top: 4px;">
		<div position="center" id="framecenter"> 
			<div tabid="10" title="本地应用管理">
                <iframe frameborder="0" name="home" id="home" src="${ctx }/application/list"></iframe>
            </div> 
        </div> 
	</div>
</body>
</html>