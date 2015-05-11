<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
	<link href="bootstrap/3.3.4/css/bootstrap-theme.min.css" rel="stylesheet">
	<title>数据库表结构</title> 
</head>
<body>
	<div class="container">
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th width="10%" align="center">序号</th>
					<th width="35%" align="center">名称</th>
					<th width="45%" align="center">描述</th>
				</tr>
			</thead>
			<tbody>
				<#list dbTables as dbTable>
					<tr>
						<td>${dbTable_index +1 }</td>
						<td><a href="tables/${(dbTable.name)! }.html">${(dbTable.name)! }</a></td>
						<td>${(dbTable.comments)! }</td>
					</tr>
				</#list>
			</tbody>
		</table>
	</div>
</body>
</html>