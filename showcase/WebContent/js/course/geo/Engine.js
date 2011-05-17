dojo.provide("course.geo.Engine");

dojo.declare("course.geo.Engine", null, {
	
	type: "gfx",
	
	factories: null,
	
	initialized: false,

	constructor: function(kwArgs){
		dojo.mixin(this, kwArgs);
		this.factories = {};
	},
	
	initialize: function(/* Function */readyFunction) {
	},
	
	isInitialized: function() {
		return this.initialized;
	},
	
	createContainer: function(parentContainer, featureType) {

	},
	
	appendChild: function(parent, child) {
		
	},

	prerender: function() {

	},
	
	getFactory: function(type) {
		return this.factories[type];
	},
	
	getTopContainer: function() {
		
	},
	
	connect: function(group, event, context, method) {
	},
	
	patchMethods: function() {
		
	}
});