dojo.provide("pizhma.base");

(function() {
	
var pb = pizhma.base,
	circleId = 0,
	map;

pb.load = function(_map) {
	map = _map;
	var ge = map.engine.ge;
	
	var link = ge.createLink('');
	var baseUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/')+1);
	var modelUrl = dojo.moduleUrl("pizhma", "P-18.kmz").uri;
	modelUrl = (function(relativePath) {
		// find the number of occurances of ../
		var depth = relativePath.match(/\.\.\//g);
		depth = depth ? depth.length : 0;
		var _depth = depth;
		var position = baseUrl.length-1; // omit trailing slash
		while(depth) {
			position--;
			if (baseUrl.charAt(position)=="/") depth--;
		}
		return baseUrl.substring(0,position) + "/" +relativePath.substring(3*_depth);
	})(modelUrl);
	
	link.setHref(modelUrl);
	
	var networkLink = ge.createNetworkLink('');
	networkLink.set(link, true, true); // Sets the link, refreshVisibility, and flyToView.
	
	ge.getFeatures().appendChild(networkLink);
};

pb.zoomTo = function(modelPlacemarkId) {
	modelPlacemarkId = "model" + modelPlacemarkId;
    var ge = map.engine.ge;
    var model = getPlacemarkById(modelPlacemarkId);
    if (model) {
            var location = model.getGeometry().getLocation();
                    lon = location.getLongitude(),
                    lat = location.getLatitude();
            map.engine._zoomTo([lon,lat,lon,lat]);
    }
};


var getPlacemarkById=function(id) {
    var ge = map.engine.ge;
    placemarks = ge.getElementsByType("KmlPlacemark");
    var placemark, placemarkId;
    for (var i = 0; i < placemarks.getLength(); ++i) {
            placemark = placemarks.item(i);
            placemarkId = placemark.getId();
            if (placemarkId==id) break;
    }
    if (i< placemarks.getLength()) return placemark;
    return null;
};

pizhma.base.addCircles = function(radius) {
	if (dojo.isObject(radius)) radius = 70000;
	else radius = radius * 1000;
    google.earth.executeBatch(map.engine.ge, function(){
            addCircles(radius);
    });
};

var addCircles = function(radius) {
    var ge = map.engine.ge,
            numCircles = 4;

    circleId++;
    for (var i=1; i<=numCircles; i++) {
            var placemark = getPlacemarkById("circle_"+i+"_"+(circleId-1));
            if (placemark) {
                    ge.getFeatures().removeChild(placemark);
            }

            var model = getPlacemarkById("model"+i),
                    location = model.getGeometry().getLocation(),
                    lon = location.getLongitude(),
                    lat = location.getLatitude();

            placemark = ge.createPlacemark("circle_"+i+"_"+circleId);
            var polygon = ge.createPolygon('');
            placemark.setGeometry(polygon);
            var linearRing = ge.createLinearRing('');
            linearRing.setAltitudeMode(ge.ALTITUDE_CLAMP_TO_GROUND);
            polygon.setOuterBoundary(linearRing);
            createCircle(linearRing,lat,lon,radius);
            //apply color
            placemark.setStyleSelector(ge.createStyle(''));
            placemark.getStyleSelector().getPolyStyle().getColor().set("640000ff");

            ge.getFeatures().appendChild(placemark);
    }
};

var createCircle = function(linearRing,lat,lon,radius) {
    var numPoints = 360,
            d_rad = radius/6378137;
    lat = lat/180.*Math.PI;
    lon = lon/180.*Math.PI;
    for(var i=0; i<=numPoints; i++) {
            radial = i/180.*Math.PI;
            var lat_rad = Math.asin(Math.sin(lat)*Math.cos(d_rad) +
Math.cos(lat)*Math.sin(d_rad)*Math.cos(radial)),
                    dlon_rad = Math.atan2(Math.sin(radial)*Math.sin(d_rad)*Math.cos(lat),
Math.cos(d_rad)-Math.sin(lat)*Math.sin(lat_rad)),
                    lon_rad = fmod((lon+dlon_rad + Math.PI), 2*Math.PI) - Math.PI;
                    lon_deg = lon_rad*180/Math.PI,
                    lat_deg = lat_rad*180/Math.PI;
            linearRing.getCoordinates().pushLatLngAlt(lat_deg, lon_deg, 0);
    }
};

var fmod = function(dividend, divisor) {
    var multiplier = 0;
    while(divisor * multiplier < dividend) {
            ++multiplier;
    }
    --multiplier;
    return dividend - (divisor * multiplier);
};

}());


