function setCurrentUserDetailsForViewInHTMLControl(preffix)
{
	
	if (document.getElementById('CURRENT_USER_CONTROL_' + preffix)) {
	 document.getElementById('CURRENT_USER_CONTROL_' + preffix).innerHTML=getCurrentUserNameFeedbackJSNIFunction();
	}
	
	if (document.getElementById('CURRENT_USER_CONTROL_FULLNAME_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_FULLNAME_' + preffix).innerHTML=getCurrentUserFullNameFeedbackJSNIFunction();
	}
	
	if (document.getElementById('CURRENT_USER_CONTROL_EMAIL_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_EMAIL_' + preffix).innerHTML=getCurrentUserEMailFeedbackJSNIFunction();
	}

	if (document.getElementById('CURRENT_USER_CONTROL_SID_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_SID_' + preffix).innerHTML=getCurrentUserSIDFeedbackJSNIFunction();
	}

	if (document.getElementById('CURRENT_USER_CONTROL_PHONE_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_PHONE_' + preffix).innerHTML=getCurrentUserPhoneFeedbackJSNIFunction();
	}	

}

function safeIncludeJS(jsFile) { 
	        dojo.xhrGet({ 
	                url: jsFile, 
	                sync: true,
	                load: function(responce, ioArgs) { 
	                        if (responce != null) { 
                                var newscript = document.createElement('script'); 
	                                newscript.text = responce; 
	                                newscript.type = "text/javascript"; 
	                                var body = document.body; 
	                                body.appendChild(newscript); 
                        } 
	                else { 
                        console.log("failed load script!");    
	                     } 
	              }                
	        }); 
	
}

function getErrorByIFrame(iframeName)
{
	var err = null; 
  
	var iframe = document.getElementsByName(iframeName)[0];
	
	if(iframe.contentDocument != null){
		var body = iframe.contentDocument.getElementsByTagName("body")[0];
		
		if((body != null) && (typeof body != "undefined")) {
			var message = body.innerHTML;
			if((message.trim() != "") && (message.trim() != "<pre></pre>") && (message.trim() != "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\"></pre>")) {
				err = message;
				
				err = err.replace("<root>", "").replace("</root>", "");
			}
		}
	}
	
	return err; 
}

function addUpload(formId)
{
	var baseForm = document.getElementById(formId); 
	
	var form = baseForm.cloneNode(true);
	
	var lastAddingId = baseForm.getAttribute("lastAddingId");
	if(lastAddingId)
	{
		lastAddingId++; 
	}
	else
	{
		lastAddingId = 1;
	}
	baseForm.setAttribute("lastAddingId",lastAddingId);
	form.setAttribute("id", baseForm.getAttribute("id")+"_add_"+lastAddingId);
	
	var baseTarget = baseForm.getAttribute("target");	
	var target = baseTarget+"_add_"+lastAddingId;
	form.setAttribute("target", target);	
	
	
	var inputs = form.getElementsByTagName("input");
	for (var i=0; i<inputs.length; i++)
	{
		var name = inputs[i].getAttribute("name");
		if(name.indexOf("@@filedata@@") > -1)
		{
			var onchange = inputs[i].getAttribute("onchange");
			if(onchange)
			{
				onchange = onchange.replace("add_upload_index_0", "add_upload_index_"+lastAddingId);
				inputs[i].setAttribute("onchange", onchange);
			}
			break;
		}	
	}
	
	
	baseForm.parentNode.appendChild(form);
	
//--------------	
	
	var baseFrame = document.getElementsByName(baseTarget)[0];
	
	var frame = baseFrame.cloneNode(true);

	frame.setAttribute("name", target);
	
	var onload = frame.getAttribute("onload");
	onload = onload.replace(baseTarget, target);
	frame.setAttribute("onload", onload);
	
	baseFrame.parentNode.appendChild(frame);
	
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
	  chartOptions.legend = chartOptions.legend ? chartOptions.legend : {};
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


