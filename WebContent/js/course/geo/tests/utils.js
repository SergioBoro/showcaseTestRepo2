
var getLocationHrefParams = function(url){
	var hrefParams = {};
	if (url.indexOf("?") > -1) {
		var str = url.substr(url.indexOf("?") + 1);
		var parts = str.split(/&/);
		for (var i = 0; i < parts.length; i++) {
			var split = parts[i].split(/=/);
			var key = split[0];
			var value = split[1];
			hrefParams[key] = value;
		}
	}
	return hrefParams;
}

var hrefParams = getLocationHrefParams(window.location.href);
if (hrefParams.gfxRenderer) djConfig.gfxRenderer = hrefParams.gfxRenderer;
if (hrefParams.mapEngine) djConfig.mapEngine = hrefParams.mapEngine;