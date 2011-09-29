dojo.provide("djeo.gfx.Placemark");

dojo.require("djeo.common.Placemark");
dojo.require("djeo.gfx");

(function() {

var g = djeo,
	dx = g.gfx,
	cp = g.common.Placemark,
	s = g.styling;

dojo.declare("djeo.gfx.Placemark", djeo.common.Placemark, {
	
	multipleSymbolizers: true,
	
	constructor: function(kwArgs) {
		dojo.mixin(this, kwArgs);
	},
	
	init: function() {
		this.group = this.engine.group;
		
		this.polygons = this.group.createGroup();
		this.lines = this.group.createGroup();
		this.points = this.group.createGroup();
		this.text = this.group.createGroup();
	},
	
	prepare: function() {
		this.calculateLengthDenominator();
	},
	
	calculateLengthDenominator: function() {
		this.lengthDenominator = (this.group.getTransform()||{xx:1}).xx;
	},
	
	moveTo: function(path, point) {
		path.moveTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	lineTo: function(path, point) {
		path.lineTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	getX: function(x) {
		var result = x-this.map.extent[0];
		if (this.engine.correctScale) result *= this.engine.correctionScale;
		return parseInt(result);
	},
	
	getY: function(y) {
		var result = this.map.extent[3]-y;
		if (this.engine.correctScale) result *= this.engine.correctionScale;
		return parseInt(result);
	},
	
	makePoint: function(feature, coords) {
		// do nothing
		// point shape are created in this.applyPointStyle
		return null;
	},
	
	makeLineString: function(feature, coords) {
		return this.lines.createPath({path: this.makePathString(coords, 1)});
	},

	makePolygon: function(feature, coords) {
		return this.polygons.createPath({path: this.makePathString(coords, 2)});
	},
	
	makeMultiLineString: function(feature, coords) {
		return this.lines.createPath({path: this.makePathString(coords, 2)});
	},
	
	makeMultiPolygon: function(feature, coords) {
		return this.polygons.createPath({path: this.makePathString(coords, 3)});
	},
	
	applyPointStyle: function(feature, coords, calculatedStyle) {
		var specificStyles = calculatedStyle["point"],
			baseShapes = feature.baseShapes,
			numBaseShapes = baseShapes.length;

		// check we have a specific case of when relative only relative scaling only is applied
		if (numBaseShapes && calculatedStyle.rScale !== undefined && !specificStyles) {
			var shapeType = cp.get("shape", calculatedStyle),
				src = cp.getImgSrc(calculatedStyle);
			if (!shapeType && !src) {
				dojo.forEach(baseShapes, function(shape){
					shape.applyRightTransform(dojox.gfx.matrix.scale(calculatedStyle.rScale));
				});
				return;
			}
		}
		
		this._updateShapes(feature, coords, calculatedStyle, specificStyles, true);

		if (specificStyles) {
			var recreateShapes = false;
			dojo.forEach(specificStyles, function(specificStyle, i){
				var currentShape = baseShapes[i];
				if (currentShape && recreateShapes) {
					// disconnect events and remove the shape
					this._removeShape(currentShape, feature);
					currentShape = null;
				}
				// index of specificStyles corresponds to the index of feature.baseShapes
				var shape = this._applyPointStyle(coords, calculatedStyle, specificStyle, feature, currentShape);
				if (currentShape && currentShape != shape) {
					// shape has been replaced!
					// we need to recreate all subsequent shapes
					recreateShapes = true;
					baseShapes[i] = shape;
				}
				if (i >= numBaseShapes) {
					baseShapes.push(shape);
				}
			}, this);
		}
		else {
			var currentShape = baseShapes[0],
				shape = this._applyPointStyle(coords, calculatedStyle, null, feature, currentShape);
			if (shape && (numBaseShapes == 0 || /* shape has been replaced*/currentShape != shape) ) feature.baseShapes[0] = shape;
		}
	},
	
	_applyPointStyle: function(coords, calculatedStyle, specificStyle, feature, shape) {
		var shapeType = cp.get("shape", calculatedStyle, specificStyle),
			src = cp.getImgSrc(calculatedStyle, specificStyle),
			isVectorShape = true,
			scale = cp.getScale(calculatedStyle, specificStyle),
			transform = [dojox.gfx.matrix.translate(this.getX(coords[0]), this.getY(coords[1]))],
			// if we alreade have a shape, we don't need to connect events: the events are already connected to the shape
			connectEvents = !shape ? true : false;

		if (!shapeType && src) isVectorShape = false;
		else if (!g.shapes[shapeType] && !shape)
			// set default value for the shapeType only if we haven't already styled the feature (!shape)
			shapeType = cp.defaultShapeType;

		var size = isVectorShape ? cp.getSize(calculatedStyle, specificStyle) : cp.getImgSize(calculatedStyle, specificStyle);
		if (size) {
			// store the size and the scale for possible future reference
			feature.state.size = [size[0], size[1]];
			feature.state.scale = scale;
		}
		else if (shape) {
			// check if we can apply relative scale (rScale)
			var rScale = cp.get("rScale", calculatedStyle, specificStyle);
			if (rScale !== undefined) {
				size = feature.state.size;
				scale = rScale * feature.state.scale;
			}
		}

		if (isVectorShape) {
			var shapeDef = g.shapes[shapeType],
				shapeSize = shapeType=="circle" ? 2 : Math.max(shapeDef.size[0], shapeDef.size[1]),
				_scale = scale/this.lengthDenominator/shapeSize;

			transform.push(dojox.gfx.matrix.scale(_scale*size[0], _scale*size[1]));

			if (shapeType=="circle") {
				if (shape && (shape.shape.type != "circle")) {
					// can't use existing shape
					// disconnect events and remove the shape
					this._removeShape(shape, feature);
					// we need to reconnect events back to newly created shape
					connectEvents = true;
					shape = null;
				}
				var circleDef = {cx:0, cy:0, r:1};
				if (shape) shape.setShape(circleDef);
				else shape = this.points.createCircle(circleDef);
			}
			else {
				if (shape && shape.shape.type != "polyline") {
					// can't use existing shape
					// disconnect events and remove the shape
					this._removeShape(shape, feature);
					// we need to reconnect events back to newly created shape
					connectEvents = true;
					shape = null;
				}
				if (shape) shape.setShape({points: shapeDef.points});
				else shape = this.points.createPolyline(shapeDef.points);
			}
			dx.applyFill(shape, calculatedStyle, specificStyle);
			dx.applyStroke(shape, calculatedStyle, specificStyle, shapeSize/Math.max(size[0], size[1])/scale);
		}
		else {
			if (shape && shape.shape.type != "image") {
				// can't use existing shape
				// disconnect events and remove the shape
				this._removeShape(shape, feature);
				// we need to reconnect events back to newly created shape
				connectEvents = true;
				shape = null;
			}
			var anchor = cp.getAnchor(calculatedStyle, specificStyle, size),
				imageDef = {
					type: "image",
					src: this._getImageUrl(src),
					width: size[0],
					height: size[1],
					x: anchor[0],
					y: anchor[1]
				};
			if (shape) shape.setShape(imageDef);
			else shape = this.points.createImage(imageDef);
			transform.push(dojox.gfx.matrix.scale(1/this.lengthDenominator*scale));
		}
		
		if (shape) {
			shape.setTransform(transform);
			if (connectEvents) this._connectEvents(shape, feature);
		}
		
		return shape;
	},
	
	applyLineStyle: function(feature, coords, calculatedStyle) {
		var specificStyles = calculatedStyle["line"],
			baseShapes = feature.baseShapes;

		this._updateShapes(feature, coords, calculatedStyle, specificStyles);

		if (specificStyles) {
			dojo.forEach(specificStyles, function(specificStyle, i){
				// index of specificStyles corresponds to the index of feature.baseShapes
				this._applyLineStyle(baseShapes[i], calculatedStyle, specificStyle);
			}, this);
		}
		else {
			this._applyLineStyle(baseShapes[0], calculatedStyle, null);
		}
	},
	
	_applyLineStyle: function(shape, calculatedStyle, specificStyle) {
		dx.applyStroke(shape, calculatedStyle, specificStyle, 1/this.lengthDenominator);
	},
	
	applyPolygonStyle: function(feature, coords, calculatedStyle) {
		var specificStyles = calculatedStyle["polygon"],
			baseShapes = feature.baseShapes;
			
		this._updateShapes(feature, coords, calculatedStyle, specificStyles);

		if (specificStyles) {
			dojo.forEach(specificStyles, function(specificStyle, i){
				// index of specificStyles corresponds to the index of feature.baseShapes
				this._applyPolygonStyle(baseShapes[i], calculatedStyle, specificStyle);
			}, this);
		}
		else {
			this._applyPolygonStyle(baseShapes[0], calculatedStyle, null);
		}
	},
	
	_applyPolygonStyle: function(shape, calculatedStyle, specificStyle) {
		dx.applyFill(shape, calculatedStyle, specificStyle);
		dx.applyStroke(shape, calculatedStyle, specificStyle, 1/this.lengthDenominator);
	},
	
	_updateShapes: function(feature, coords, calculatedStyle, specificStyles, preventAddingShapes) {
		var baseShapes = feature.baseShapes,
			numSpecificStyles = specificStyles ? specificStyles.length : 1,
			numBaseShapes = baseShapes.length;

		if (numSpecificStyles > numBaseShapes) {
			// add missing shapes
			// preventAddingShapes matters only for point features
			if (!preventAddingShapes)
			for (var i=numBaseShapes; i<numSpecificStyles; i++) {
				var shape = this.createShape(feature, coords);
				// connect events to the shape
				this._connectEvents(shape, feature);
				baseShapes.push(shape);
			}
		}
		else if (numSpecificStyles < numBaseShapes) {
			// remove excessive shapes
			for (var i=numBaseShapes-1; i>=numSpecificStyles; i--) {
				var shape = baseShapes.pop();
				this._removeShape(shape, feature);
			}
			
		}
	},
	
	_connectEvents: function(shape, feature) {
		var handles = feature.handles;
		for (var handle in handles) {
			var events = handles[handle][0],
				context = handles[handle][1],
				method = handles[handle][2],
				eventConnections = handles[handle][3];
			dojo.forEach(events, function(event, eventIndex){
				eventConnections[eventIndex].push( [shape, shape.connect(event, this.engine.normalizeCallback(feature, event, context, method))] );
			}, this);
		}
	},
	
	_removeShape: function(shape, feature) {
		var handles = feature.handles;
		shape.removeShape();
		// disconnect events from the shape
		for (var handle in handles) {
			var events = handles[handle][0],
				eventConnections = handles[handle][3];
			dojo.forEach(events, function(event, eventIndex){
				shape.disconnect( eventConnections[eventIndex].pop()[1] );
			});
		}
	},
	
	remove: function(feature) {
		if (feature.visible) {
			dojo.forEach(feature.baseShapes, function(shape){
				this._removeShape(shape, feature);
			}, this);
		}
	},
	
	show: function(feature, show) {
		if (show) {
			var container = feature.state.gfxContainer;
			// we don't need the container anymore
			delete feature.state.gfxContainer;

			dojo.forEach(feature.baseShapes, function(shape){
				container.add(shape);
			}, this);
		}
		else {
			if (feature.baseShapes.length) {
				// save shapes container for possible future use
				// all base shapes are supposed to be in the same gfx container
				feature.state.gfxContainer = feature.baseShapes[0].getParent();
			}
			dojo.forEach(feature.baseShapes, function(shape){
				shape.removeShape();
			});
		}
	},

	createText: function(feature, textStyle) {
		var shape = feature.baseShapes[0],
			label = this._getLabel(feature, textStyle),
			textShape;

		if (label) {
			var coords = feature.getCoords();
			if (feature.getType() == "Point") {
				var x = this.getX(coords[0]),
					y = this.getY(coords[1]);
				textShape = this.text.createText({
						x: x,
						y: y,
						text: label
					}).
					setTransform(dojox.gfx.matrix.scaleAt(1/this.lengthDenominator, x, y ));
			}
			if (textShape) {
				if (textStyle.fill) {
					textShape.setFill(textStyle.fill);
				}
				
			}
		}
		return textShape;
	},

	translate: function(position, feature) {
		var baseShapes = feature.baseShapes,
			textShape = feature.textShapes,
			oldPosition = feature.getCoords(),
			transform = {dx:this.getX(position[0])-this.getX(oldPosition[0]), dy:this.getY(position[1])-this.getY(oldPosition[1])};

		dojo.forEach(baseShapes, function(shape){
			shape.applyLeftTransform(transform);
		}, this);
		
		if (textShape) {
			textShape.applyLeftTransform(transform);
		}
	},

	rotate: function(orientation, feature) {
		var baseShapes = feature.baseShapes,
			heading = dojo.isObject(orientation) ? orientation.heading : orientation,
			oldHeading = feature.orientation ? feature.orientation.heading : feature.heading,
			deltaHeading = -oldHeading + heading;

		dojo.forEach(baseShapes, function(shape){
			shape.applyRightTransform(dojox.gfx.matrix.rotate(deltaHeading));
		}, this);
	},
	
	makePathString: function(entities, depth) {
		var pathString = "";
		if (depth == 1) {
			pathString = "M" + this.getX(entities[0][0]) + "," + this.getY(entities[0][1]);
			for(var i=1; i<entities.length; i++) {
				pathString += "L" + this.getX(entities[i][0]) + "," + this.getY(entities[i][1]) 
			}
		}
		else {
			dojo.forEach(entities, function(entity){
				pathString += this.makePathString(entity, depth-1);
			}, this);
		}
		return pathString;
	}
});

}());
