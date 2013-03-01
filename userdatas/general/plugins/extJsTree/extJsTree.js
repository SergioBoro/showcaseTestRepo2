function createExtJsTree(parentId, pluginParams, data) {
	if (!pluginParams.core.filter) {
		pluginParams.core.filter = {};
	}
	var DataLoader = function(store, curVal, delay, generalFilters) {
		this.timeoutId = false;
		this.setCurValue(),
		this.store = store;
		this.store.proxy.ExtJsTree = {
			self: this
		};
		this.store.proxy.read=this.loadNode;
		this.delay = delay || 900;
		this.generalFilters = generalFilters || {};
	};
	DataLoader.prototype = {			
			load:function(operation, callback, scope) {
				gwtGetDataPlugin({
					id:pluginParams.elementPanelId,
					parentId:parentId,
					params: Ext.apply(operation.params, {generalFilters:this.generalFilters}),
					callbackFn: function(data) {
						if (data) {
							if (!Ext.isArray(data)) {
								data = [data];
							}
							operation.resultSet = store.proxy.getReader().read(data);	
						}							
						operation.setCompleted();
						operation.setSuccessful();
						Ext.callback(callback, scope, [operation]);
					}
				});
			},
			loadNode:function(operation, callback, scope) {
				var dataLoader = this.ExtJsTree.self;
				if (operation.node != store.getRootNode()) {
					var dataNode = operation.node.data;
					operation.params={
						id:dataNode.id,
						name:dataNode.name,
						curValue:dataLoader.curVal.val,
						startsWith:dataLoader.curIsChecked
					};		
				}
				dataLoader.load(operation, callback, scope);
			},
			_doEvent:function() {
				var rootNode = this.store.getRootNode();
				rootNode.removeAll();
				this.store.load({node: rootNode, params:{
						curValue:this.curVal.val,
						startsWith:this.curIsChecked
					}
				});
			},
			doFilter: function(val, isChecked, isNoDelay) {				
				if (this.timeoutId || isNoDelay) {
					clearTimeout(this.timeoutId);
					this.timeoutId = false;
				}
				if (this.curVal.val != val || this.curVal.curIsChecked !=isChecked) {
					if (!isNoDelay) {
						var self = this;
						this.timeoutId = setTimeout(function() {
							self.timeoutId = false;
							self.setCurValue(val,isChecked);
							self._doEvent();
						},this.delay);
					} else {
						this._doEvent();
					}
				}
			},
			setCurValue: function(curVal, curIsChecked) {
				this.curVal = {
					val:curVal || '',
					curIsChecked:curIsChecked || false
				};
			}
	};
	function applyFieldsToModel(fields, addFields) {
		var result = addFields || [];
		for (i = 0; i < fields.length; i++) {
			var isContains = false;
            for (j = 0; j < addFields.length; j++) {
				if (fields[i].name==addFields[j].name) {
					isContains = true;
					break;
				}
			}
			if (!isContains) {
				result.push(fields[i]);
			}
        }
		return result;
	}
	////////////////////////////////////////////////
    Ext.require([
        'Ext.tree.*',
        'Ext.data.*'
        ]);
    var parentEl = Ext.get(parentId);
	var modelOptions = Ext.apply((pluginParams.dataModel || {}), {
		extend: 'Ext.data.Model'
	}, {});
	modelOptions.fields = applyFieldsToModel([
		{name:'id',type:'string'},
		{name:'name',type:'string'}
	], modelOptions.fields||[]);
	Ext.define('ExtJsTree.DataModel', modelOptions);	
	var root = !data ? [] : {
		id:'root',
		name:'/',
		expanded:true,
		leaf:false,
		icon:Ext.BLANK_IMAGE_URL,
		children:data
	};	
	var store = Ext.create('Ext.data.TreeStore', {
		model: 'ExtJsTree.DataModel',
        root: root,
        proxy: {
		type: 'memory',
			reader: {
				type: 'json'
			}
		}
    });
	var dataLoader = new DataLoader(store, {}, pluginParams.core.filter.delay, pluginParams.generalFilters);	
    Ext.onReady(function() { 
		//добавление фильтра поиска
		var dh = Ext.DomHelper;
		var inputId = Ext.id();
		var checkboxId = Ext.id();
		var filterEl = new Ext.dom.Element(dh.createDom({
			tag:'div',
			style:'text-align:center;',
			children: [{
				tag:'input',
				id:inputId,
				type:'text',
				autocomplete:'off',
				style:'width:'+(parentEl.getWidth()-5)+'px;'
			},{
				tag:'div',
				style:'text-align:left;',
				children: [{
					tag:'input',
					id:checkboxId,
					type:'checkbox',
					value:true
				},{
					tag:'label',
					'for':checkboxId,
					html:'Начинается с'
				}]
			}]
		}));		
		parentEl.appendChild(filterEl);				
		
		var inputEl = Ext.get(inputId);		
		var checkBoxEl = Ext.get(checkboxId);		
		if (pluginParams.core.filter.startsWith) {
			checkBoxEl.set({checked:'checked'});
		}		
		dataLoader.setCurValue(inputEl.getValue(), checkBoxEl.dom.checked);
		checkBoxEl.on('click', function(event, htmlEl, o) {
			dataLoader.doFilter(inputEl.getValue(), htmlEl.checked, false);
		});
		inputEl.on('keyup', function(event, htmlEl, o) {
			dataLoader.doFilter(htmlEl.value, checkBoxEl.dom.checked);
		});
		
		//Построение элемента Ext.tree.Panel								
		var option = {
			store: store,
			columns: [
				{xtype: 'treecolumn', header: 'Название', dataIndex: 'name', flex: 1}
			],
			rootVisible:false,
			hideHeaders:true,
            useArrows: true,
            frame: true,
            title: 'Check Tree',
            renderTo: parentEl,
            width: parentEl.getWidth(),
            height: parentEl.getHeight()-filterEl.getHeight()-2
		};
        option = Ext.apply(option, pluginParams.treePanel);
        var tree = Ext.create('Ext.tree.Panel', option);
				
		if (Ext.isFunction(pluginParams.onDrawPluginComplete)) {
			pluginParams.onDrawPluginComplete(tree);
		};		
    });
};