dojo.provide("course.geo.utils.jenks");

dojo.require("course.geo.utils.colorbrewer");

(function(){

var j = course.geo.utils.jenks;

var ascendingSort = function(a, b) {return (a - b);}

j.getBreaks = function(dataList, numClasses) {
	var numElements = dataList.length;
	dataList.sort(ascendingSort);

	var mat1 = [];
	for(var i=0; i<=numElements; i++) {
		var temp = []
		for(var j=0; j<=numClasses; j++) {
			temp.push[0];
		}
		mat1.push(temp);
	}
	
	var mat2 = [];
	for(var i=0; i<=numElements; i++) {
		var temp = []
		for(var j=0; j<=numClasses; j++) {
			temp.push[0];
		}
		mat2.push(temp);
	}
	
	for(var i=1; i<=numClasses; i++) {
		mat1[1][i] = 1;
		mat2[1][i] = 0;
		for(var j=2; j<=numElements; j++) {
			mat2[j][i] = Infinity
		}
	}

	v = 0.0 
	for(var l=2; l<=numElements; l++) {
		var s1 = 0.0;
		var s2 = 0.0;
		var w = 0.0 
		for(var m=1; m<=l; m++) {
			var i3 = l - m + 1;
			var val = parseFloat(dataList[i3-1]);
			s2 += val * val;
			s1 += val;
			w += 1;
			v = s2 - (s1 * s1) / w;
			var i4 = i3 - 1;
			if (i4 != 0) {
				for(var j=2; j<=numClasses; j++) {
					if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
						mat1[l][j] = i3
						mat2[l][j] = v + mat2[i4][j - 1]
					}
				}
			}
		}
		mat1[l][1] = 1
		mat2[l][1] = v
	}
	
	k = numElements;
	var kclass = [];
	for(var j=0; j<=numClasses; j++) {
		kclass.push(0);
	}
	
	kclass[numClasses] = parseFloat(dataList[numElements - 1]);
	kclass[0] = parseFloat(dataList[0])
	var countNum = numClasses;
	
	while (countNum >= 2) {
		var id = parseInt((mat1[k][countNum]) - 2);
		kclass[countNum - 1] = dataList[id];
		k = parseInt((mat1[k][countNum] - 1));
		countNum -= 1;
	}
	
	return kclass;
};


j.generateStyle = function(feature, style, kwArgs) {
	var featureContainer = feature.parent;
	var breaks = featureContainer._breaks;
	if (!breaks || kwArgs.recalculateBreaks) {
		var values = [];
		dojo.forEach(featureContainer.features, function(feature){
			var value = feature.get(kwArgs.attr);
			if (!isNaN(value)) values.push(value);
		});
		if (!values.length) return;
		breaks = j.getBreaks(values, kwArgs.numClasses);
		if (!breaks.length) return;
		// store calculated breaks for the use by other features that are children of the featureContainer
		featureContainer._breaks = breaks;
		kwArgs.recalculateBreaks = false;
	}

	var attrValue = feature.get(kwArgs.attr);
	for (var i=0; i<kwArgs.numClasses; i++) {
		if (i==0) {
			if (breaks[0]<=attrValue && attrValue<=breaks[1]) break;
		}
		else if (breaks[i]<attrValue && attrValue<=breaks[i+1]) break;
	}
	if (i<kwArgs.numClasses) style.fill = course.geo.utils.colorbrewer.schemes["seq"][kwArgs.numClasses][kwArgs.colorSchemeName].colors[i];
}

}());