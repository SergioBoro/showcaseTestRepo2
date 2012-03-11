function createFlashD(parentId, data) {	
    var obj = document.createElement('object'); 
    obj.data = "solutions/default/plugins/flashD/Components/ComponentD.swf"; 
    obj.align="top";
    obj.id="ComponentD";
    obj.title="";
    obj.width="100%";
    obj.height="100%";
    obj.style="position:relative;";
    var param = document.createElement('param');
    param.name = "FlashVars";
    param.value = "data=" + (JSON.stringify(data));
    var parent = document.getElementById(parentId); 
    obj.appendChild(param);
    parent.appendChild(obj); 
}