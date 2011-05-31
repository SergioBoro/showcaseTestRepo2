dojo.provide("course.geo.gfx.Navigation");

dojo.require("course.geo.gfx.Moveable");

dojo.declare("course.geo.gfx.Navigation",null, {
	
	resizePoints: true,
	resizeLines: true,
	resizePolygons: true,

	moveable: null,
	wheelConnection: null,

	enable: function() {
		this.moveable = new course.geo.gfx.Moveable(this.map.engine.surface);
		if (dojox.gfx.renderer!="silverlight") this.enableZoom(true);
	},

	enableZoom: function(enable) {
		if (enable && !this._mouseWheelListener) {
			var wheelEventName = !dojo.isMozilla ? "onmousewheel" : "DOMMouseScroll";
			this.wheelConnection = this.map.engine.surface.connect(wheelEventName, this, this._onWheel);
		}
	},

	_onWheel: function(mouseEvent) {
		// prevent browser interaction
		dojo.stopEvent(mouseEvent);
		
		// position relative to map container
		var x = mouseEvent.pageX - this.map.x,
			y = mouseEvent.pageY - this.map.y;
		
		// zoom increment power 
		var power  = mouseEvent[ dojo.isMozilla ? "detail" : "wheelDelta" ] / (dojo.isMozilla ? -3 : 120) ;
		var scaleFactor = Math.pow(1.2, power);

		var engine = this.map.engine;
		engine.group.applyLeftTransform({xx:scaleFactor,yy:scaleFactor, dx: x*(1-scaleFactor), dy: y*(1-scaleFactor)});
		engine.factories.Placemark.calculateLengthDenominator();
		
		this._resizeFeatures(this.map.featureContainer, 1/scaleFactor);
	},
	
	_resizeFeatures: function(featureContainer, scaleFactor) {
		dojo.forEach(featureContainer.features, function(feature){
			if (feature.type == "Placemark" || feature.type == "Feature") this._resizePlacemark(feature, scaleFactor);
			else if (feature.type == "FeatureContainer" || feature.type == "FeatureCollection") this._resizeFeatures(feature, scaleFactor);
		}, this);
	},
	
	_resizePlacemark: function(feature, scaleFactor) {
		if (feature.invalid) return;
		var geometry = feature.getGeometry();

		if (this.resizePoints && geometry.type == "Point") {
			dojo.forEach(feature.baseShapes, function(shape){
				shape.applyRightTransform(dojox.gfx.matrix.scale(scaleFactor));
			});
		}
		else if ( dojox.gfx.renderer!="vml" && (
				(this.resizeLines && (geometry.type == "LineString" || geometry.type == "MultiLineString")) ||
				(this.resizePolygons && (geometry.type == "Polygon" || geometry.type == "MultiPolygon")) )) {
			dojo.forEach(feature.baseShapes, function(shape){
				var stroke = shape.getStroke();
				if (stroke) {
					stroke.width *= scaleFactor;
					shape.setStroke(stroke);
				}
			});
		}
	}
});