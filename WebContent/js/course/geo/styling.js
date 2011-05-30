dojo.provide("course.geo.styling");

(function() {
	
var g = course.geo,
	s = g.styling;
	
var symbolizers = ["point", "polygon", "line", "text"],
	noStyleMixin = {id:1, filter:1, styleClass:1, fid:1, styleFunction:1, mode:1};

dojo.declare("course.geo.Style", null, {
	
	// json style definition
	def: null,
	
	// features that have this style in their style attribute (i.e. inline style)
	_features: null,

	constructor: function(def, map) {
		this.map = map;
		this._features = {};
		this._setDef(def);
		if (def.id) map.styleById[def.id] = this;
	},
	
	set: function(def) {
		var m = this.map;
		var affectedFeatures = this.styleClass || this.fid ? {} : this._features;
		// remove the style from styleByClassAndFid, styleByFid, styleByClass
		if (this.styleClass) {
			dojo.forEach(this.styleClass, function(_class){
				// remove the style from styleByClassAndFid
				if (this.fid) {
					dojo.forEach(this.fid, function(_fid){
						var entry = m.styleByClassAndFid[_class][_fid],
							entryLength = entry.length;
						for(var i=0; i<entryLength;i++) {
							if (entry[i]==this) break;
						}
						if (i<entryLength) {
							entry.splice(i,1);
							var feature = m.getFeatureById(_fid);
							if (feature && feature.styleClass) {
								for (var i=0; i<feature.styleClass.length; i++) {
									if (_class==feature.styleClass[i] && !affectedFeatures[feature.id]) {
										affectedFeatures[feature.id] = feature;
										break;
									}
								}
							}
						}
					}, this);
				}
				else {
					// remove the style from styleByClass
					var entry = m.styleByClass[_class],
						entryLength = entry.length;
					for(var i=0; i<entryLength;i++) {
						if (entry[i]==this) break;
					}
					if (i<entryLength) {
						entry.splice(i,1);
						var features = m.featuresByClass[_class];
						if (features) dojo.forEach(features, function(f){
							if (!affectedFeatures[f.id]) affectedFeatures[f.id] = f;
						});
					}
				}
			}, this);
		}
		else if (this.fid) {
			// remove the style from styleByFid
			dojo.forEach(this.fid, function(_fid){
				var entry = m.styleByFid[_fid],
					entryLength = entry.length;
				for(var i=0; i<entryLength;i++) {
					if (entry[i]==this) break;
				}
				if (i<entryLength) {
					entry.splice(i,1);
					var feature = m.getFeatureById(_fid);
					if (feature && !affectedFeatures[feature.id]) affectedFeatures[feature.id] = feature;
				}
			}, this);
		}
		
		delete this.styleClass, this.fid; this.filter;

		this._setDef(def);
		// apply the style to the the affected features
		this.map.render(true, "normal", affectedFeatures);
	},
	
	_setDef: function(def) {
		var m = this.map;
		if (def.mode) this.mode = def.mode;
		var filter = def.filter;
		if (def.filter) {
			this.filter = dojo.isString(filter) ? eval("_=function(){return "+filter+";}" ) : filter;
		}
		// treat styleClass and fid
		var styleClass = def.styleClass;
		var fid = def.fid;
		if (fid && !dojo.isArray(fid)) fid = [fid];
		if (styleClass) {
			if (!dojo.isArray(styleClass)) styleClass = [styleClass];
			dojo.forEach(styleClass, function(_class){
				// styleClass and fid simultaneously
				if (fid) {
					var byClassAndFid = m.styleByClassAndFid;
					dojo.forEach(fid, function(_fid){
						if (!byClassAndFid[_class]) byClassAndFid[_class] = {};
						if (!byClassAndFid[_class][_fid]) byClassAndFid[_class][_fid] = [];
						byClassAndFid[_class][_fid].push(this);
					}, this);
				}
				// styleClass only
				else {
					if (!m.styleByClass[_class]) m.styleByClass[_class] = [];
					m.styleByClass[_class].push(this);
				}
			}, this);
		}
		else if (fid) {
			// fid only
			dojo.forEach(fid, function(_fid){
				if (!m.styleByFid[_fid]) m.styleByFid[_fid] = [];
				m.styleByFid[_fid].push(this);
			}, this);
		}
		if (styleClass) this.styleClass = styleClass;
		if (fid) this.fid = fid;
		var styleFunction = def.styleFunction;
		if (styleFunction) {
			this.styleFunction = {
				// features may need this attribute to be aware that the style has changed
				updated: (new Date()).getTime()
			};
			var func = styleFunction.func;
			func = dojo.isString(func) ? dojo.getObject(func) : func;
			for(var attr in styleFunction) {
				this.styleFunction[attr] = (attr == "func") ? func : styleFunction[attr];
			}
		}
		def = styleMixin({}, def);
		this.def = def;
	},
	
	destroy: function() {
		delete this.def, this.filter, this.map, this._features, this.styleFunction;
	}
});

/*
s.reset = function() {
	dojo.forEach([styleByFid, styleByClass, styleByClassAndFid], function(obj){
		for(var key in obj) {
			dojo.forEach(obj[key], function(style){
				style.destroy();
			})
		}
	});
	styleByFid = {};
	styleByClass = {};
	styleByClassAndFid = {};
}
*/

s.calculateStyle = function(feature, mode) {
	var styles = [];
	// find all features participating in the style calculation
	// features = feature itself + all its parents
	var features = [],
		parent = feature;
	do {
		features.push(parent);
		parent = parent.parent;
	}
	while (parent != feature.map)
	
	for (var i=features.length-1; i>=0; i--) {
		appendFeatureStyle(features[i], styles, mode);
	}
	
	// now do actual style calculation
	var resultStyle = {};
	dojo.forEach(styles, function(style) {
		var applyStyle = style.filter ? evaluateFilter(style.filter, feature) : true;
		if (applyStyle) {
			dojo.mixin(resultStyle, style.def);
			if (style.styleFunction) style.styleFunction.func(feature, resultStyle, style.styleFunction);
		}
	});
	
	// final adjustments
	dojo.forEach(symbolizers, function(styleType){
		// ensure that specific style is defined as array
		var s = resultStyle[styleType];
		if (s && !dojo.isArray(s)) resultStyle[styleType] = [s];
	})
	return resultStyle;
}

function evaluateFilter(filter, feature) {
	return filter.call(feature.map.attributesInFeature ? feature : feature.properties);
}

var appendFeatureStyle = function(feature, styles, mode) {
	// TODO: duplication of styleClass
	var m = feature.map;
		styleClass = feature.styleClass,
		fid = feature.id;
	if (styleClass) {
		dojo.forEach(styleClass, function(_styleClass){
			var byClassAndFid = m.styleByClassAndFid;
			if (byClassAndFid[_styleClass] && byClassAndFid[_styleClass][fid]) {
				append(byClassAndFid[_styleClass][fid], styles, mode);
			}
			else if (m.styleByClass[_styleClass]) append(m.styleByClass[_styleClass], styles, mode);
		});
	}
	else if (m.styleByFid[fid]) append(m.styleByFid[fid], styles, mode);

	if (feature.style) append(feature.style, styles, mode);
}

var append = function(/*Array*/what, /*Array*/to, mode) {
	dojo.forEach(what, function(element){
		if ( ( (!element.mode||element.mode=="normal") && (!mode||mode=="normal") ) || (element.mode==mode) ) {
			if (element.def.reset) {
				// clear all entries in the destination
				for(var i=to.length-1; i>=0; i--) to.pop();
			}
			to.push(element);
		}
	});
}

var styleMixin = function(styleDef, styleAttrs) {
	// rules will be copied by reference
	for (attr in styleAttrs) {
		if (!noStyleMixin[attr]) styleDef[attr] = styleAttrs[attr];
	}
	return styleDef;
}

s.style = [
	{
		stroke: "black",
		strokeWidth: 0.5,
		strokeJoin: "round",
		fill: "#B7B7B7",
		shape: "square", // circle, square, triangle, star, cross, or x
		/*
		point: {
			type: "image",
			width: 10,
			height: 10,
			x: 0,
			y: 0,
			src: "path.png"
		},*/
		size: 10
	},
	{
		mode: "highlight",
		fill: "blue",
		stroke: "orange",
		strokeWidth: 10,
		point: {
			type: "shape",
			shape: "square",
			fill: "yellow",
			size: 40
		},
		line: [
			{strokeWidth: 30, stroke: "red"},
			{strokeWidth: 20, stroke: "green"},
			{strokeWidth: 10, stroke: "white"}
		]
	}
]

}())