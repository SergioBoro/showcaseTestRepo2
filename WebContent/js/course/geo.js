dojo.provide("course.geo");

dojo.require("djeo.Map");

(function(){
var defaultManagerModule = "course.demo",
	defaultManagerFunction = "make",
	defaultMapEngine = "gfx";

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
					type = "Point";
				}
				else if (feature.polygonCoords) {
					coords = feature.polygonCoords;
					type = "Polygon";
				}
				else if (feature.multiPolygonCoordinates) {
					coords = feature.multiPolygonCoords;
					type = "MultiPolygon";
				}
				// clean feature object
				delete feature.pointCoords, feature.polygonCoords, feature.multiPolygonCoords;
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
    if (dojo.isString(options)) options = dojo.fromJson(options);
	convertData(data);
    
    var mapNode = dojo.byId(mapDivId);
    if (!mapNode) return;

    g.destroyMap(mapDivId);

	if (!options) options = {};
	var managerModule = options.managerModule ? options.managerModule : defaultManagerModule;
	dojo.require(managerModule);
	managerModule = dojo.getObject(managerModule);
	var managerFunction = options.managerFunction ? options.managerFunction : defaultManagerFunction;
	if (managerFunction) {
		// register dojo modules
		if (options.registerModules) {
			dojo.forEach(options.registerModules, function(module){
				dojo.registerModulePath(module[0], module[1]);
			});
		}
		// clean up options
		delete options.managerModule, options.managerFunction, options.registerModules;
		dojo.mixin(data, options);
		// patch data
		if (!data.mapEngine) data.mapEngine = defaultMapEngine;
		data.useAttrs = true;
		mapRegistry[mapDivId] = managerModule[managerFunction](mapNode, dojo.byId(mapLegendId), data);
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