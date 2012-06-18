define([
	"djeo/Map",
	"djeo/control/Navigation",
	"djeo/control/Highlight",
	"djeo/control/Tooltip",
	"mil/demos/style",
	"djeo/Circle",
	"mil/Sector",
	"dojo/domReady!"
	
], function(Map, Navigation, Highlight, Tooltip, style){

return function(mapNode, legendNode, data) {

	data.extent = [33,43,45,45];
	data.layers = "webtiles:../../mil/tiles";
	data.style = style;
	data.iconBasePath = "../../mil/demos/"
	
	var map = new Map(mapNode, data);

	map.ready(function(){
		new Navigation(map);
		//new Highlight(map);
		new Tooltip(map);
	});
	
	return {map: map};
};
	
});