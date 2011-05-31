package ru.curs.showcase.app.api;

import com.google.gwt.regexp.shared.*;

/**
 * Тип браузера.
 * 
 * @author den
 * 
 */
public enum BrowserType implements SerializableElement {
	/**
	 * Microsoft Internet Explorer.
	 */
	IE("Microsoft Internet Explorer"),
	/**
	 * Google Chrome.
	 */
	CHROME("Google Chrome"),
	/**
	 * Mozilla Firefox.
	 */
	FIREFOX("Mozilla Firefox"),
	/**
	 * Opera.
	 */
	OPERA("Opera"),
	/**
	 * Apple Safari.
	 */
	SAFARI("Apple Safari");

	static final String VERSION_NOT_DEFINED = "не определена";
	/**
	 * Имя браузера.
	 */
	private String name;

	BrowserType(final String aName) {
		name = aName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	/**
	 * Определяет тип браузера по UserAgent. UserAgent может быть получен либо в
	 * Java сервлете: req.getHeader("User-Agent"), либо через JSNI:
	 * navigator.userAgent.toLowerCase().
	 * 
	 * @param userAgent
	 *            - userAgent.
	 * @return - тип браузера.
	 */
	public static BrowserType detect(final String userAgent) {
		if (findCI(userAgent, "gecko") && findCI(userAgent, "firefox")) {
			return FIREFOX;
		}
		if (findCI(userAgent, "applewebkit") && findCI(userAgent, "chrome")) {
			return CHROME;
		}
		if (findCI(userAgent, "applewebkit") && findCI(userAgent, "safari")) {
			return SAFARI;
		}
		if (findCI(userAgent, "opera")) {
			return OPERA;
		}
		if (findCI(userAgent, "msie")) {
			return IE;
		}
		return null;
	}

	/**
	 * Определяет сообщаемую браузером версию по строке userAgent.
	 * 
	 * @param userAgent
	 *            - userAgent.
	 * @return - версия.
	 */
	public static String detectVersion(final String userAgent) {
		BrowserType bt = detect(userAgent);
		switch (bt) {
		case FIREFOX: // firefox/4.0.1
			return findVersion(userAgent, "firefox\\/([0-9\\.]*)");
		case CHROME: // chrome/11.0.696.71
			return findVersion(userAgent, "chrome\\/([0-9\\.]*)");
		case IE: // msie 9.0
			return findVersion(userAgent, "msie\\s([0-9\\.]*)");
		case OPERA: // version/11.11
		case SAFARI:
			return findVersion(userAgent, "version\\/([0-9\\.]*)");
		default:
			return VERSION_NOT_DEFINED;
		}

	}

	private static boolean findCI(final String source, final String regex) {
		RegExp pattern = RegExp.compile(".*" + regex + ".*", "i");
		return pattern.test(source);
	}

	private static String findVersion(final String source, final String regex) {
		RegExp pattern = RegExp.compile(regex, "i");
		MatchResult res = pattern.exec(source);
		if (res.getGroupCount() == 2) {
			return res.getGroup(1);
		}

		return VERSION_NOT_DEFINED;
	}
}
