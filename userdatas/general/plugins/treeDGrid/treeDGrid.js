var arrGrids = {};


function createTreeDGrid(elementId, parentId, metadata) {
	require(["dojo/on", "dgrid/tree", "dgrid/OnDemandGrid", "dgrid/extensions/ColumnResizer","dgrid/Selection", "dgrid/CellSelection", "dgrid/Keyboard", "dojo/_base/declare", "JsonRest", "dojo/store/Cache", "dojo/store/Memory", "dojo/aspect", "dojo/domReady!"], 
	function(on, tree, Grid, ColumnResizer, Selection, CellSelection, Keyboard, declare, JsonRest, Cache, Memory, aspect){
		
		var firstLoading = true;
		
		var store = Cache(JsonRest({
			target:"secured/JSGridService",
			
			idProperty: "id",
			
			query: function(query, options){
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

	 	    	var httpParams = gwtGetHttpParams(elementId, options.start, options.count, sortColId, sortColDir, query.parent);
	 	    	httpParams = eval('('+httpParams+')');	 	 
	 	    	
			    var params = {};
			    params[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
			    params[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];			    

				var results = JsonRest.prototype.query.call(this, query, options, params);
				results.then(function(results){
					if(results[0]){
						gwtAfterLoadData(elementId, results[0]["liveGridExtradata"]);						
					}
				});				
				return results;
			},
		
		}), Memory());
		store.getChildren = function(parent, options){
	    	return store.query({parent: parent.id}, options);
		};
		
		
		var columns = []; var count=0;
		for(var k in metadata["columns"]) {
			var column = null;
			if(count == 0){
				column = tree({});	
			}
			else
			{
				column = {};	
			}	
			count++;		

			column["id"]    = metadata["columns"][k]["id"];
			column["field"] = metadata["columns"][k]["id"];			
			column["label"] = metadata["columns"][k]["caption"];
			column["sortable"] = "true";
			
			if(metadata["columns"][k]["valueType"] == "DOWNLOAD"){
				column["renderCell"] = function actionRenderCell(object, value, node, options) {
					var div = document.createElement("div");
//					div.className = "renderedCell";
					div.innerHTML = "<tbody><tr><td style=\"font-size: 1em;\">"+value+"</td><td  align=\"center\" style=\"vertical-align: middle;\"><button onclick=\"gwtProcessFileDownload('"+elementId+"', '"+object.id+"', '"+this.id+"')\"><img src="+metadata["columns"][k]["urlImageFileDownload"]+" title=\"Загрузить файл с сервера\"  style=\"vertical-align: middle; align: right; width: 16px; height: 16px;  \"   ></button></p></td></tr></tbody>";					
					return div;
		        };
			}else{
				column["formatter"] = function columnFormatter(item){
					return item;
				};
			}
			
			columns.push(column);
		}
		
		
		var declareGrid;
		var selectionMode;
		if(metadata["common"]["selectionModel"] == "RECORDS"){
			selectionMode = "extended";			
			declareGrid = declare([Grid, ColumnResizer, Keyboard, Selection]);
		}else{
			declareGrid = declare([Grid, ColumnResizer, Keyboard, CellSelection]);
			selectionMode = "single";			
		}
		
		
	    var	grid = new declareGrid({
				store: store,
				getBeforePut: false,
				minRowsPerPage: parseInt(metadata["common"]["limit"]),
				selectionMode: selectionMode,
				loadingMessage: "Загрузка...",
				noDataMessage: "Таблица пуста",
				pagingDelay: 50,
				deselectOnRefresh: false,				
				keepScrollPosition: true,
				columns: columns
		}, parentId);
	    arrGrids[parentId] = grid;
		grid.on(".dgrid-row:click,", function(event){
			if(event.toElement.nodeName.toUpperCase() == "DIV"){
				return;
			}
			gwtAfterClick(elementId, grid.row(event).id, grid.column(event).label, getSelection());
		});
		grid.on(".dgrid-row:dblclick", function(event){
			if(event.toElement.nodeName.toUpperCase() == "DIV"){
				return;
			}
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
		
		aspect.around(grid, 'renderRow', function(origMethod) {
			return function(object, options) {
				var html = origMethod.apply(this, arguments);
				if (object.rowstyle != null) {
					html.className = object.rowstyle;
				}
				return html;
			};
		});	
		
		
	});
}

function refreshTreeDGrid(parentId){
	arrGrids[parentId].refresh();
}

