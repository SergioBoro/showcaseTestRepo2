dojo.provide("course.geo.demo");

dojo.require("djeo.Map");

//dojo.registerModulePath("", "");

dojo.require("djeo.control.Navigation");
dojo.require("djeo.control.Highlight");
dojo.require("djeo.control.Tooltip");
dojo.require("djeo.widget.Legend");

//dojo.require("kurs.data.russia_geometriesEpsg4326");
dojo.require("kurs.data.russia_geometries");

dojo.require("djeo.util.numeric");
dojo.require("djeo.util.colorbrewer");
dojo.require("djeo.util.jenks");

(function() {

var defaultMapEngine = "gfx";

course.geo.demo.make = function(mapNode, legendNode, data) {
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

	if (!data.geometries) data.geometries = kurs.data.russiaGeometries;
	data.style = mapStyle;

	map = new djeo.Map(mapNode, data);

	map.ready(function(){
		if (legendNode) legend = new djeo.widget.Legend({map: map}, legendNode);
	});
	
	return {map: map, legend: legend};
}

}());


//o.projection = "RUSSIA-ALBERS";