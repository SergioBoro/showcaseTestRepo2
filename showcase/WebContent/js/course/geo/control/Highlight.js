dojo.provide("course.geo.control.Highlight");

dojo.require("course.geo.control.Base");
dojo.require("dojox.color");

/*=====
dojo.declare("dojox.charting.action2d.__HighlightCtorArgs", dojox.charting.action2d.__BaseCtorArgs, {
	//	summary:
	//		Additional arguments for highlighting actions.

	//	highlight: String|dojo.Color|Function?
	//		Either a color or a function that creates a color when highlighting happens.
	highlight: null
});
=====*/
(function(){
var DEFAULT_SATURATION  = 100,	// %
	DEFAULT_LUMINOSITY1 = 75,	// %
	DEFAULT_LUMINOSITY2 = 50,	// %

	c = dojox.color,

	cc = function(color){
		return function(){ return color; };
	},

	hl = function(color){
		var a = new c.Color(color),
			x = a.toHsl();
		if(x.s == 0){
			x.l = x.l < 50 ? 100 : 0;
		}else{
			x.s = DEFAULT_SATURATION;
			if(x.l < DEFAULT_LUMINOSITY2){
				x.l = DEFAULT_LUMINOSITY1;
			}else if(x.l > DEFAULT_LUMINOSITY1){
				x.l = DEFAULT_LUMINOSITY2;
			}else{
				x.l = x.l - DEFAULT_LUMINOSITY2 > DEFAULT_LUMINOSITY1 - x.l ?
					DEFAULT_LUMINOSITY2 : DEFAULT_LUMINOSITY1;
			}
		}
		return c.fromHsl(x);
	};

dojo.declare("course.geo.control.Highlight", course.geo.control.Base, {
	//	summary:
	//		Creates a highlighting action on a plot, where an element on that plot
	//		has a highlight on it.

	// the data description block for the widget parser
	defaultParams: {
		duration: 400,	// duration of the action in ms
		easing:   dojo.fx.easing.backOut	// easing for the action
	},
	optionalParams: {
		highlight: "orange"	// name for the highlight color
							// programmatic instantiation can use functions and color objects
	},

	constructor: function(targets, kwArgs){
		var highlight = kwArgs && kwArgs.highlight;
		this.colorFun = highlight ? (dojo.isFunction(highlight) ? highlight : cc(highlight)) : hl;

		this.connect();
	},

	process: function(feature, evt, evtType){
		var fid = feature.id,
			start,
			end,
			anim,
			step; // result of _getStep;

		if (fid in this.anim){
			anim = this.anim[fid];
		}else{
			this.anim[fid] = {};
		}
		
		var color = feature.baseShapes[0].getFill();
		if(anim) {
			anim.action.stop(true);
		}
		else {
			anim = this.anim[fid];
			anim.start = color;
			anim.end = this.colorFun(color);
		}
		
		start = color;
		if (evtType == "onmouseover"){
			end = anim.end;
		}
		else {
			end = anim.start;
		}

		var kwArgs = {
			shape:    feature.baseShapes[0],
			duration: this.duration,
			easing:   this.easing,
			color:    {start: start, end: end}
		};
		anim.action = dojox.gfx.fx.animateFill(kwArgs);
		
		if(evtType == "onmouseout"){
			anim.con = dojo.connect(anim.action, "onEnd", this, function(){
				if (this.anim[fid]){
					dojo.disconnect(this.anim[fid].con);
					delete this.anim[fid];
				}
			});
		}
		anim.action.play();
	}
});
})();
