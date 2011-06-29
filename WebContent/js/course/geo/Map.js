dojo.provide("course.geo.Map");

(function() {
	
var g = course.geo;
	
var defaultEngine = {type: "gfx", options:{}};

var restoreMethods = function(map) {
	if (!map.methods) map.methods = {};
	for (var type in g.methods) {
		if (!map.methods[type]) map.methods[type] = {};
		dojo.mixin(map.methods[type], g.methods[type]);
	}
	
	/*
	dojo.mixin(map.methods, {
		Map: {
			render: map._render,
			renderFeatures: map._renderFeatures
		}
	});
	*/
};


dojo.declare("course.geo.Map", null, {

	engine: null,
	
	// registry of externally available methods
	methods: null,
	
	extent: null,
	
    //	features: Object
	//		A registry of features that can be referenced by id.
	features: null,
    //	geometries: Object
	//		A registry of geometries that can be referenced by id.
	geometries: null,
	//	featureContainer: course.geo.FeatureContainer
	//		Top level course.geo.FeatureContainer
	featureContainer: null,
	
	attributesInFeature: true,
	
	styleById: null,
	styleByFid: null,
	styleByClass: null,
	styleByClassAndFid: null,
	featuresByClass: null,

	constructor: function(/* DOMNode */container, /* Object? */kwArgs){
		// initialize styling registries
		this.styleById = {};
		this.styleByFid = {};
		this.styleByClass = {};
		this.styleByClassAndFid = {};
		this.featuresByClass = {};

		restoreMethods(this);
		
		// get map container coords
		//dojo.style(container, "display", "block");
		this.container = container;

		if(!kwArgs) kwArgs = {};
		dojo.mixin(this, kwArgs);

		// features
		this.features = {};
		// geometries
		this.geometries = {};
		if (kwArgs.geometries) this.loadGeometries(kwArgs.geometries);
		// features
		this.featureContainer = new course.geo.FeatureContainer(null, {
			map: this,
			parent: this
		});
		// alias for this.featureContainer
		this.document = this.featureContainer;
		this.addStyle(course.geo.styling.style, true);

		if (kwArgs.style) this.addStyle(kwArgs.style, true);
		
		// set engine
		this.setEngine(kwArgs.engine || (dojo.config&&dojo.config.mapEngine) || defaultEngine);
		
		if (kwArgs.features) this.addFeatures(kwArgs.features, true);
	},
	
	ready: function(/* Function */readyFunction) {
		if (!this.engine.isInitialized()) this.engine.initialize(dojo.hitch(this, function(){
			this.render();
			if (readyFunction) readyFunction();
		}));
	},
	
	addFeatures: function(features, noRendering) {
		this.document.addFeatures(features, noRendering);
	},
	
	removeFeatures: function(features) {
		this.document.removeFeatures(features);
	},
	
	render: function(stylingOnly, mode) {
		this.methods.Map.render.call(this, stylingOnly, mode);
	},
	
	_render: function(stylingOnly, mode) {
		if (!this.extent) this.extent = this.getBbox();
		this._calculateViewport();
		this.engine.prepare();
		this.document._render(stylingOnly, mode);
	},
	
	renderFeatures: function(features, stylingOnly, mode) {
		this.methods.Map.renderFeatures.call(this, features, stylingOnly, mode);
	},

	_renderFeatures: function(features, stylingOnly, mode) {
		// render the given features only instead of the whole map tree
		if (dojo.isArray(features)) {
			dojo.forEach(features, function(feature){
				feature._render(stylingOnly, mode);
			}, this);
		}
		else {
			for(var fid in features) {
				// TODO: avoid double rendering
				features[fid]._render(stylingOnly, mode);
			}
		}
	},

	resize: function(width, height) {
		this._calculateViewport(width, height);
		this.render();
	},
	
	getContainer: function() {
		return this.engine.getTopContainer();
	},

	resize: function(width, height) {
		this.width = width;
		this.height = height;
		this.render();
	},

	_calculateViewport: function() {
		var contentBox = dojo.contentBox(this.container);
		var coords = dojo.coords(this.container);
		this.width = this.width || contentBox.w || 100;
		this.height = this.height || contentBox.h || 100;
		this.x = coords.x;
		this.y = coords.y;
	},

	loadGeometries: function(/* String|Array|Object */geometries) {
		if (dojo.isString(geometries)) {
			dojo.xhrGet({
				url: geometries,
				handleAs: "json",
				sync: true,
				load: dojo.hitch(this, function(/* Array|Object */_geometries){
					this.loadGeometries(_geometries);
				})
			});
		}
		else if (dojo.isArray(geometries)) {
			dojo.forEach(geometries, function(geometry){
				if (geometry.id) this.geometries[geometry.id] = geometry;
			}, this);
		}
		else { // Object
			this.geometries = geometries;
		}
	},
	
	addStyle: function(/* Array|Object */style, preventRendering) {
		this.document.addStyle(style);
		if (!preventRendering) this.document._render(true);;
	},
	
	setStyle: function(/* Array|Object */style) {
		this.document.setStyle(style);
	},

	getGeometryById: function(id) {
		return this.geometries[id];
	},

	getStyleById: function(id) {
		return this.styleById[id];
	},

	registerFeature: function(feature) {
		if (feature.id) this.features[feature.id] = feature;
	},

	getFeatureById: function(id) {
		return this.features[id];
	},

	connect: function(/* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.featureContainer.connect(events, context, method);
	},

	connectWithHandle: function(/* Integer */handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.featureContainer.connectWithHandle(handle, events, context, method);
	},
	
	disconnect: function(handle) {
		this.featureContainer.disconnect(handle);
	},
	
	setEngine: function(engine) {
		// cheating build util
		var req = dojo.require;
		if (dojo.isString(engine)) engine = {type: engine};
		if (!engine.declaredClass) {
			var options = {map: this};
			if (engine.options) dojo.mixin(options, engine.options);
			req("course.geo."+engine.type+".Engine");
			engine = new course.geo[engine.type].Engine(options);
		}
		// check dependencies in the _dependencies local variable
		for (dependency in _dependencies) {
			req("course.geo."+engine.type+"."+dependency, true);
		}
		this.engine = engine;
	},
	
	getBbox: function() {
		return this.document.getBbox();
	},
	
	destroy: function() {
		this.engine.destroy();
	},
	
	getMapGeometry: function(geometry) {
		return geometry;
	}
});

// An engine is supposed to implement each module listed in the _dependencies.
// See dynamics.js for an example
var _dependencies = {};
g.setDependency = function(dependency) {
	_dependencies[dependency] = 1;
};

// default methods;
var p = g.Map.prototype;
if (!g.methods) g.methods = {};
g.methods.Map = {
	render: p._render,
	renderFeatures: p._renderFeatures
}

}());

dojo.require("course.geo.styling");
dojo.require("course.geo.FeatureContainer");
dojo.require("course.geo.Placemark");