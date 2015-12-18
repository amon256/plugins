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
<script type="text/javascript">
	var webCtx = '${ctx }';
</script>
<script type="text/javascript" src="${ctx }/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${ctx }/ligerUI/js/ligerui.min.js"></script>
<script type="text/javascript" src="${ctx }/js/menu.js"></script>
<title>Upgrade Kit ——Application Upgrade Kit </title>
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
		//树
		$("#menuTree").ligerTree(
				{
					data : indexdata,
					checkbox : false,
					slide : false,
					nodeWidth : 120,
					attribute : [ 'nodename', 'url' ],
					render : function(a) {
						if (!a.isnew)
							return a.text;
						return '<a href="' + a.url + '" target="_blank">'
								+ a.text + '</a>';
					},
					onSelect : function(node) {
						if (!node.data.url)
							return;
						if (node.data.isnew) {
							return;
						}
						var tabid = $(node.target).attr("tabid");
						if (!tabid) {
							tabid = new Date().getTime();
							$(node.target).attr("tabid", tabid)
						}
						mainTabAdd(tabid, node.data.text, node.data.url);
					}
				});
		tab = liger.get("framecenter");
		tree = liger.get("menuTree");
		$("#pageloading").hide();
	});
	function windowHeightChanged(options) {
		if (tab)
			tab.addHeight(options.diff);
	}
	function mainTabAdd(tabid, text, url) {
		tab.addTabItem({
			tabid : tabid,
			text : text,
			url : url
		});
	}
</script>
</head>
<body style="padding: 0px; background: #EAEEF5;">
	<div id="pageloading"></div>
	<div id="topmenu" class="l-topmenu">
		<div class="l-topmenu-logo">Application Upgrade Kit</div>
		<div class="l-topmenu-welcome">
			<a href="${ctx }/logout" class="l-link2" target="_blank">注销</a>
		</div>
	</div>
	<div id="mainLayout" style="width: 99.2%; margin: 0 auto; margin-top: 4px;">
		<div position="left" title="菜单">
			<ul id="menuTree" style="margin-top: 3px;">
		</div>
		<div position="center" id="framecenter"> 
        </div> 
	</div>
</body>
</html>