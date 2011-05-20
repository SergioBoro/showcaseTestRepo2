dojo.provide("course.geo.gfx.Navigation");

dojo.declare("course.geo.gfx.Navigation",null, {

	moveable: null,

	enable: function() {
		this.moveable = new dojox.gfx.Moveable(this.map.engine.group);
	}

});