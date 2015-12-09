/**
 * 默认数据表格配置
 */
var DefaultConfig = {
		dataGridConfig : {
			root : "datas",
			record : "recordCount",
			pageParmName : "currentPage",
			pagesizeParmName : "pageSize",
			sortnameParmName : "sort_",
			sortorderParmName : "order_",
			height:'100%',
			rownumbers : true
		}
}
/**
 * 表格日期类型格式化
 * @param pattern
 * @returns {Function}
 */
function gridDateFormatterFunction(pattern){
	return function(rowdata, index, value){
		return dateFormatter(new Date(value), pattern);
	};
}

/**
 * 日期格式化输出
 * @param value
 * @param pattern
 * @returns
 */
function dateFormatter(value,pattern){
	var o = { 
		 "M+" : value.getMonth()+1,                 //月份 
		 "d+" : value.getDate(),                    //日 
		 "h+" : value.getHours(),                   //小时 
		 "H+" : value.getHours(),                   //小时 
		 "m+" : value.getMinutes(),                 //分 
		 "s+" : value.getSeconds(),                 //秒 
		 "q+" : Math.floor((value.getMonth()+3)/3), //季度 
		 "S"  : value.getMilliseconds()             //毫秒 
	}; 
	if(/(y+)/.test(pattern)){
		pattern=pattern.replace(RegExp.$1, (value.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	}
	for(var k in o){
		if(new RegExp("("+ k +")").test(pattern)){
			pattern = pattern.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		} 
	}
	return pattern; 
}

/**
 * 返回点击打开dialog的超链接标签
 * @param url
 * @param title
 */
var _hrefCallback = {};
function hrefCallbackLabel(text,callback){
	var functionKey = '';
	if(callback && callback instanceof Function){
		functionKey = Math.random() * 100000;
		_hrefCallback[functionKey] = callback;
	}
	var a = '<a href="javascript:void(0)"';
	a += 'onclick=\'hrefCallback("'+functionKey+'")\'>';
	a += text;
	a += "</a>";
	return a;
}

function hrefCallback(functionKey){
	if(functionKey){
		var fun = _hrefCallback[functionKey];
		if(fun){
			fun.apply();
		}
	}
}













