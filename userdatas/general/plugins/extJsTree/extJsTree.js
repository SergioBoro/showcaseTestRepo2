function createExtJsTree(parentId, pluginParams, data) {
	if (!pluginParams.core.filter) {
		pluginParams.core.filter = {};
	}
	var DataLoader = function(store, curVal, delay) {
		this.timeoutId = false;
		this.curVal = curVal || {},
		this.store = store;
		this.store.proxy.dataLoader = this;
		this.store.proxy.read=this.loadNode;
		this.delay = delay || 900;
	};
	DataLoader.prototype = {			
			load:function(operation, callback, scope) {
				gwtGetDataPlugin({
					id:pluginParams.elementPanelId,
					parentId:parentId,
					params: operation.params,
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
				if (operation.node != store.getRootNode()) {
					var dataNode = operation.node.data;
					operation.params={
						id:dataNode.id,
						name:dataNode.name
					};		
				}
				this.dataLoader.load(operation, callback, scope);
			},
			_doEvent:function(val, isChecked) {
				var rootNode = this.store.getRootNode();
				rootNode.removeAll();
				this.store.load({node: rootNode, params:{
						curValue:val,
						startsWith:isChecked
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
							self._doEvent(val, isChecked);
						},this.delay);
					} else {
						this._doEvent(val, isChecked);
					}
				}
			},
			setCurValue: function(curVal, curIsChecked) {
				this.curVal = {
					val:curVal,
					curIsChecked:curIsChecked
				};
			}
	};
	
    Ext.require([
        'Ext.tree.*',
        'Ext.data.*'
        ]);
    var parentEl = Ext.get(parentId);
	Ext.define('Item', {
			extend: 'Ext.data.Model',
			fields: [
				{ name: 'id', type: 'string' },
				{ name: 'name', type: 'string' }
			]
	});
	var root = !data ? [] : {
			id:'root',
			name:'/',
			expanded:true,
			leaf:false,
			icon:Ext.BLANK_IMAGE_URL,
			children:data
	};	
	var store = Ext.create('Ext.data.TreeStore', {
			model: 'Item',
            root: root,
            proxy: {
				type: 'memory',
				reader: {
					type: 'json'
				}
			}
    });
	var dataLoader = new DataLoader(store, {}, pluginParams.core.filter.delay);	
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