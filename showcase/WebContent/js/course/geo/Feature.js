dojo.provide("course.geo.Feature");

dojo.require("course.geo.utils");

course.geo.events = {onmouseover: true, onmouseout: true, onclick: true};

dojo.declare("course.geo.Feature", null, {
	
	id: null,
	bbox: null,
	styleMap: null,
	parent: null,
	map: null,

	constructor: function(featureDef, kwArgs) {
		this.styleMap = {};
		if (kwArgs) dojo.mixin(this, kwArgs);
		if (featureDef) dojo.mixin(this, featureDef);
		if (!this.id) this.id = "_geo_"+course.geo.utils.getUniqueNumber();
		if (this.styleClass && !dojo.isArray(this.styleClass)) this.styleClass = [this.styleClass];
		if (featureDef) {
			if (featureDef.styleMap) this.setStyleMap(featureDef.styleMap);
			else if (featureDef.style) this.addStyle(featureDef.style);
		}
	},
	
	setMap: function(map) {
		this.map = map;
		if (this.styleClass) dojo.forEach(this.styleClass, function(_class){
			var featuresByClass = map._featuresByClass;
			if (!featuresByClass[_class]) featuresByClass[_class] = [];
			featuresByClass[_class].push(this);
		}, this);
	},
	
	setParent: function(parent) {
		this.parent = parent;
	},
	
	get: function(attr) {
		return this.properties[attr];
	},
	
	addStyle: function(/* Array|Object */style, styleKey) {
		if (!styleKey) styleKey = "normal";
		if (!dojo.isArray(style)) style = [style];
		dojo.forEach(style, function(_style){
			var s = new course.geo.Style(_style, this.map);
			if (!s.styleClass && !s.fid) {
				if (!this.styleMap[styleKey]) this.styleMap[styleKey] = [];
				this.styleMap[styleKey].push(s);
			}
		}, this);
	},
	
	render: function() {
		
	},
	
	setGroup: function(group) {
		this.group = group;
	},
	
	getBbox: function() {
		return this.bbox;
	},
	
	getStyle: function(styleKey) {
		styleKey = styleKey||"normal";
		var style = this.styleMap[styleKey];
		if (!style && styleKey=="normal") {
			style = this.map.getStyleById(this.styleId || this.id);
			if (style && !style._features[this.id]) style._features[this.id] = this; //TODO: move this code to the feature constructor
		}
		return style;
	},
	
	getStyleDef: function(styleKey) {
		var style = this.getStyle(styleKey);
		return style && style.def;
	},
	
	connect: function(/* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.connectWithHandle(null, events, context, method);
	},
	
	connectWithHandle: function(handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		
	},
	
	disconnect: function(handle) {
		
	}
});


// registry of feature constructors
course.geo.featureTypes = {};
