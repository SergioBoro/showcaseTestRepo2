dojo.provide("course.geo.control.Tooltip");

dojo.require("dijit.Tooltip");

dojo.require("course.geo.control.Base");
dojo.require("dojox.gfx.matrix");

(function(){
	var DEFAULT_TEXT = function(feature){
		return feature.tooltip || feature.name || feature.id;
	};
	
	var position = ["above", "below"];

	var m = dojox.gfx.matrix, pi4 = Math.PI / 4, pi2 = Math.PI / 2;
	
	var tooltip; // a singleton!
	var showTooltip = function(/*String*/ innerHTML, /*DomNode*/ aroundNode, /*String[]?*/ position, /*Boolean*/ rtl){
		if(!tooltip){ tooltip = createTooltip(); }
		return tooltip.show(innerHTML, aroundNode, position, rtl);
	};
	var hideTooltip = function(aroundNode){
		if(!tooltip){ tooltip = createTooltip(); }
		return tooltip.hide(aroundNode);
	};
	var createTooltip = function() {
		// create a tooltip without a dijitTooltipConnector
		var tooltip = new dijit._MasterTooltip();
		dojo.style(tooltip.connectorNode, "display", "none");
		return tooltip;
	}
	
	dojo.declare("course.geo.control.Tooltip", course.geo.control.Base, {
		//	summary:
		//		Create an action on a plot where a tooltip is shown when hovering over an element.

		// the data description block for the widget parser
		defaultParams: {
			text: DEFAULT_TEXT	// the function to produce a tooltip from the object
		},
		optionalParams: {},	// no optional parameters

		constructor: function(targets, kwArgs) {
			this.text = kwArgs && kwArgs.text ? kwArgs.text : DEFAULT_TEXT;
			
			this.connect();
		},
		
		process: function(feature, evt, evtType) {
			if(evtType == "onmouseout"){
                hideTooltip(this.aroundRect);
				this.aroundRect = null;
				return;
			}

			var factory =  feature.map.engine.getFactory(feature.type),
				featureBbox = feature.getBbox(),
				centerX = factory.getX( (featureBbox[0]+featureBbox[2])/2 ),
				centerY = factory.getY( (featureBbox[1]+featureBbox[3])/2 );
			var realMatrix = feature.baseShapes[0]._getRealMatrix() || {xx:1,xy:0,yx:0,yy:1,dx:0,dy:0};
			var point = dojox.gfx.matrix.multiplyPoint(realMatrix, centerX, centerY);
			
			// calculate relative coordinates and the position
			var aroundRect = {type: "rect", x: point.x, y:point.y, width:0, height:0};

			
			// adjust relative coordinates to absolute, and remove fractions
			var lt = dojo.coords(feature.map.container, true);
			aroundRect.x += lt.x;
			aroundRect.y += lt.y;
			aroundRect.x = Math.round(aroundRect.x);
			aroundRect.y = Math.round(aroundRect.y);
			aroundRect.width = Math.ceil(aroundRect.width);
			aroundRect.height = Math.ceil(aroundRect.height);
			this.aroundRect = aroundRect;
			
            showTooltip(this.text(feature), this.aroundRect, position);
		}
	});
})();
