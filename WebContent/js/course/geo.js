dojo.provide("course.geo");
dojo.require("djeo.Map");

(function(){
var defaultManagerModule = "course.geo.basic",
	defaultManagerFunction = "make";

var g = course.geo;

var mapRegistry = {maps:{}, data:{}};

function convertData(data) {

	if (dojo.isString(data)) features = dojo.fromJson(data);

	if (data.layers) {
		var layers = data.layers;
		data.features = layers;
		delete data.layers;
		
		dojo.forEach(layers, function(layer){
			delete layer.type;
			if (layer.features) dojo.forEach(layer.features, function(feature) {
				var coords, type;
				if (feature.pointCoords) {
					coords = feature.pointCoords;
					delete feature.pointCoords;
					type = "Point";
				}
				else if (feature.polygonCoords) {
					coords = feature.polygonCoords;
					delete feature.polygonCoords;
					type = "Polygon";
				}
				else if (feature.multiPolygonCoordinates) {
					coords = feature.multiPolygonCoords;
					delete feature.multiPolygonCoords;
					type = "MultiPolygon";
				}
				if (coords) {
					feature.coords = coords;
					feature.type = type;
				}
				// process style
				var style = feature.style;
				if (style && dojo.isString(style)) {
					if (style.charAt(0)=="#") feature.style = {fill: style};
				}
			});
		});
	}
}

g.makeMap = function(mapDivId, mapLegendId, data, options) {
    if (dojo.isString(data)) data = dojo.fromJson(data);
	convertData(data);
    
    var mapNode = dojo.byId(mapDivId);
    if (!mapNode) return;

    g.destroyMap(mapDivId);
	
	var managerModule = options.managerModule ? options.managerModule : defaultManagerModule;
	dojo.require(managerModule);
	var managerFunction = options.managerFunction ? options.managerFunction : defaultManagerFunction;
	if (managerFunction) {
		var map = managerFunction(mapNode, dojo.byId(mapLegendId), data)
		mapRegistry[mapDivId] = {map: map};
	}
}

g.destroyMap = function(id) {
    if (mapRegistry[id]) {
		mapRegistry[id].map.destroy();
		if (mapRegistry[id].legend) mapRegistry[id].legend.destroy(true);
		delete mapRegistry[id];
    }
}

})();