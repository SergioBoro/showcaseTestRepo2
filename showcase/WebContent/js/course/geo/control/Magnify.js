dojo.provide("course.geo.control.Magnify");

dojo.require("course.geo.control.Base");
dojo.require("dojox.gfx.matrix");
dojo.require("dojo.fx");


(function(){

var DEFAULT_SCALE = 1.2,
	m = dojox.gfx.matrix,
	gf = dojox.gfx.fx;

dojo.declare("course.geo.control.Magnify", course.geo.control.Base, {
	//	summary:
	//		Create an action that magnifies the object the action is applied to.

	// the data description block for the widget parser
	defaultParams: {
		duration: 400,	// duration of the action in ms
		easing:   dojo.fx.easing.backOut,	// easing for the action
		scale:    DEFAULT_SCALE	// scale of magnification
	},
	optionalParams: {},	// no optional parameters

	constructor: function(targets, kwArgs) {
		// process optional named parameters
		this.scale = kwArgs && typeof kwArgs.scale == "number" ? kwArgs.scale : DEFAULT_SCALE;

		this.connect();
	},

	process: function(feature, evt, evtType){
		var fid = feature.id,
			startScale,
			endScale,
			anim,
			step; // result of _getStep;

		if (fid in this.anim){
			anim = this.anim[fid];
		} else{
			this.anim[fid] = {};
		}
		
		if (anim) {
			if (anim.action.status()=="stopped") step = 1;
			else step = anim.action._getStep();
			anim.action.stop(true);
		}
		else anim = this.anim[fid];

		if (evtType == "onmouseover"){
			startScale = step!=undefined ? this.scale*(1-step)+step : 1;
			endScale = this.scale;
		} else {
			startScale = step!=undefined ? step*this.scale+1-step : this.scale;
			endScale = 1;
		}
		
		var factory =  feature.map.engine.getFactory(feature.type),
			featureBbox = feature.getBbox(),
			centerX = factory.getX( (featureBbox[0]+featureBbox[2])/2 ),
			centerY = factory.getY( (featureBbox[1]+featureBbox[3])/2 );

		var kwArgs = {
			shape:    feature.baseShapes[0],
			duration: this.duration,
			easing:   this.easing,
			transform: [
				{
					name: "scaleAt",
					start: [startScale, centerX, centerY],
					end: [endScale, centerX, centerY]
				}
			]
		};

		anim.action = gf.animateTransform(kwArgs);

		if (evtType == "onmouseout"){
			anim.con = dojo.connect(anim.action, "onEnd", this, function(){
				if (this.anim[fid]){
					dojo.disconnect(this.anim[fid].con);
					delete this.anim[fid];
				}
			});
		}
		else feature.baseShapes[0].moveToFront();

		anim.action.play();
	}
});

})();
