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

   // copy id, width and height
   chartOptions.id = chartId;   
   if (optionSet1.width) chartOptions.width = optionSet1.width;
   if (optionSet1.height) chartOptions.height = optionSet1.height;
   
   // copy series
   chartOptions.series = optionSet1.series;
   
   // copy labels to the chartOptions
   if (chartOptions.axisX && optionSet1.labelsX) {
      chartOptions.axisX.labels = optionSet1.labelsX;
   }
   if (chartOptions.axisY && optionSet1.labelsY) {
      chartOptions.axisY.labels = optionSet1.labelsY;
   }   

   // copy legend settings
   if (chartLegendId) {
	   if (!chartOptions.legend) {
		   chartOptions.legend = {};
	   }
      chartOptions.legend.id = chartLegendId;
      chartOptions.legend.options =  {horizontal: false};
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
