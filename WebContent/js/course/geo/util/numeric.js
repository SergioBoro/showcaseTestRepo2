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
}

}());