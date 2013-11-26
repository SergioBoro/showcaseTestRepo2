var arrGrids = {};


function createPageDGrid(elementId, parentId, metadata) {
	require(["dojo/store/util/QueryResults", "dojo/on", "dgrid/Grid", "dgrid/extensions/Pagination", "dgrid/extensions/ColumnResizer","dgrid/Selection", "dgrid/CellSelection", "dgrid/Keyboard", "dojo/_base/declare", "JsonRest", "dojo/store/Cache", "dojo/store/Memory", "dojo/aspect", "dojo/domReady!"], 
	function(QueryResults, on, Grid, Pagination, ColumnResizer, Selection, CellSelection, Keyboard, declare, JsonRest, Cache, Memory, aspect){
		
		var firstLoading = true;
		
		var store = Cache(JsonRest({
			target:"secured/JSGridService",
			
			idProperty: "id",
			
			query: function(query, options){
				
				var results = null;
				
				if(firstLoading){
                    results = QueryResults(metadata["data"]["rows"]);
                    results.total = parseInt(metadata["data"]["total"]);
                    
					gwtAfterLoadData(elementId, "");
				}else{
					var sortColId  = null;
					var sortColDir = null;
					if(options && options.sort){
						for(var i = 0; i<options.sort.length; i++){
							var sort = options.sort[i];
							sortColId = grid.columns[sort.attribute].label;
							if(sort.descending){
								sortColDir = "DESC";
							}
							else{
								sortColDir = "ASC";
							}
							break;
						}
					}
					
		 	    	var httpParams = gwtGetHttpParams(elementId, options.start, options.count, sortColId, sortColDir);
		 	    	httpParams = eval('('+httpParams+')');	 	 
		 	    	
				    var params = {};
				    params[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
				    params[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];			    

					results = JsonRest.prototype.query.call(this, query, options, params);
					results.then(function(results){
						if(results[0]){
							gwtAfterLoadData(elementId, results[0]["liveGridExtradata"]);						
						}
					});				
				}
				
				return results;
			}
		}), Memory());
		

		var columns = [];
		for(var k in metadata["columns"]) {
			var column = {};

			column["id"]    = metadata["columns"][k]["id"];
			column["field"] = metadata["columns"][k]["id"];			
			column["label"] = metadata["columns"][k]["caption"];
			column["sortable"]  = "true";
			column["valueType"] = metadata["columns"][k]["valueType"];
			
			column["renderCell"] = function actionRenderCell(object, value, node, options) {
				var div = document.createElement("div");
				if(object.rowstyle && (object.rowstyle != "")){
					div.className = object.rowstyle;						
				}
				if(this["valueType"] == "DOWNLOAD"){
					div.innerHTML = "<tbody><tr><td style=\"font-size: 1em;\">"+value+"</td><td  align=\"center\" style=\"vertical-align: middle;\"><button onclick=\"gwtProcessFileDownload('"+elementId+"', '"+object.id+"', '"+this.id+"')\"><img src="+metadata["columns"][k]["urlImageFileDownload"]+" title=\"Загрузить файл с сервера\"  style=\"vertical-align: middle; align: right; width: 16px; height: 16px;  \"   ></button></p></td></tr></tbody>";
				}else{
					div.innerHTML = value;
				}
				return div;
	        };
			
			columns.push(column);
		}
		
		
		var declareGrid;
		var selectionMode;
		if(metadata["common"]["selectionModel"] == "RECORDS"){
			selectionMode = "extended";			
			declareGrid = declare([Grid, Pagination, ColumnResizer, Keyboard, Selection]);
		}else{
			declareGrid = declare([Grid, Pagination, ColumnResizer, Keyboard, CellSelection]);
			selectionMode = "single";			
		}
		
		var isVisiblePager = false;
		if(metadata["common"]["isVisiblePager"]){
			isVisiblePager = true;	
		}
		var isVisibleColumnsHeader = false;
		if(metadata["common"]["isVisibleColumnsHeader"]){
			isVisibleColumnsHeader = true;	
		}
		
	    var	grid = new declareGrid({
				store: store,
				getBeforePut: false,
				
				showFooter: isVisiblePager,
				pagingLinks: 2,
				pagingTextBox: true,
	            firstLastArrows: true,
				pageSizeOptions: [25, 50, 75, 100],
				rowsPerPage: parseInt(metadata["common"]["limit"]),

				showHeader: isVisibleColumnsHeader,
				selectionMode: selectionMode,
				loadingMessage: metadata["common"]["loadingMessage"],
//				noDataMessage: "Таблица пуста",
				pagingDelay: 50,
				deselectOnRefresh: false,				
				keepScrollPosition: true,
				columns: columns
		}, parentId);
	    arrGrids[parentId] = grid;
		grid.on(".dgrid-row:click,", function(event){
			gwtAfterClick(elementId, grid.row(event).id, grid.column(event).label, getSelection());
		});
		grid.on(".dgrid-row:dblclick", function(event){
			gwtAfterDoubleClick(elementId, grid.row(event).id, grid.column(event).label, getSelection());
		});
		function getSelection()
		{
		    var selection = "";
		    var i = 0;
	        for(var id in grid.selection){
	            if(grid.selection[id]){
	            	if(i > 0){
		            	selection = selection+",";	
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
		
		for(var k in metadata["columns"]) {
			grid.styleColumn(metadata["columns"][k]["id"], metadata["columns"][k]["style"]);
		}
		
	});
}

function refreshPageDGrid(parentId){
	arrGrids[parentId].refresh();
}

function clipboardPageDGrid(parentId){
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
