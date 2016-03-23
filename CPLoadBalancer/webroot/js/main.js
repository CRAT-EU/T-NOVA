! function($) {

	$(function() {
		
		function updateData(){

		$.get( "lb", function( data ) {	
			$("#cpinstances_container").empty();
			$("#cpinstances_container").append("<div class='row'>");
			$.each( data, function( i, val ) {
			$("#cpinstances_container").append("<div class='col-sm-4'>" + getHTMLPanel(val) + "</div>");
		});
			$("#cpinstances_container").append("</div>");
		}, "json" );

		setTimeout(updateData, 10000);

		}
		
	updateData();

	function getHTMLPanel(val){
		var result = "<div class='panel panel-primary'>";
		result+="<div class='panel-heading'>";
		result+="<h3><strong>IP: </strong>" + val.instanceIp +"</h3>";
		result+="</div>";
		result+="<div class='panel-body'>";

		if (val.metrics){

			if (val.metrics.freeRam) {
				result+="<h4>Memory</h4>";
				result+="<strong>Used memory: </strong>";
				var usedRAMperc = parseFloat(val.metrics.usedRam * 100 / (val.metrics.usedRam + val.metrics.freeRam)).toFixed(2);
				result+="<div class='progress'><div class='progress-bar progress-bar-"+percToColor(usedRAMperc)+"' role='progressbar' aria-valuenow='"+usedRAMperc+"' aria-valuemin='0' aria-valuemax='100' style='width: "+usedRAMperc+"%;padding-left: 5px; text-align:left;'>";
				result+=val.metrics.usedRam +"MB  / "+(val.metrics.totalRam)+"MB ("+ usedRAMperc+"%)";
				result+="</div></div>";
			}

			if (val.metrics.loadAvg1m||val.metrics.loadAvg5m||val.metrics.loadAvg15m)
				result+="<h4>Load Average</h4>";

			if (val.metrics.loadAvg1m != -1 ) {
				var loadAvg1m = parseFloat(val.metrics.loadAvg1m * 100).toFixed(2);
				result+="<strong>Last 1 min: </strong>";
				result+="<div class='progress'><div class='progress-bar progress-bar-"+percToColor(loadAvg1m)+"' role='progressbar' aria-valuenow='"+loadAvg1m+"' aria-valuemin='0' aria-valuemax='100' style='width: "+loadAvg1m+"%;padding-left: 5px; text-align:left;'>";
				result+=loadAvg1m +"%";
				result+="</div></div>";
			}

			if (val.metrics.loadAvg5m != -1) {
				var loadAvg5m = parseFloat(val.metrics.loadAvg5m * 100).toFixed(2);
				result+="<strong>Last 5 min: </strong>"
				result+="<div class='progress'><div class='progress-bar progress-bar-"+percToColor(loadAvg5m)+"' role='progressbar' aria-valuenow='"+loadAvg1m+"' aria-valuemin='0' aria-valuemax='100' style='width: "+loadAvg5m+"%;padding-left: 5px; text-align:left;'>";
				result+=loadAvg5m +"%";
				result+="</div></div>";
			}

			if (val.metrics.loadAvg15m != -1) {
				var loadAvg15m = parseFloat(val.metrics.loadAvg15m * 100).toFixed(2);
				result+="<strong>Last 15 min: </strong>";
				result+="<div class='progress'><div class='progress-bar progress-bar-"+percToColor(loadAvg15m)+"' role='progressbar' aria-valuenow='"+loadAvg1m+"' aria-valuemin='0' aria-valuemax='100' style='width: "+loadAvg15m+"%;padding-left: 5px; text-align:left;'>";
				result+=loadAvg15m +"%";
				result+="</div></div>";
			}
			//result+="<div class='alert alert-success'><strong>Free memory:</strong>"+val.metrics.freeRam+"</div>";
		}


		if (val.switches && val.switches.length > 0){
			result+="<h4>Switches</h4>";
			result+="<div style='display:table'>";
			$.each( val.switches, function( j, sw ) {
				result+="<div class='input-group input-group-sm switch "+(sw.role == 3 ? "opacity" : "")+"'>";
				result+="<span class='input-group-addon "+roleToColor(sw.role)+"'>" + roleToString(sw.role) + "</span>";
				result+="<input type='text' class='form-control' value='ID:"+sw.id+"'/>";
				result+="</div>";
			});
			result+="</div>";
		}

		if(val.logs && val.logs.length > 0){
			result+="<h4>Log</h4>"
			result+="<pre style='width:100%;'>";
					$.each( val.logs, function( i, log ) {
						result+=log+'\n';
					});
			result+="</pre>";
		}

		result+="</div>";
		result+="</div>";
		return result;
	}

	});

	function percToColor(perc){
		if (perc > 75)
			return "danger";
		if (perc > 50)
			return "warning";
		return "success";

	}

	function roleToColor(role){
		if (role == 1)
			return "";
		if (role == 2)
			return "success";
		if (role == 3)
			return "warning";

	}

	function roleToString(role){
		if (role == 0)
			return "-";
		if (role == 1)
			return "E";
		if (role == 2)
			return "M";
		if (role == 3)
			return "S";

	}

	function bytesToSize(bytes) {
		var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
		if (bytes == 0) return '0 Byte';
		var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
		return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
	};

}(window.jQuery);
