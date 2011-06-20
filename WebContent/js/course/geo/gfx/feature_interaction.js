dojo.provide("course.geo.gfx.feature_interaction");

(function() {
	var gg = course.geo.gfx;
	gg.onpointeroutDelay = 100;
	gg._pointed = {
		cancelOnpointerout: false,
		onpointeroutTimeout: null,
		control: null
	};
})();