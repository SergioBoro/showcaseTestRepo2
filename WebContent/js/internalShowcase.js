function setCurrentUserNameForViewInHTMLControl(preffix)
{
	
	if (document.getElementById('CURRENT_USER_CONTROL_' + preffix)) {
	 document.getElementById('CURRENT_USER_CONTROL_' + preffix).innerHTML=getCurrentUserNameFeedbackJSNIFunction();
	}

}

function getErrorByIFrame(iframeName)
{
	var err = null; 
  
	var iframe = document.getElementsByName(iframeName)[0];
	
	var body = iframe.contentDocument.getElementsByTagName("body")[0];
	
	if((body != null) && (typeof body != "undefined")) {
		var message = body.innerHTML;
		if(message.trim() != "") {			
			err = message;
		}
	}
	
	return err; 
}

var convertorFunc = function(chartId, chartLegendId, optionSet1, optionSet2) {

   if (dojo.isString(optionSet1)) optionSet1 = dojo.fromJson(optionSet1);
   if (dojo.isString(optionSet2)) optionSet2 = dojo.fromJson(optionSet2);
   var chartOptions = optionSet2;

   // copy id, seriesstrokeWidth: 2,
   chartOptions.id = chartId;
   chartOptions.series = optionSet1.series;
   
   // copy width and height
   if (optionSet1.width) chartOptions.width = optionSet1.width;
   if (optionSet1.height) chartOptions.height = optionSet1.height;

   // copy legend settings
   if (chartLegendId) {
      chartOptions.legend = {id: chartLegendId, options: {horizontal: false}};
   }

   //copy eventHandler
   if (optionSet1.eventHandler) chartOptions.eventHandler = optionSet1.eventHandler;

   // copy labels to the chartOptions
   var axis = chartOptions.axis;
   
   if (chartOptions.axisX && optionSet1.labelsX) {
      chartOptions.axisX.labels = optionSet1.labelsX;
   }
   if (chartOptions.axisY && optionSet1.labelsY) {
      chartOptions.axisY.labels = optionSet1.labelsY;
   }

   return chartOptions;
};

var eventCallbackChartHandler = function(chartEvent) {
   if (chartEvent.type=="onclick") {
      //console.debug(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
      gwtChartFunc(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
   }
};

var eventHandler2 = function(chartEvent) {
   if (chartEvent.type=="onclick") console.debug("2", chartEvent);
};
