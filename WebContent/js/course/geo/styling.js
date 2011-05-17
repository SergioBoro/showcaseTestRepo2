dojo.provide("course.geo.styling");


(function() {
	
var g = course.geo,
	s = g.styling;
	
var symbolizers = ["point", "polygon", "line", "text"],
	noStyleMixin = {id:1, filter:1, styleClass:1, fid:1, styleFunction:1};
	
var styleById = {};

var byFid = {},
	byClass = {},
	byClassAndFid = {};

dojo.declare("course.geo.Style", null, {
	
	// json style definition
	def: null,
	
	// features that have this style in their style attribute (i.e. inline style)
	_features: null,

	constructor: function(def, map) {
		this.map = map;
		this._features = {};
		this._setDef(def);
		if (def.id) styleById[def.id] = this;
	},
	
	set: function(def) {
		var affectedFeatures = this.styleClass || this.fid ? {} : this._features;
		// remove the style from byClassAndFid, byFid, byClass
		if (this.styleClass) {
			dojo.forEach(this.styleClass, function(_class){
				// remove the style from byClassAndFid
				if (this.fid) {
					dojo.forEach(this.fid, function(_fid){
						var entry = byClassAndFid[_class][_fid],
							entryLength = entry.length;
						for(var i=0; i<entryLength;i++) {
							if (entry[i]==this) break;
						}
						if (i<entryLength) {
							entry.splice(i,1);
							var feature = this.map.getFeatureById(_fid);
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
					// remove the style from byClass
					var entry = byClass[_class],
						entryLength = entry.length;
					for(var i=0; i<entryLength;i++) {
						if (entry[i]==this) break;
					}
					if (i<entryLength) {
						entry.splice(i,1);
						var features = this.map._featuresByClass[_class];
						if (features) dojo.forEach(features, function(f){
							if (!affectedFeatures[f.id]) affectedFeatures[f.id] = f;
						});
					}
				}
			}, this);
		}
		else if (this.fid) {
			// remove the style from byFid
			dojo.forEach(this.fid, function(_fid){
				var entry = byFid[_fid],
					entryLength = entry.length;
				for(var i=0; i<entryLength;i++) {
					if (entry[i]==this) break;
				}
				if (i<entryLength) {
					entry.splice(i,1);
					var feature = this.map.getFeatureById(_fid);
					if (feature && !affectedFeatures[feature.id]) affectedFeatures[feature.id] = feature;
				}
			}, this);
		}
		
		delete this.styleClass, this.fid; this.filter;

		this._setDef(def);
		// apply the style to the the affected features
		this.map.render(true, affectedFeatures);
	},
	
	_setDef: function(def) {
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
					dojo.forEach(fid, function(_fid){
						if (!byClassAndFid[_class]) byClassAndFid[_class] = {};
						if (!byClassAndFid[_class][_fid]) byClassAndFid[_class][_fid] = [];
						byClassAndFid[_class][_fid].push(this);
					}, this);
				}
				// styleClass only
				else {
					if (!byClass[_class]) byClass[_class] = [];
					byClass[_class].push(this);
				}
			}, this);
		}
		else if (fid) {
			// fid only
			dojo.forEach(fid, function(_fid){
				if (!byFid[_fid]) byFid[_fid] = [];
				byFid[_fid].push(this);
			}, this);
		}
		if (styleClass) this.styleClass = styleClass;
		if (fid) this.fid = fid;
		var styleFunction = def.styleFunction;
		if (styleFunction) {
			this.styleFunction = {
				func: dojo.isString(styleFunction.func) ? dojo.getObject(styleFunction.func) : styleFunction.func,
				options: styleFunction.options
			};
		}
		def = styleMixin({}, def);
		this.def = def;
	},
	
	destroy: function() {
		delete this.def, this.filter, this.map, this._features, this.styleFunction;
	}
});

s.reset = function() {
	dojo.forEach([byFid, byClass, byClassAndFid], function(obj){
		for(var key in obj) {
			dojo.forEach(obj[key], function(style){
				style.destroy();
			})
		}
	});
	byFid = {};
	byClass = {};
	byClassAndFid = {};
}

s.getStyleById = function(id) {
	return styleById[id];
};

s.calculateStyle = function(feature, styleKey) {
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
		appendFeatureStyle(features[i], styles, styleKey);
	}
	
	// now do actual style calculation
	var resultStyle = {};
	dojo.forEach(styles, function(style) {
		var applyStyle = style.filter ? evaluateFilter(style.filter, feature) : true;
		if (applyStyle) {
			dojo.mixin(resultStyle, style.def);
			if (style.styleFunction) style.styleFunction.func(feature, resultStyle, style.styleFunction.options);
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


s.applyStyle = function(styleType, shape, feature, calculatedStyle, specificStyle, factory) {
	switch(styleType) {
		case "point":
			shape = factory.applyPointStyle(shape, feature, calculatedStyle, specificStyle, factory);
			break;
		case "line":
			shape = factory.applyLineStyle(shape, feature, calculatedStyle, specificStyle, factory);
			break;
		case "polygon":
			shape = factory.applyPolygonStyle(shape, feature, calculatedStyle, specificStyle, factory);
			break;
		case "text":
			shape = factory.applyTextStyle(shape, feature, calculatedStyle, specificStyle, factory);
			break;
	}
	return shape;
}

function evaluateFilter(filter, feature) {
	return filter.call(feature.properties);
}

var appendFeatureStyle = function(feature, styles, styleKey) {
	// TODO: duplication of styleClass
	var styleClass = feature.styleClass,
		fid = feature.id;
	if (styleClass) {
		dojo.forEach(styleClass, function(_styleClass){
			if (byClassAndFid[_styleClass] && byClassAndFid[_styleClass][fid]) {
				append(byClassAndFid[_styleClass][fid], styles);
			}
			else if (byClass[_styleClass]) append(byClass[_styleClass], styles);
		});
	}
	else if (byFid[fid]) append(byFid[fid], styles);
	
	if (!styleKey) styleKey = "normal";
	if (feature.styleMap[styleKey]) append(feature.styleMap[styleKey], styles);
}

var append = function(/*Array*/what, /*Array*/to) {
	dojo.forEach(what, function(element){
		to.push(element);
	});
}

var styleMixin = function(styleDef, styleAttrs) {
	// rules will be copied by reference
	for (attr in styleAttrs) {
		if (!noStyleMixin[attr]) styleDef[attr] = styleAttrs[attr];
	}
	return styleDef;
}

s.styleMap = {
	"normal": {
		stroke: "black",
		strokeWidth: 0.5,
		strokeJoin: "round",
		fill: "#B7B7B7",
		point: {
			/*
			type: "image",
			width: 10,
			height: 10,
			x: 0,
			y: 0,
			src: "path.png"
			*/
			type: "shape",
			shapeType: "square" // circle, square, triangle, star, cross, or x
		},
		size: 10
	},
	"highlight": {
		fill: "#D5D5D5"
	}
}

}())