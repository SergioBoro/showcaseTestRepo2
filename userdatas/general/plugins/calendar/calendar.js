function createCalendar(parentId, data, template) {
	require(["dijit/registry", "dojox/calendar/Calendar", "dojo/store/Memory"],
			  function(registry, Calendar, Memory){
			    	
			    	var w = registry.byId(parentId);
			    	if(w){
			    		w.destroy();			    		
			    	}
			    	
			    	var calendar = new Calendar({
			    		  date: data.metadata.date,
			    		  columnViewProps:{minHours:data.metadata.minHours, maxHours:data.metadata.maxHours},
			    		  store: new Memory({data: data.data}),
			    		  dateInterval: data.metadata.dateInterval,
			    		  style: data.metadata.style,
			    		  editable: data.metadata.editable
			    		}, parentId);
			    	
			    	calendar.on("itemClick", function(e){
//			    		  console.log("Item clicked", e.item);
			    		gwtPluginFunc(parentId, e.item.id);
			    	});
			      
			  }
	);
}
