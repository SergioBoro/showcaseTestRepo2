dojo.provide("course.geo.widget.Legend");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("course.geo.widget.Legend", [dijit._Widget, dijit._Templated], {
	// summary: A legend for a map.
	
	templateString: "<table dojoAttachPoint='legendNode' class='dojoxLegendNode' role='group' aria-label='map legend'><tbody dojoAttachPoint='legendBody'></tbody></table>",
	legendNode: null,
	legendBody: null,

	postCreate: function() {
		if (!this.map) return;
		
		this.refresh();
	},
	refresh: function(){
		// summary: regenerates the legend to reflect changes to the map

		// cleanup
		while(this.legendBody.lastChild){
			dojo.destroy(this.legendBody.lastChild);
		}
		
		var m = this.map,
			styleByClass = m.styleByClass;
		
		// process styleByClass
		for (var styleClass in styleByClass) {
			this._processStyle(styleByClass[styleClass], m.featuresByClass[styleClass]);
		};
		
		// process inline styles
		this._processInlineStyle(m.document);
		
	},
	
	_processInlineStyle: function(featureContainer) {
		if (featureContainer.style) this._processStyle(featureContainer.style, [featureContainer]);
		dojo.forEach(featureContainer.features, function(feature) {
			if (feature.type == "FeatureContainer") this._processInlineStyle(feature);
			else if (feature.style) this._processStyle(feature.style, [feature]);
		}, this);
	},
	
	_processStyle: function(styles, affectedFeatures) {
		dojo.forEach(styles, function(style){
			var tr = dojo.create("tr", null, this.legendBody),
				td = dojo.create("td", null, tr),
				name = (this.getName) ? this.getName(style) : style.def.name;
			if (style.styleFunction) {
				var getLegend = style.styleFunction.getLegend;
				if (getLegend) {
					getLegend = dojo.isString(getLegend) ? dojo.getObject(getLegend) : getLegend;
					getLegend(td, style, affectedFeatures, name);
				}
			}
		}, this);
	}
});
