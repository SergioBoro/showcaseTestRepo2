dojo.provide("course.geo.Placemark");

dojo.require("course.geo.Feature");
dojo.require("course.geo.styling");

(function() {

var g = course.geo,
	s = g.styling;
	
var geometryTypes = {Point: 1, LineString: 1, Polygon: 1, MultiLineString: 1, MultiPolygon: 1};

dojo.declare("course.geo.Placemark", course.geo.Feature, {
	
	type: "Placemark",
	
	// keep active handles
	handles: null,
	
	baseShapes: null,

	constructor: function(/* Object? */featureDef, /* Object? */kwArgs) {
		this.handles = {};
		this.baseShapes = [];
	},
	
	getGeometry: function() {
		return this._getGeometry();
	},

	_getGeometry: function() {
		var geometry = this.geometry;
		if (!geometry) {
			var geometryId = this.geometryId || this.id;
			if (geometryId) geometry = this.map.getGeometryById(geometryId);
		}
		return geometry;
	},
	
	render: function(stylingOnly) {
		this.map.methods.Placemark.render.call(this, stylingOnly);
	},

	_render: function(stylingOnly) {
		//TODO: disconnect connections and then reconnect them
		var geometry = this.getGeometry();
		if (!geometry || !geometry.coordinates || !(geometry.type in geometryTypes)) {
			this.invalid = true;
			return;
		}
		
		// check if have factory for the Placemark
		var factory = this.map.engine.getFactory(this.type);
		if (!factory) return;

		// TODO: disconnect
		if (!stylingOnly) {
			// destroy base shapes
			for (var i=this.baseShapes.length-1; i>=0; i--) {
				this.map.engine.destroy(this.baseShapes.pop(), this);
			}
		}
		// destroy extra shapes
		// extra shapes are destroyed in an case (also if stylingOnly == true)
		if (this.extraShapes) for (var i=this.extraShapes.length-1; i>=0; i--) {
			this.map.engine.destroy(this.extraShapes.pop(), this);
		}
		
		var style = s.calculateStyle(this);
		/*
		var pointStyle = s.calculatePointStyle(this),
			textStyle = s.calculateTextStyle(this),
			lineStyle,
			polygonStyle;
		
		var gType = geometry.type,
			isPoint = (gType == "Point" || gType == "MultiPoint");
			isLine = (gType == "LineString" || gType == "MultiLineString");
			isPolygon = (gType == "Polygon" || gType == "MultiPolygon");

		if (isLine || isPolygon) {
			lineStyle = s.calculateLineStyle();
			polygonStyle = s.calculatePolygonStyle();
		}
		*/

		// apply style to the base geometry
		var styleType = "point";
		if (geometry.type == "Polygon" || geometry.type == "MultiPolygon") styleType = "polygon";
		else if (geometry.type == "LineString" || geometry.type == "MultiLineString") styleType = "line";
		if (stylingOnly) {
			if (style[styleType]) { // we have a specific style
				dojo.forEach(style[styleType], function(_style, i) {
					var shape = this.baseShapes[i];
					s.applyStyle(styleType, shape, this, style, _style, factory);
				}, this);
			}
			else { // no specific style
				var shape = this.baseShapes[0];
				s.applyStyle(styleType, shape, this, style, null, factory);
			}
			// TODO: always rerender for points
			/*
			dojo.forEach(style, function(_style, i) {
				var shape, appendChild = false;
				if (i<numBaseShapes) {
					shape = this.baseShapes[i];
					// TODO: consider z-index change
				}
				else {
					appendChild = true;
					shape = this._createShape(geometry, factory);
					this.baseShapes.push(shape);
				}
				s.applyStyle(shape, this, _style, factory);
				if (appendChild) this.map.engine.appendChild(shape, this, _style.zIndex);
			}, this);
			*/
		}
		else {
			if (style[styleType]) { // we have a specific style
				dojo.forEach(style[styleType], function(_style) {
					var shape = this._createShape(geometry, factory);
					s.applyStyle(styleType, shape, this, style, _style, factory);
					this.map.engine.appendChild(shape, this, _style.zIndex || style.zIndex);
					this.baseShapes.push(shape);
				}, this);
			}
			else { // no specific style
				var shape = this._createShape(geometry, factory);
				s.applyStyle(styleType, shape, this, style, null, factory);
				this.map.engine.appendChild(shape, this, style.zIndex);
				this.baseShapes.push(shape);
			}
		}
		
		// render extra geometry if it is defined in a style
		// point geometry can only have base geometry
		/*
		if (!isPoint) {
			if (lineStyle) {
				dojo.forEach(lineStyle, function(_style) {
					var extraGeometry = s.getStyleGeometry(_style, geometry);
					if (extraGeometry) {
						var shape = this._createShape(extraGeometry, factory);
						s.applyLineStyle(shape, this, _style, factory);
						this.map.engine.appendChild(shape, this, _style.zIndex);
						this.extraShapes.push(shape);
					}
				}, this);
			}
			if (polygonStyle) {
				dojo.forEach(polygonStyle, function(_style) {
					var extraGeometry = s.getStyleGeometry(_style, geometry);
					if (extraGeometry) {
						var shape = this._createShape(extraGeometry, factory);
						s.applyPolygonStyle(shape, this, _style, factory);
						this.map.engine.appendChild(shape, this, _style.zIndex);
						this.extraShapes.push(shape);
					}
				}, this);
			}
		}
		
		// apply text styles
		if (textStyle) {
			// take into account all geometries produced above
		}
		*/
	},
	
	_createShape: function(geometry, factory) {
		var shape;
		var coordinates = geometry.coordinates;
		switch (geometry.type) {
			case "Point":
				shape = factory.makePoint(this, coordinates);
				break;
			case "LineString":
				shape = factory.makeLineString(this, coordinates);
				break;			
			case "Polygon":
				shape = factory.makePolygon(this, coordinates);
				break;
			case "MultiPolygon":
				shape = factory.makeMultiPolygon(this, coordinates);
				break;
			case "MultiLineString":
				shape = factory.makeMultiLineString(this, coordinates);
				break;
			case "MultiPoint":
				shape = factory.makeMultiPoint(this, coordinates);
				break;
		}
		return shape;
	},
	
	getBbox: function() {
		// summary:
		//		Returns the feature boundings box in the current map projection
		var geometry = this.getGeometry();
		return (geometry && geometry.bbox) ? geometry.bbox : geometry.coordinates; // TODO: calculate bounding box if it's not provided
	},
	
	connectWithHandle: function(handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		if (this.invalid) return handle;
		events = dojo.isString(events) ? [events] : events;
		var handleObj = handle && this.handles[handle] ? this.handles[handle] : {},
			numEvents = 0; // number of connected events
		dojo.forEach(events, function(event) {
			if (course.geo.events[event]) {
				if (handleObj[event]) dojo.disconnect(handleObj[event]);
				method = dojo.hitch(context, method);
				handleObj[event] = this.map.engine.connect(this.baseShapes[0], event, this, function(evt) {
					method(this, evt, event);
				});
				numEvents++;
			}
		}, this);
		if (!numEvents) return handle;
		handle = handle || course.geo.utils.getUniqueNumber();
		if (!this.handles[handle]) this.handles[handle] = handleObj;
		return handle;
	},
	
	disconnect: function(handle) {
		var handleObj = this.handles[handle];
		if (handleObj) {
			for (var event in handleObj) dojo.disconnect(handleObj[event]);
			delete this.handles[handle];
		}
	}
});


// default methods;
var p = g.Placemark.prototype;
g.methods.Placemark = {
	render: p._render
}

}())

// register the constructor
course.geo.featureTypes["Feature"] = course.geo.Placemark;
course.geo.featureTypes["Placemark"] = course.geo.Placemark;
