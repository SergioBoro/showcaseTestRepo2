dojo.provide("course.geo.gfx.Placemark");

(function() {

dojo.declare("course.geo.gfx.Placemark", null, {
	
	constructor: function(kwArgs) {
		dojo.mixin(this, kwArgs);
	},
	
	moveTo: function(path, point) {
		path.moveTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	lineTo: function(path, point) {
		path.lineTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	getX: function(x) {
		return x-this.map.extent[0];
	},
	
	getY: function(y) {
		return this.map.extent[3]-y;
	},
	
	makePoint: function(feature, coordinates) {
		// just return coordinates to reference them in this.applyPointStyle
		return coordinates;
	},
	
	makeLineString: function(feature, coordinates, path) {
		if (!path) path = this.lines.createPath();
		this.moveTo(path, coordinates[0]);
		for(var i=1; i<coordinates.length; i++) {
			this.lineTo(path, coordinates[i]);
		}
		return path;
	},

	makePolygon: function(feature, coordinates, path) {
		if (!path) path = this.polygons.createPath();
		dojo.forEach(coordinates, function(lineStringCoords){
			this.moveTo(path, lineStringCoords[0]);
			for(var i=1; i<lineStringCoords.length; i++) {
				this.lineTo(path, lineStringCoords[i]);
			}
		}, this);
		return path;
	},
	
	makeMultiLineString: function(feature, coordinates) {
		var path = this.lines.createPath();
		dojo.forEach(coordinates, function(lineStringCoords){
			this.makeLineString(feature, lineStringCoords, path);
		}, this);
		return path;
	},
	
	makeMultiPolygon: function(feature, coordinates) {
		var path = this.polygons.createPath();
		dojo.forEach(coordinates, function(polygonCoords){
			this.makePolygon(feature, polygonCoords, path);
		}, this);
		return path;
	},
	
	applyPointStyle: function(shape, feature, calculatedStyle, specificStyle, factory) {
		var coordinates = shape,
			type = specificStyle.type,
			width,
			height,
			lengthDenominator = getLengthDenominator(this.group),
			scale = get("scale", calculatedStyle, specificStyle),
			transform = [dojox.gfx.matrix.translate(this.getX(coordinates[0]), this.getY(coordinates[1]))];
		shape = null;

		// find width and height
		if (specificStyle) {
			width = specificStyle.width ? specificStyle.width : specificStyle.size;
			height = specificStyle.height ? specificStyle.height : specificStyle.size;
		}
		if (!width) width = calculatedStyle.width ? calculatedStyle.width : calculatedStyle.size;
		if (!height) height = calculatedStyle.height ? calculatedStyle.height : calculatedStyle.size;
		
		if (!scale) scale = 1;

		if (type == "shape" && shapes[specificStyle.shapeType]) {
			var shapeDef = shapes[specificStyle.shapeType],
				size = specificStyle.shapeType=="circle" ? 1 : shapeDef.size,
				_scale = scale/lengthDenominator/size;

			transform.push(dojox.gfx.matrix.scale(_scale*width, _scale*height));
			
			if (specificStyle.shapeType=="circle") {
				shape = this.points.createCircle({cx:0, cy:0, r:0.5});
			}
			else {
				shape = this.points.createPolyline(shapeDef.points);
				transform.push( dojox.gfx.matrix.translate(-shapeDef.center[0], -shapeDef.center[1]) );
			}
			applyFill(shape, calculatedStyle, specificStyle);
			applyStroke(shape, calculatedStyle, specificStyle, size/height/scale);
		}
		else if (type == "image") {
			var imageDef = {
				type: "image",
				src: specificStyle.src,
				width: width,
				height: height,
				x: (specificStyle.x === undefined) ? -width/2 : specificStyle.x,
				y: (specificStyle.y === undefined) ? -height/2 : specificStyle.y
			}
			shape = this.points.createImage(imageDef);
			transform.push(dojox.gfx.matrix.scale(1/lengthDenominator*scale));
		}
		if (shape) {
			shape.setTransform(transform);
		}
		return shape;
	},
	
	applyLineStyle: function(shape, feature, calculatedStyle, specificStyle, factory) {
		applyStroke(shape, calculatedStyle, specificStyle, 1/getLengthDenominator(this.group));
		return shape;
	},
	
	applyPolygonStyle: function(shape, feature, calculatedStyle, specificStyle, factory) {
		applyFill(shape, calculatedStyle, specificStyle);
		applyStroke(shape, calculatedStyle, specificStyle, 1/getLengthDenominator(this.group));
		return shape;
	}
});


var patchStyle = function(styleDef, group) {
	if (dojox.gfx.renderer == "vml") return;
	var lengthDenominator = (group._getRealMatrix()||{xx:1}).xx;
	
	var fill = styleDef.fill;
	if (dojo.isObject(fill) && fill.type == "pattern") { //pattern
		if (fill.width) fill.width = fill.width/lengthDenominator;
		if (fill.height) fill.height = fill.height/lengthDenominator;
	}
};

var get = function(attr, calculatedStyle, specificStyle) {
	return specificStyle&&specificStyle[attr] ? specificStyle[attr] : calculatedStyle[attr];
};
	
var normalizeLength = function(group, width) {
	var matrix = group._getRealMatrix();
	return (dojox.gfx.renderer != "vml") ? width/(matrix||{xx:1}).xx : width;
};

var getLengthDenominator = function(shape) {
	return (shape._getRealMatrix()||{xx:1}).xx;
};

var applyFill = function(shape, calculatedStyle, specificStyle) {
	var fill = get("fill", calculatedStyle, specificStyle);
	shape.setFill(fill);
};

var applyStroke = function(shape, calculatedStyle, specificStyle, widthMultiplier) {
	if (dojox.gfx.renderer == "vml") widthMultiplier=1;
	var stroke = get("stroke", calculatedStyle, specificStyle);
	var strokeWidth = get("strokeWidth", calculatedStyle, specificStyle);
	shape.setStroke({color: stroke, width:strokeWidth*widthMultiplier});
};

var shapes = {
	circle: 1, // a dummy value
	star: {
		center: [500, 500],
		size: 1000,
		points:[500,24, 618,388, 1000,388, 691,612, 809,976, 500,751, 191,976, 309,612, 0,388, 382,388, 500,24]
	},
	cross: {
		center: [5,5],
		size: 10,
		points: [4,0, 6,0, 6,4, 10,4, 10,6, 6,6, 6,10, 4,10, 4,6, 0,6, 0,4, 4,4, 4,0]
	},
	x: {
		center: [50,50],
		size: 100,
		points: [0,0, 25,0, 50,35, 75,0, 100,0, 65,50, 100,100, 75,100, 50,65, 25,100, 0,100, 35,50, 0,0]
	},
	square: {
		center: [1,1],
		size: 2,
		points: [0,0, 0,2, 2,2, 2,0, 0,0]
	},
	triangle: {
		center: [5,5],
		size: 10,
		points: [0,10, 10,10, 5,0, 0,10]
	}
};

}())
