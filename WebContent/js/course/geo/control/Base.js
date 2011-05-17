dojo.provide("course.geo.control.Base");

dojo.require("course.geo.utils");

dojo.require("dojo.fx.easing");
dojo.require("dojox.lang.functional.object");
dojo.require("dojox.gfx.fx");


(function(){
	var DEFAULT_DURATION = 400,	// ms
		DEFAULT_EASING   = dojo.fx.easing.backOut,
		df = dojox.lang.functional;

	dojo.declare("course.geo.control.Base", null, {
		
		// array of features, featureCollection or the whole map
		targets: null,
		
		handle: null,

		constructor: function(targets, kwArgs){

			this.anim = {};

			// process common optional named parameters
			if(!kwArgs){ kwArgs = {}; }
			this.duration = kwArgs.duration ? kwArgs.duration : DEFAULT_DURATION;
			this.easing   = kwArgs.easing   ? kwArgs.easing   : DEFAULT_EASING;
			if (kwArgs.map) this.map = kwArgs.map;
			
			this.handle = course.geo.utils.getUniqueNumber();
			
			// process targets
			if (!dojo.isArray(targets)) targets = [targets];
			this.targets = [];
			dojo.forEach(targets, function(target){
				if (this.map && dojo.isString(target)) target = this.map.getFeatureById(target);
				this.targets.push(target);
			}, this);
			// default events to listen to
			this.events = ["onmouseover", "onmouseout"];
		},

		connect: function(){
			dojo.forEach(this.targets, function(target){
				target.connectWithHandle(this.handle, this.events, this, "process");
			}, this);
		},

		disconnect: function(){
			dojo.forEach(this.targets, function(target){
				target.disconnect(this.handle);
			}, this);
		},

		reset: function(){
			//	summary:
			//		Reset the action.
		},

		destroy: function(){
			//	summary:
			//		Do any cleanup needed when destroying parent elements.
			this.disconnect();
			df.forIn(this.anim, function(o){
				df.forIn(o, function(anim){
					anim.action.stop(true);
				});
			});
			this.anim = {};
		}
	});
})();
