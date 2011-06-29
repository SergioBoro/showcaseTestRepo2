dojo.provide("course.geo.dynamics");

dojo.require("course.geo.Placemark");

(function(){
	
var g = course.geo,
	u = g.util;
g.setDependency("dynamics");

// patch the Placemark class
dojo.extend(g.Placemark, {

	translate: function(coordinates) {
		// get factory for the Placemark
		var factory = this.map.engine.getFactory(this.type);
		if (factory.translate) factory.translate(coordinates, this);
	},

	rotate: function(radians) {
		// get factory for the Placemark
		var factory = this.map.engine.getFactory(this.type);
		if (factory.rotate) factory.rotate(radians, this);
	},

	rotateg: function(degrees) {
		this.rotate( u.degToRad(degrees) );
	}
});

}());