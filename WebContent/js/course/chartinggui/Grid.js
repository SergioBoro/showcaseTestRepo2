dojo.provide("course.chartinggui.Grid");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.SimpleTextarea");

dojo.requireLocalization("course.chartinggui", "Theme");

(function() {

dojo.declare("course.chartinggui.Grid", course.chartinggui.Option, {
    
    templateString: dojo.cache("course.chartinggui", "templates/Grid.html"),
    
    constructor: function(params, srcNodeRef) {
        this.chartOption = undefined;
		var plot = params.chartOptions.plot;
		if (plot.length>1) {
			this.chartOption = plot[1]
		}
    },
    
    watchChanges: function() {
		/*
        dojo.connect(this.themeSelect, "onChange", dojo.hitch(this, function(value){
            if (this.ignoreFirstChange) {delete this.ignoreFirstChange; return;}
            if (value == this.noThemeValue) value= "";
            this.optionChanged(this.optionId, value);
        }));
        */
    },
    
    setChartOption: function() {
		this.ignoreFirstChange = true;
		this.gridSettings.set("value", dojo.toJson(this.chartOption));
		this.showGrid.set("value", true);
    }
});

})();