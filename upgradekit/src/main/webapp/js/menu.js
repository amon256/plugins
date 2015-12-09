var indexdata = [
{
	text : "个人信息",
	isexpand : false,
	children : [
		{
			url : webCtx + "/adminuser/toProfile",
			text : "个人信息"
		},{
			url : webCtx + "/adminuser/toPassword",
			text : "密码修改"
		}
	]
},                 
/*{
	url : webCtx + "/adminuser/list",
	text : "管理员"
},*/
{
	url : webCtx + "/application/list",
	text : "应用管理"
}/*, {
	text : '过滤器',
	isexpand : false,
	children : [ {
		url : "demos/filter/filter.htm",
		text : "自定义查询"
	}, {
		url : "demos/filter/filterwin.htm",
		text : "在窗口显示"
	}, {
		url : "demos/filter/grid.htm",
		text : "配合表格"
	} ]
} */];
