function createCalendar(parentId, data, template) {
	require(["dijit/registry", "dojox/calendar/Calendar", "dojo/store/Memory"],
			  function(registry, Calendar, Memory){
		
		            var dateInterval      = data.metadata.dateInterval;
		            var dateIntervalSteps = data.metadata.dateIntervalSteps;
			    	
			    	var w = registry.byId(parentId);
			    	if(w){
			    		
			    		if(template){
				    		dateInterval      = w.dateInterval;
				    		dateIntervalSteps = w.dateIntervalSteps;
			    		}
			    		
			    		w.destroy();
			    		
			    	}
			    	
			    	var calendar = new Calendar({
			    		  date: data.metadata.date,
			    		  columnViewProps:{minHours:data.metadata.minHours, maxHours:data.metadata.maxHours},
			    		  store: new Memory({data: data.data}),
			    		  dateInterval: dateInterval,
						  dateIntervalSteps: dateIntervalSteps,
			    		  style: data.metadata.style,
			    		  editable: data.metadata.editable
			    		}, parentId);
			    	
			    	if(data.metadata.timeSlotDuration){
			    		calendar.columnView.timeSlotDuration = data.metadata.timeSlotDuration;
			    	}
			    	
			    	if(data.buttons){
						for(var i = 0; i<data.buttons.length; i++){
							if(data.buttons[i].hide){
						    	calendar[data.buttons[i].id].domNode.parentNode.removeChild(calendar[data.buttons[i].id].domNode);
							}
						}
			    	}
			    	
			    	
			    	calendar.matrixView.itemToRendererKindFunc = function(item){
		    		    return "horizontal";
		    		};
			    	
			    	
			    	calendar.on("itemClick", function(e){
//			    		  console.log("Item clicked", e.item);
			    		gwtPluginFunc(parentId, e.item.id);
			    	});
			    	
			    	calendar.on("gridDoubleClick", function(e){
//			    		  console.log("Item clicked", e.item);
//			    		gwtPluginFunc(parentId, "addItem");
			    		gwtPluginFunc(parentId, "addItem", e.date.toString(), "ADD_CONTEXT");			    		
			    	});
					
			    	calendar.on("onItemEditBegin", function(e){
//			    		  console.log("Item clicked", e.item);
			    		gwtPluginFunc(parentId, e.item.id + "Delete");
			    	});	
					
			    	if(!data.metadata.toolbarVisible){
				        var div = document.getElementById(parentId).parentNode;
				        if(div){
					        var div2 = div.firstChild;
					        if(div2){
					        	for (var index = 0; index < div2.childNodes.length; index++) {
					        		if(div2.childNodes[index].className == "buttonContainer"){
					        			div2.removeChild(div2.childNodes[index]);
					        		}
					        	}
					        }
				        }
			    	}
			    	
			    	
			    	
			      
			  }
	);
}
