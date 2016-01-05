<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<jsp:include page="../common/common.jsp"></jsp:include>
</head>
<script type="text/javascript">
	$(function(){
		$.metadata.setType("attr", "validate");
		var form = $('#addForm').ligerForm({
			inputWidth: 170, labelWidth: 60, space: 40,
			validate : true,
			fields: [
				{
                  display: "主机", name: "machine_id", textField: "name",type: "combobox", 
                  		editor: {
		                      selectBoxWidth: 550,
		                      selectBoxHeight: 300,
		                      textField: 'host',
		                      valeuField: 'id',
		                      condition: {
		                          prefixID : 'condition_',
		                          fields: [
		                              { label: '', name: 'keyword', type: 'text' }
		                          ]
		                      },
		                      grid: $.extend(true,{},DefaultConfig.dataGridConfig,{
		                          columns: [
						              { display: '主机', name: 'name', align: 'left', width: 100, minWidth: 33 },
						              { display: '地址', name: 'host', minWidth: 120 },
						              { display: '端口', name: 'port', minWidth: 70 }
		                          ], 
		                          url: webCtx+'/hostmachine/listData',
		                          width: 300
		                      })
		                },
		               validate: {required: true}
                },
                {
                    display: "应用", name: "application_id", textField: "name",type: "combobox", 
                    		editor: {
  		                      selectBoxWidth: 550,
  		                      selectBoxHeight: 300,
  		                      textField: 'name',
  		                      valeuField: 'id',
  		                      condition: {
  		                          prefixID : 'condition_',
  		                          fields: [
  		                              { label: '', name: 'keyword', type: 'text' }
  		                          ]
  		                      },
  		                      grid: $.extend(true,{},DefaultConfig.dataGridConfig,{
  		                   		columns: [
  						              { display: '应用名称', name: 'name', align: 'left', width: 150},
  						              { display: '编码', name: 'number', width: 140 }
  		                          ], 
 		                          url: webCtx + "/application/listData",
  		                          width: 300
  	  		                   })
  		                },
  		               validate: {required: true}
                  },
				{ display: '部署标识', name: 'identity',validate : {required:true,rangelength:[3,20]}},
				{ display: '描述', name: 'description',type: 'textarea',validate : {rangelength:[0,500]}}
			],
			buttons : [
				{text: '保存', width: 60, click: function(){
					var submitData = form.getData();
					for(var k in submitData){
						if(k.indexOf('_') > 0){
							submitData[k.split('_').join('.')] = submitData[k];
							delete submitData[k];
						}
					}
					$.ligerDialog.waitting('正在保存中,请稍候...');
					$.post(webCtx + '/deployinfo/addSave',submitData,function(data){
						$.ligerDialog.closeWaitting();
						if(data.status == 'success'){
							$.ligerDialog.success("保存成功","提示",function(){
								art.dialog.close();
							});
						}else{
							var msg = data.msg;
							if(data.validateErrors && data.validateErrors.length > 0){
								for(var i = 0; i < data.validateErrors.length; i++){
									var err = data.validateErrors[i];
									msg += '<br/><span style="color:red;"><label style="color:blue;">'+err.fieldName+'</label>'+err.message+'</span>';
								}
							}
							$.ligerDialog.error(msg);
						}
					});
				}},
				{text: '取消', width: 60, click: function(){
					art.dialog.close();
				}}
			]
		});
	});
</script>
<body  style="overflow:hidden;padding: 5px;min-width: 700px;min-height: 460px;">
	<div id="main">
		<div position="center">
				<form id="addForm" >
				</form>
			</div>
        </div> 
	</div>
</body>
</html>