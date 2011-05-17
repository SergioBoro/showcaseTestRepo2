dojo.provide("course.geo.gfx.Engine");

dojo.require("course.geo.Engine");

dojo.require("dojox.gfx");
dojo.require("dojox.gfx.move");

dojo.require("course.geo.gfx.Placemark");

(function(){

dojo.declare("course.geo.gfx.Engine", course.geo.Engine, {
	
	type: "gfx",
	
	initialize: function(/* Function */readyFunction) {
		this.surface = dojox.gfx.createSurface(this.map.container, this.map.width, this.map.height);
		this.group = this.surface.createGroup();
		this.polygons = this.group.createGroup();
		this.lines = this.group.createGroup();
		this.points = this.group.createGroup();
		new dojox.gfx.Moveable(this.group);
		
		// initialize some factories
		this.factories["Placemark"] = new course.geo.gfx.Placemark({
			group: this.group,
			polygons:this.polygons,
			lines: this.lines,
			points: this.points,
			map: this.map
		});

		this.initialized = true;
		
		readyFunction();
	},
	
	createContainer: function(parentContainer, featureType) {
		// parentContainer is actually parent group
		return parentContainer.createGroup();
	},

	prerender: function() {
		var mapExtent = this.map.extent;
		//transform map to fit container
		var mapWidth = mapExtent[2] - mapExtent[0];
		var mapHeight = mapExtent[3] - mapExtent[1];
		var scale = Math.min(this.map.width / mapWidth, this.map.height / mapHeight);
		this.group.setTransform([
			dojox.gfx.matrix.scale(scale)
		]);
	},
	
	getTopContainer: function() {
		return this.group;
	},
	
	connect: function(group, event, context, method) {
		return group.connect(event, context, method);
	}
});

}());