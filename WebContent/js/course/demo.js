dojo.provide("course.geo.basic");

dojo.require("djeo.Map");

//dojo.registerModulePath("", "");

dojo.require("djeo.ge.Engine");

dojo.require("djeo.Map");

dojo.require("djeo.control.Navigation");
dojo.require("djeo.control.Highlight");
dojo.require("djeo.control.Tooltip");
dojo.require("djeo.widget.Legend");

dojo.require("kurs.data.russia_geometriesEpsg4326");
dojo.require("kurs.data.russia_geometries");

dojo.require("djeo.util.numeric");
dojo.require("djeo.util.colorbrewer");
dojo.require("djeo.util.jenks");

(function() {
	
var mapEngine = "gfx";

course.geo.basic.make = function(mapNode, legendNode, data) {
	var map, legend;
	var mapStyle = {
		id: "populationDensity",
		styleClass: "populationDensity",
		stroke: "black",
		strokeWidth: 1,
		name: "Style name",
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Reds",
				attr: "indicator1",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: djeo.util.colorbrewer.calculateStyle
			}
		},
		legend: "djeo._getBreaksAreaLegend"
	};

	map = new djeo.Map("map", {
		geometries: kurs.data.russiaGeometries,
		features: data.features,
		style: mapStyle,
		useAttrs: true
	});
	
	map.ready(function(){
		if (legendNode) new djeo.widget.Legend({map: map}, legendNode);
	});
	
	return map;
}

}());


//o.projection = "RUSSIA-ALBERS";