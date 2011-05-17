dojo.provide("course.geo.utils");

(function(){
	
var u = course.geo.utils;

var idCounter = 0;

u.getUniqueNumber = function() {
	idCounter += 1;
	return idCounter;
}

u.extendBbox = function(extendWhat, extendWith) {
	// left
	if (extendWith[0] < extendWhat[0]) extendWhat[0] = extendWith[0];
	// bottom
	if (extendWith[1] < extendWhat[1]) extendWhat[1] = extendWith[1];
	if (extendWith.length == 4) {
		// right
		if (extendWith[2] > extendWhat[2]) extendWhat[2] = extendWith[2];
		// top
		if (extendWith[3] > extendWhat[3]) extendWhat[3] = extendWith[3];
	}
	else { // extendWith.length == 4 (e.g. a point)
		if (extendWith[0] > extendWhat[2]) extendWhat[2] = extendWith[0];
		// top
		if (extendWith[1] > extendWhat[3]) extendWhat[3] = extendWith[1];
	}
	return extendWhat;
}

}());