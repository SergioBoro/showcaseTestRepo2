function createLyraDGrid(elementId, parentId, metadata) {
	require([
	         "dijit/form/Button",
	         "dijit/form/DropDownButton",
	         "dijit/form/ComboButton",
	         "dijit/form/ToggleButton",
	         "dijit/form/CurrencyTextBox",
	         "dijit/form/DateTextBox",
	         "dijit/form/NumberSpinner",
	         "dijit/form/NumberTextBox",
	         "dijit/form/TextBox",
	         "dijit/form/TimeTextBox",
	         "dijit/form/ValidationTextBox",
	         "dijit/form/SimpleTextarea",
	         "dijit/form/Textarea",
	         "dijit/form/Select",
	         "dijit/form/ComboBox",
	         "dijit/form/MultiSelect",
	         "dijit/form/FilteringSelect",
	         "dijit/form/HorizontalSlider",
	         "dijit/form/VerticalSlider",
	         "dijit/form/CheckBox",
	         "dijit/form/RadioButton",
	         "dijit/form/DataList",

			 "dojo/_base/lang",
	         "dojo/has",			 
			 "dgrid/List",
			 "dgrid/OnDemandGrid",
	         "dgrid/extensions/CompoundColumns", 
	         "dgrid/ColumnSet", 
			 "dgrid/extensions/ColumnResizer",
			 "dgrid/Selection",
	         "dgrid/CellSelection", 				
			 "dgrid/Editor",
			 "dgrid/Keyboard",
			 "dojo/_base/declare",
	         "dstore/QueryResults",
			 "dstore/Rest",
			 "dstore/Trackable",
			 "dstore/Cache",
	     	 "dojo/when",	         
			 "dojo/domReady!"
	         ],	function(
	        	 Button,DropDownButton,ComboButton,ToggleButton,CurrencyTextBox,DateTextBox,NumberSpinner,NumberTextBox,TextBox,TimeTextBox,ValidationTextBox,SimpleTextarea,Textarea,Select,ComboBox,MultiSelect,FilteringSelect,HorizontalSlider,VerticalSlider,CheckBox,RadioButton,DataList,	        		 
	        	 lang, has, List, Grid, CompoundColumns, ColumnSet, ColumnResizer, Selection, CellSelection, Editor, Keyboard, declare, QueryResults, Rest, Trackable, Cache, when
		     ){
    	
		
		var webSocket;
	    if(arrGrids[parentId] && arrGrids[parentId].webSocket && (arrGrids[parentId].webSocket.readyState == arrGrids[parentId].webSocket.OPEN)){
    		webSocket = arrGrids[parentId].webSocket;
    	}else{
    		
    		var protocol;
    		if(window.location.protocol.indexOf("https") > -1){
    			protocol = "wss";
    		} else {
    			protocol = "ws";
    		}
    		
    		webSocket = new WebSocket(protocol+"://"+window.location.host+window.location.pathname+"secured/JSLyraGridScrollBack");
            webSocket.onopen = function(){
             	var httpParams = gwtGetHttpParamsLyra(elementId, -1000, -1000, null, null);
                webSocket.send(httpParams);
            };
            webSocket.onmessage = function(message){
    			var grid = arrGrids[parentId];
    			
    			var pos = parseInt(message.data);
    			pos = pos * grid.rowHeight;
    			pos = pos + arrGrids[parentId].getScrollPosition().y-Math.floor(arrGrids[parentId].getScrollPosition().y/grid.rowHeight)*grid.rowHeight;
    			backScroll = true;
    			grid.scrollTo({x:0, y:pos});
            };
    	}
		
		
		    var firstLoading = true;		
		    
		    
//----------------------Debug		    
		    var backScroll = false;
		    var resScroll = null;
//----------------------Debug
		    

			var store = new declare([ Rest, Trackable, Cache ])(lang.mixin({
				target:"secured/JSLyraGridService",
				idProperty: "id",
				
				_fetch: function (kwArgs) {

					
					if(backScroll){
//----------------------Debug
							
						    gwtSetOldPositionLyra(elementId, kwArgs[0].start);
						
//							console.log("backScroll21");
							
							results =  new QueryResults(when(resScroll), {
								totalLength: when(arrGrids[parentId]._total)
//								totalLength: when(50000)
							});
							
							//gwtAfterLoadDataLyra(elementId, null, "50000");
							//gwtAfterLoadDataLyra(elementId, null, arrGrids[parentId]._total);

							setTimeout(function(){
							   backScroll = false;
//							}, 50);
							}, 150);
					
							
							return results;

//----------------------Debug
						
					} else {
						var results = null;

						var sortColId  = null;
						var sortColDir = null;
						
			 	    	var httpParams = gwtGetHttpParamsLyra(elementId, kwArgs[0].start, kwArgs[0].end-kwArgs[0].start, sortColId, sortColDir);
			 	    	httpParams = eval('('+httpParams+')');	 	 
			 	    	
					    var scparams = {};
					    scparams[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
					    scparams[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
					    kwArgs["scparams"] = scparams;			
					    
					    kwArgs.start = kwArgs[0].start;
					    kwArgs.end = kwArgs[0].end;					    

						results = Rest.prototype.fetchRange.call(this, kwArgs);
						results.then(function(results){
							var events = null;
							if(results[0]){

//----------------------Debug								
								resScroll = results;
//----------------------Debug
								
								if(results[0]["events"]){
									events = results[0]["events"];
								}
							}
							gwtAfterLoadDataLyra(elementId, events, arrGrids[parentId]._total);
						});
						
						return results;
					}
					
				},
				
				
				put: function(object){
					if((object.id).indexOf("addRecord") > -1 ){ // Добавление записи
						
						object["editor"] = "addRecord";
						
						var strObject = JSON.stringify(object);
			 	    	var httpParams = gwtEditorGetHttpParamsLyra(elementId, strObject, object["editor"]);
			 	    	httpParams = eval('('+httpParams+')');

					    object[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
					    object[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
					    
						var result = this.inherited(arguments);
						result.then(function(value){
								if(value.success == '1'){
									arrGrids[parentId].refresh();								
								}
								else{
								}
								gwtShowMessageLyra(elementId, value.message, object["editor"]);
						    }, function(err){
						    	alert("Произошла ошибка при добавлении записи:\n"+err+"\nПодробности находятся в консоли броузера.");
						    });
					    return result;
						
					}else{  //Сохранение
						
						object["editor"] = "save";
						
						var strObject = JSON.stringify(object, function(key, value) {
							  if (
									  (key == "dirty")							  
								   || (key == "gridContextName") 
							       || (key == "elementInfoName")
							       || (key == object["gridContextName"])
							       || (key == object["elementInfoName"])
							     )
							  {
								  return undefined;						  
							  }
							  return value;
						});
			 	    	var httpParams = gwtEditorGetHttpParamsLyra(elementId, strObject, object["editor"]);
			 	    	httpParams = eval('('+httpParams+')');

			 	    	object["gridContextName"] = httpParams["gridContextName"];
			 	    	object["elementInfoName"] = httpParams["elementInfoName"];
					    object[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
					    object[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
		 
					    object.dirty = JSON.stringify(grid.dirty);

						var result = this.inherited(arguments);
						
						result.then(function(value){
								if(value.success == '1'){
								}
								else{
							        grid.dirty = JSON.parse(object.dirty);
								}
								if(value.refreshAfterSave == 'true'){
							        grid.refresh();								
								}
								gwtShowMessageLyra(elementId, value.message, object["editor"]);
						    }, function(err){
						        grid.dirty = JSON.parse(object.dirty);				    	
						    	alert("Произошла ошибка при сохранении данных:\n"+err+"\nПодробности находятся в консоли броузера.");
						    });
						    
					    return result;
						
					}
				}
			}, {} ));
			
			
			var columns = [];
			for(var k in metadata["columns"]) {
				var column = {};
				
				if(metadata["common"]["readonly"] || metadata["columns"][k]["readonly"]){
				}else{
					var editor = eval("("+metadata["columns"][k]["editor"]+")");
					for(var m in editor) {
						column[m] = editor[m];	
					}
					column["editable"] = true;
				}
				
				column["id"]        = metadata["columns"][k]["id"];
				column["parentId"]  = metadata["columns"][k]["parentId"];			
				column["field"]     = metadata["columns"][k]["id"];			
				column["label"]     = metadata["columns"][k]["caption"];
				column["sortable"]  = false;
				column["valueType"] = metadata["columns"][k]["valueType"];
				
				
				function getTitle(title){
					var res = title;
					if(res){
						res = res.replace(/&lt;/g, "<");
						res = res.replace(/&gt;/g, ">");
						res = res.replace(/&amp;/g, "&");
					}
					return res;
				}
				
				column["renderCell"] = function actionRenderCell(object, value, node, options) {
					
					if(!value){
						value = "";
						
					}
					
					var div = document.createElement("div");
					
					switch (this["valueType"]) {
					    case "DOWNLOAD":
							if(value && (value.trim()!="")){
								div.innerHTML = 
									"<tbody>" +
										"<tr>";
								if(value.trim()!="enableDownload"){
									div.innerHTML = div.innerHTML +							
									"<td>"+value+"" +
									"</td>";
								}
								div.innerHTML = div.innerHTML +										
											"<td  align=\"center\" style=\"vertical-align: middle;\">" +
													"<button onclick=\"gwtProcessFileDownloadLyra('"+elementId+"', '"+object.id+"', '"+this.id+"')\">" +
															"<img src="+metadata["columns"][k]["urlImageFileDownload"]+" title=\"Загрузить файл с сервера\"  style=\"vertical-align: middle; align: right; width: 8px; height: 8px;  \"   >" +
													"</button>" +
											"</td>" +
										"</tr>" +
									"</tbody>";
							}else{
								div.innerHTML = value;
							}
					        break; 
					    case "IMAGE":
					    	var start = "<a><img border=\"0\" src=\"";
					    	var end = "\"></a>";
					    	var sep = ":";
					    	
					    	var title = value;
					    	
							if(value.indexOf(sep)>-1){
								title = value.substring(value.indexOf(sep)+1, value.length);
								value = value.substring(0, value.indexOf(sep));
							}
					    	
							div.innerHTML =  start + value + end;
							div.title = title;	
							
					        break; 
					    default:
							div.innerHTML = value;
					    	div.title = value;
					    	break;
					}					
					
			    	div.title = getTitle(div.title);

					return div;
		        };
		        
		        column["renderHeaderCell"] = function actionRenderCell(node) {
					var div = document.createElement("div");
			        if(metadata["common"]["haColumnHeader"]){
						div.style["text-align"] = metadata["common"]["haColumnHeader"];
			        }
					div.innerHTML = this.label;		        
			    	div.title = this.label;
			    	
			    	div.title = getTitle(div.title);
			    	
					return div;
		        };
				
				if(column["editable"]){
					column["canEdit"] = function columnCanEdit(object, value){
						result = true;
						if(object.readonly && (object.readonly.toLowerCase() == "true") ){
							result = false;
						}
						return result;					
					};
				}
				
				columns.push(column);
			}
			
			
			var virtualColumnType = 0;
			var columnSets = null;
			var columnSetWidths = null;
			var compoundColumns = null;
			var allVirtualColumns = null;
			if(metadata["virtualColumns"]){
				for(var vc in metadata["virtualColumns"]) {
					if(metadata["virtualColumns"][vc]["virtualColumnType"] == "COLUMN_HEADER"){
						virtualColumnType = 1;
						break;	 				
					}
					if(metadata["virtualColumns"][vc]["virtualColumnType"] == "COLUMN_SET"){
						virtualColumnType = 2;
						break;					
					}
				}		
							
				if(virtualColumnType == 1){
					compoundColumns = [];
					allVirtualColumns = [];
					
					var i = 1;
					for(var k2 in metadata["virtualColumns"]) {
						var virtualColumn = {};
						if(metadata["virtualColumns"][k2]["virtualColumnType"] == "COLUMN_HEADER"){
							virtualColumn["id"] = "vcol"+i;
							virtualColumn["parentId"] = metadata["virtualColumns"][k2]["parentId"];
							virtualColumn["label"] = metadata["virtualColumns"][k2]["id"];					
							virtualColumn["style"] = metadata["virtualColumns"][k2]["style"];
							virtualColumn["children"] = [];
							
			
							for(var k3 in allVirtualColumns) {
								if(virtualColumn["parentId"] == allVirtualColumns[k3]["label"]){
									allVirtualColumns[k3]["children"].push(virtualColumn);
									break;
								}
							}
							
							allVirtualColumns.push(virtualColumn);
							if(!virtualColumn["parentId"]){
								compoundColumns.push(virtualColumn);
							}
			
							i++;
						} else {
							for(var k11 in columns) {
								if(columns[k11]["label"] == metadata["virtualColumns"][k2]["id"]){
									compoundColumns.push(columns[k11]);
									break;
								}
							}						
						}
					}
					
					for(var k4 in columns) {
						for(var k5 in allVirtualColumns) {
							if(columns[k4]["parentId"] == allVirtualColumns[k5]["label"]){
								allVirtualColumns[k5]["children"].push(columns[k4]);
								break;
							}
						}	
					}
				}
				
				if(virtualColumnType == 2){
					columnSets = [];
					columnSetWidths = [];
					for(var vc in metadata["virtualColumns"]) {
						var columnSet = [];
						for(var kk in columns) {
							if(columns[kk]["parentId"] == metadata["virtualColumns"][vc]["id"]){
								columnSet.push(columns[kk]);
							}
						}
						columnSetWidths.push(metadata["virtualColumns"][vc]["width"]);
						columnSets.push([columnSet]);					
					}
				}
			}

		
		var declareGrid = [Grid, ColumnResizer, Keyboard, Editor];		
		
		var selectionMode;
		if(metadata["common"]["selectionModel"] == "RECORDS"){
			selectionMode = "extended";			
			declareGrid.push(Selection);
		}else{
			selectionMode = "single";			
			declareGrid.push(CellSelection);
		}
		
		if(virtualColumnType == 1){
			declareGrid.push(CompoundColumns);			
		}
		
		if(virtualColumnType == 2){
			declareGrid.push(ColumnSet);			
		}
		
		var isVisibleColumnsHeader = false;
		if(metadata["common"]["isVisibleColumnsHeader"]){
			isVisibleColumnsHeader = true;	
		}
		
		var isAllowTextSelection = false;
		if(metadata["common"]["isAllowTextSelection"]){
			isAllowTextSelection = true;	
		}
		
		
		if(virtualColumnType == 1){
		    columns = compoundColumns;
		}
		
		var sort = null;
		if(metadata["common"]["sortColId"] && metadata["common"]["sortColDirection"]){
			var descending = false;
			if(metadata["common"]["sortColDirection"].toUpperCase()=="DESC"){
				descending = true;	
			}
			
			for(var k in metadata["columns"]) {
				if(metadata["columns"][k]["caption"] == metadata["common"]["sortColId"]){
					sort = [{property: metadata["columns"][k]["id"], descending: descending}];
					store.initialSort = sort;
					break;
				}
			}
		}
		
		
		var grid = new declare(declareGrid)({
			
			columns: columns, 
			columnSets: columnSets,
			sort: sort,
			collection: store,
			getBeforePut: false,

			
			webSocket: webSocket,
			
			minRowsPerPage: parseInt(metadata["common"]["limit"]),
			maxRowsPerPage: parseInt(metadata["common"]["limit"]),
			bufferRows: 0,
//			bufferRows: 10,
//			bufferRows: 1,
			farOffRemoval: 0,
			pagingDelay: 50,			
			
//----------------------Debug			
//			maxEmptySpace: 100,
//			queryRowsOverlap: 10,
//----------------------Debug
			
			
			selectionMode: selectionMode,
			allowTextSelection: isAllowTextSelection,			
			showHeader: isVisibleColumnsHeader,
			loadingMessage: metadata["common"]["loadingMessage"],
			noDataMessage: metadata["common"]["noDataMessage"],
//			pagingDelay: 50,
			deselectOnRefresh: false,
			keepScrollPosition: true,
			readonly: metadata["common"]["readonly"],
			
			renderRow: function (object) {
			     var rowElement = this.inherited(arguments);
				 if(object.rowstyle && (object.rowstyle != "")){
						rowElement.className = rowElement.className +" "+ object.rowstyle +" ";
				 }
			     return rowElement;
			}
			
		},  parentId);
	    arrGrids[parentId] = grid;
	    
	    
		for(var k in metadata["columns"]) {
			grid.styleColumn(metadata["columns"][k]["id"], metadata["columns"][k]["style"]);
		}
		if(virtualColumnType == 1){
		    for(var k1 in allVirtualColumns) {
				if(allVirtualColumns[k1]["style"]){
					grid.styleColumn(allVirtualColumns[k1]["id"], allVirtualColumns[k1]["style"]);
				}	
			}
		}
		if(virtualColumnType == 2){
			for(var vcc in columnSets) {
				if(columnSetWidths[vcc]){
					grid.styleColumnSet(vcc, "width:"+columnSetWidths[vcc]+";");					
				}
			}
		}
		
		
		grid.on("dgrid-select", function(event){
			if(firstLoading){
				gwtAfterClickLyra(elementId, metadata["common"]["selRecId"], metadata["common"]["selColId"], getSelection());				
			} else {
				setTimeout(function(){
					if(!grid.readonly){
						if(grid.currentRowId != grid.row(event.grid._focusedNode).id){
							grid.currentRowId = grid.row(event.grid._focusedNode).id;
							grid.save();
						}
					}
					
					gwtAfterClickLyra(elementId, grid.row(event.grid._focusedNode).id, grid.column(event.grid._focusedNode).label, getSelection());
				}, 50);
			}
		});
		grid.on(".dgrid-row:dblclick", function(event){
			gwtAfterDoubleClickLyra(elementId, grid.row(event).id, grid.column(event).label, getSelection());
		});
		function getSelection()
		{
		    var selection = "";
		    var i = 0;
	        for(var id in grid.selection){
	            if(grid.selection[id]){
	            	if(i > 0){
		            	selection = selection+metadata["common"]["stringSelectedRecordIdsSeparator"];	
	            	}
	            	selection = selection+id;
	            	i++;
	            }
	        }
	        return selection; 
		}
		grid.on("dgrid-refresh-complete", function(event) {
			if(firstLoading){
				if(metadata["common"]["selectionModel"] == "RECORDS"){
					if(metadata["common"]["selRecId"]){
						event.grid.select(event.grid.row(metadata["common"]["selRecId"]));
					}
				}else{
					if(metadata["common"]["selRecId"] && metadata["common"]["selColId"]){
						for(var col in event.grid.columns){
							if(event.grid.columns[col].label == metadata["common"]["selColId"]){
								event.grid.select(event.grid.cell(metadata["common"]["selRecId"], col));
								break;
							}
						}
					}
				}
				firstLoading = false;
			}
		});
		grid.on("dgrid-datachange", function(event){
			if(typeof event.value === "string"){
				if(event.value.indexOf("<") > -1){
					event.returnValue = false;
					console.log("Заблокирована строка, содержащая символ '<'");
				}
			}
		});
		
		
		grid.resizeColumnWidth("col1", "5px");
	    
	});
}

function refreshLyraDGrid(parentId){
	arrGrids[parentId].refresh();
}

function addRecordLyraDGrid(parentId){
	arrGrids[parentId].collection.add({id: "addRecord_"+GenerateGUID()});
}

function saveLyraDGrid(parentId){
	arrGrids[parentId].save();
}

function revertLyraDGrid(parentId){
	arrGrids[parentId].revert();
}

function clipboardLyraDGrid(parentId){
	var str = "";
	
	var grid = arrGrids[parentId];
	
	for(var col in grid.columns){
		str = str + grid.columns[col].label + "\t";
	}
	
	str = str + "\n";
		
    for(var id in grid.selection){
        if(grid.selection[id]){
        	for(var col in grid.columns){
        		str = str + grid.row(id).data[col] + "\t";
        	}
        	str = str + "\n";
        }
    }
	
	return str;
}

function partialUpdateLyraDGrid(elementId, parentId, partialdata){
	for(var k in partialdata) {
		if(arrGrids[parentId].row(partialdata[k].id).data){
				arrGrids[parentId].collection.emit('update', {target: partialdata[k]});				
		}
	}
	
	if(partialdata[0]){
		var events = null;
		if(partialdata[0]["events"]){
			events = partialdata[0]["events"];
		}
		gwtAfterPartialUpdateLyra(elementId, events);						
	}
}





