dojo.provide("course.geo.util.colorbrewer");

dojo.require("course.geo.util.numeric");
dojo.require("course.geo.util.colorbrewer_data");

(function(){

var u = course.geo.util,
	n = u.numeric,
	cb = u.colorbrewer,
	cbd = u.colorbrewer_data;

cb.getStyle = function(feature, style, styleFunctionDef) {
	var kwArgs = styleFunctionDef.options,
		getBreaks = dojo.isString(kwArgs.getBreaks) ? dojo.getObject(kwArgs.getBreaks) : kwArgs.getBreaks,
		attrValue = feature.get(kwArgs.attr),
		featureContainer = feature.parent,
		breaks = featureContainer._breaks;
	
	if (!breaks || styleFunctionDef.updated > featureContainer._breaksTimestamp) {
		breaks = getBreaks(featureContainer, styleFunctionDef.options);
		
		// store calculated breaks for the use by other features that are children of the featureContainer
		featureContainer._breaks = breaks;
		featureContainer._breaksTimestamp = (new Date()).getTime();
	}

	var breakIndex = n.getBreakIndex(breaks, kwArgs.numClasses, attrValue);
	if (breakIndex<kwArgs.numClasses) style.fill = cbd["seq"][kwArgs.numClasses][kwArgs.colorSchemeName].colors[breakIndex];
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