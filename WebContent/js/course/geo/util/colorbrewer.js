dojo.provide("course.geo.util.colorbrewer");

dojo.require("course.geo.util.colorbrewer_data");

(function(){

var u = course.geo.util,
	cb = u.colorbrewer,
	cbd = u.colorbrewer_data;

cb.getStyle = function(feature, style, styleFunctionDef) {
	var kwArgs = styleFunctionDef.options,
		getBreaks = dojo.isString(kwArgs.getBreaks) ? dojo.getObject(kwArgs.getBreaks) : kwArgs.getBreaks,
		breaks = getBreaks(feature, styleFunctionDef),
		attrValue = feature.get(kwArgs.attr);

	for (var i=0; i<kwArgs.numClasses; i++) {
		if (i==0) {
			if (breaks[0]<=attrValue && attrValue<=breaks[1]) break;
		}
		else if (breaks[i]<attrValue && attrValue<=breaks[i+1]) break;
	}
	if (i<kwArgs.numClasses) style.fill = cbd["seq"][kwArgs.numClasses][kwArgs.colorSchemeName].colors[i];
};

cb.getLegend = function(domContainer, style, features, name) {
	var html = "",
		kwArgs = style.styleFunction.options;
	dojo.forEach(features, function(feature){
		var breaks = feature._breaks;
		if (name) html += name+"<br>";
		html += "<div style='padding-left:20px'>";
		for (var i=0; i<kwArgs.numClasses; i++) {
			var color = cbd["seq"][kwArgs.numClasses][kwArgs.colorSchemeName].colors[i];
			html += "<span style='background-color:"+ color +"'>&nbsp;&nbsp;&nbsp;&nbsp;</span> "+breaks[i]+"..."+breaks[i+1]+"<br>";
		}
		html += "</div>";
	});
	domContainer.innerHTML = html;
};

}());