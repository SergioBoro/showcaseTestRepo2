dojo.provide("course.geo.util");

(function(){
	
var u = course.geo.util;

var idCounter = 0;

u.getUniqueNumber = function() {
	idCounter += 1;
	return idCounter;
}

u.normalizeCallback = function(feature, event, context, method) {
	method = method ? dojo.hitch(context, method) : context;
	return function(nativeEvent){
		method({
			type: event,
			event: nativeEvent,
			feature: feature
		});
	};
}

u.bbox = {
	extend: function(extendWhat, extendWith) {
		if (extendWith) {
			// left
			if (extendWith[0] < extendWhat[0]) extendWhat[0] = extendWith[0];
			// bottom
			if (extendWith[1] < extendWhat[1]) extendWhat[1] = extendWith[1];
			if (extendWith.length == 4) {
				// right
				if (extendWith[2] > extendWhat[2]) extendWhat[2] = extendWith[2];
				// top
				if (extendWith[3] > extendWhat[3]) extendWhat[3] = extendWith[3];
			}
			else { // extendWith.length == 4 (e.g. a point)
				if (extendWith[0] > extendWhat[2]) extendWhat[2] = extendWith[0];
				// top
				if (extendWith[1] > extendWhat[3]) extendWhat[3] = extendWith[1];
			}
		}
		return extendWhat;
	},
	
	get: function(geometry) {
		var bbox,
			depth = 0;
		switch (geometry.type) {
			case "Point":
				depth = 1;
				break;
			case "LineString":
				depth = 2;
				break;
			case "Polygon":
				depth = 3;
				break;
			case "MultiLineString":
				depth = 3;
				break;
			case "MultiPolygon":
				depth = 4;
				break;
		}
		if (depth) {
			bbox = [Infinity,Infinity,-Infinity,-Infinity];
			_calculateBbox(depth, geometry.coordinates, bbox);
			geometry.bbox = bbox;
		}
		return bbox;
	}
	
}
	
// calculate bbox for:
// a single point (depth == 1)
// an array of points (depth == 2)
// an array of arrays of points (depth == 3)
// an array of arrays of arrays of points (depth == 4)
	
var _calculateBbox = function(depth, /* Array */entities, bbox) {
	if (depth == 1) {
		u.bbox.extend(bbox, entities);
	}
	else {
		dojo.forEach(entities, function(entity){
			_calculateBbox(depth-1, entity, bbox);
		});
	}
}

}());