var initSystemInfo;
var seriesCPU, seriesHeap, seriesNonHeap;
$(function() {
	$.getJSON("machine", function(data) {
		initSystemInfo = data;
		var ids = [ "name", "arch", "version", "availableProcessors", "vmName",
				"vmVendor", "vmVersion", "vmStartTime", "initHeapMemory",
				"maxHeapMemory", "initNonHeapMemory", "maxNonHeapMemory" ];
		for (var i = 0; i < ids.length; i++) {
			if (data[ids[i]]) {
				$('#' + ids[i]).text(data[ids[i]]);
			}
		}
		if (data.systemProperties) {
			var con = $('#systemProperties');
			for ( var key in data.systemProperties) {
				con.append("<code>" + key + "</code> = <var>"
						+ data.systemProperties[key] + "</var><br/>");
			}
		}
		createCpuChart();
		createMemChart();
	});
	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});
});

function triggerLoadUsage() {
	if (seriesCPU && seriesHeap && seriesNonHeap) {
		$.getJSON('usage', function(data) {
			var x = data['time'];
			var y = data['cpuUsage'];
			seriesCPU.addPoint([ x, y ], true, true);

			y = data['heapUsage'];
			seriesHeap.addPoint([ x, y ], true, true);

			y = data['nonHeapUsage'];
			seriesNonHeap.addPoint([ x, y ], true, true);

			setTimeout('triggerLoadUsage()', 3000);
		});
	}
}

function createMemChart() {
	$('#memPerf').highcharts(
			{
				chart : {
					type : 'area',
					width : 600,
					animation : Highcharts.svg, // don't animate in old IE
					marginRight : 10,
					events : {
						load : function() {
							seriesHeap = this.series[0];
							seriesNonHeap = this.series[1];
							triggerLoadUsage();
						}
					}
				},
				credits : {
					enabled : false
				},
				title : {
					text : '内存使用率'
				},
				xAxis : {
					type : 'datetime',
					tickPixelInterval : 50
				},
				yAxis : {
					title : {
						text : ''
					},
					max : Math.max(initSystemInfo.maxHeapMemory,
							initSystemInfo.maxNonHeapMemory),
					min : 0,
					labels : {
						formatter : function() {
							return this.value + 'mb';
						}
					},
					plotLines : [ {
						value : 0,
						width : 1,
						color : '#808080'
					} ]
				},
				tooltip : {
					formatter : function() {
						return '<b>' + this.series.name + '</b> use '
								+ Highcharts.numberFormat(this.y, 2) + 'Mb at '
								+ Highcharts.dateFormat('%H:%M:%S', this.x);
					}
				},
				legend : {
					enabled : false
				},
				exporting : {
					enabled : false
				},
				plotOptions : {
					area : {
						marker : {
							enabled : false,
							symbol : 'circle',
							radius : 20,
							states : {
								hover : {
									enabled : false
								}
							}
						}
					}
				},
				series : [ {
					name : '堆',
					data : (function() {
						// generate an array of random data
						var data = [], time = (new Date()).getTime(), i;

						for (i = -19; i <= 0; i += 1) {
							data.push({
								x : time + i * 1000,
								y : 0
							});
						}
						return data;
					}())
				}, {
					name : '非堆',
					data : (function() {
						// generate an array of random data
						var data = [], time = (new Date()).getTime(), i;

						for (i = -19; i <= 0; i += 1) {
							data.push({
								x : time + i * 1000,
								y : 0
							});
						}
						return data;
					}())
				} ]
			});
}

function createCpuChart() {
	$('#cpuPerf').highcharts(
			{
				chart : {
					type : 'area',
					width : 600,
					animation : Highcharts.svg, // don't animate in old IE
					marginRight : 10,
					events : {
						load : function() {
							seriesCPU = this.series[0];
							triggerLoadUsage();
						}
					}
				},
				credits : {
					enabled : false
				},
				title : {
					text : 'CPU使用率'
				},
				xAxis : {
					type : 'datetime',
					tickPixelInterval : 50
				},
				yAxis : {
					title : {
						text : ''
					},
					labels : {
						formatter : function() {
							return this.value + '%';
						}
					},
					max : 100,
					plotLines : [ {
						value : 0,
						width : 1,
						color : '#808080'
					} ]
				},
				tooltip : {
					formatter : function() {
						return '<b>' + this.series.name + '</b> use '
								+ Highcharts.numberFormat(this.y, 2) + '% at '
								+ Highcharts.dateFormat('%H:%M:%S', this.x);
					}
				},
				legend : {
					enabled : false
				},
				exporting : {
					enabled : false
				},
				plotOptions : {
					area : {
						marker : {
							enabled : false,
							symbol : 'circle',
							radius : 20,
							states : {
								hover : {
									enabled : false
								}
							}
						}
					}
				},
				series : [ {
					name : 'CPU',
					data : (function() {
						// generate an array of random data
						var data = [], time = (new Date()).getTime(), i;

						for (i = -19; i <= 0; i += 1) {
							data.push({
								x : time + i * 1000,
								y : 0
							});
						}
						return data;
					}())
				} ]
			});
}