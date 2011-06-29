dojo.provide("course.geo.ge.dynamics");

dojo.require("course.geo.dynamics");

(function(){
	
var g = course.geo,
	gge = g.ge,
	u = g.util;

// patch the Placemark class
dojo.extend(gge.Placemark, {

	translate: function(coordinates, feature, animate) {
		var placemark = feature.baseShapes[0],
			// convert coordinates to the map projection if it is relevant here
			newGeometry = this.map.getMapGeometry({type: "Point", coordinates:coordinates}),
			newCoordinates = newGeometry.coordinates,
			oldCoordinates = feature.getGeometry().coordinates;

		feature.setGeometry(newGeometry);
		var point = placemark.getGeometry();
		point.setLatLngAlt (newCoordinates[1], newCoordinates[0], 200);
	},

	rotate: function(angle, feature) {
		var placemark = feature.baseShapes[0],
			iconStyle = placemark.getStyleSelector().getIconStyle();

		iconStyle.setHeading(u.radToDeg(angle));
	}

});

}());