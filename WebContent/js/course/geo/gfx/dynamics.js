dojo.provide("course.geo.gfx.dynamics");

dojo.require("course.geo.dynamics");
dojo.require("dojox.gfx.decompose");

(function(){
	
var ggfx = course.geo.gfx;

// patch the Placemark class
dojo.extend(ggfx.Placemark, {

	translate: function(coordinates, feature) {
		var baseShapes = feature.baseShapes,
			// convert coordinates to the map projection if it is relevant here
			newGeometry = this.map.getMapGeometry({type: "Point", coordinates:coordinates}),
			newCoordinates = newGeometry.coordinates,
			oldCoordinates = feature.getGeometry().coordinates;
		feature.setGeometry(newGeometry);
		dojo.forEach(baseShapes, function(shape){
			shape.applyLeftTransform({dx:this.getX(newCoordinates[0])-this.getX(oldCoordinates[0]), dy:this.getY(newCoordinates[1])-this.getY(oldCoordinates[1])});
		}, this);
	},

	rotate: function(angle, feature) {
		var baseShapes = feature.baseShapes,
			// rotation angle is the same for all shapes
			lastAngle = dojox.gfx.decompose(baseShapes[0].getTransform()).angle1;

		dojo.forEach(baseShapes, function(shape){
			shape.applyRightTransform(dojox.gfx.matrix.rotate(-lastAngle+angle));
		}, this);
	}

});

}());