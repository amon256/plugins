<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="../bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
	<link href="../bootstrap/3.3.4/css/bootstrap-theme.min.css" rel="stylesheet">
	<title>${(dbTable.name)! }[${(dbTable.comments)! }]</title> 
</head>
<body>
	<div class="container" style="padding-top: 20px;">
		<div class="bs-example bs-example-bg-classes" >
			<p class="bg-info" style="padding: 15px;">${(dbTable.name)! }&nbsp;&nbsp;&nbsp;&nbsp;${(dbTable.comments)! }</p>
		</div>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th width="15%" align="center">列名</th>
					<th width="15%" align="center">类型</th>
					<th width="10%" align="center">长度</th>
					<th width="10%" align="center">精度</th>
					<th width="10%" align="center">小数位</th>
					<th width="10%" align="center">允许空</th>
					<th width="10%" align="center">主键</th>
					<th width="20%" align="center">注释</th>
				</tr>
			</thead>
			<tbody>
				<#if (dbTable.columns)??>
				<#list (dbTable.columns) as col>
					<tr>
						<td>${(col.name)!}</td>
						<td>${(col.dataType)!}</td>
						<td>${(col.dataLength)!}</td>
						<td>${(col.dataPrecision)!}</td>
						<td>${(col.dataScale)!}</td>
						<td>${(col.nullAble)!}</td>
						<td>${(col.primaryKey)!}</td>
						<td>${(col.comments)!}</td>
					</tr>
				</#list>
				</#if>
			</tbody>
		</table>
		<div class="panel panel-default" style="height: 320px;">
			<div class="panel-body">
				<form>
					<div class="form-group">
						<label for="ddl">DDL</label>
						<textarea class="form-control"  style="height: 250px;" readonly id="ddl">${(dbTable.sql)! }</textarea>
				    </div>
			    </form>
			</div>
		</div>
	</div>
</body>
</html>