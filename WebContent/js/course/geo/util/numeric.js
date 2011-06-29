dojo.provide("course.geo.util.numeric");

(function(){

var n = course.geo.util.numeric;

n.composeArray = function(featureContainer, attr, performSort, ascendingSort) {
	var values = [];
	dojo.forEach(featureContainer.features, function(feature){
		var value = feature.get(attr);
		if (!isNaN(value)) values.push(value);
	});
	if (performSort) {
		ascendingSort = ascendingSort ? function(a, b) {return (a - b);} : null;
		values.sort(ascendingSort);
	}
	return values;
};

n.getBreakIndex = function(breaks, numClasses, value) {
	for (var i=0; i<numClasses; i++) {
		if (i==0) {
			if (breaks[0]<=value && value<=breaks[1]) break;
		}
		else if (breaks[i]<value && value<=breaks[i+1]) break;
	}
	// the value doesn't belong to the given breaks
	return (i<numClasses) ? i : -1;
}

}());