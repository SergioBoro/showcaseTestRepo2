package ru.curs.showcase.app.client.utils;

/**
 * Поддержка работы с несколькими userdata в клиентской части.
 * 
 */
public final class MultiUserData {

	/**
	 * Идентификатор userdata по-умолчанию.
	 */
	private static final String SHOWCASE_USER_DATA_DEFAULT = "default";
	/**
	 * URL_PARAM_USERDATA.
	 */
	private static final String URL_PARAM_USERDATA = "userdata";
	/**
	 * SOLUTIONS.
	 */
	private static final String SOLUTIONS = "solutions";

	private MultiUserData() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает подкорректированный путь с учетом текущей userdata. Например,
	 * "html/welcome.jsp" --> "solutions/default/html/welcome.jsp"
	 * 
	 * @param path
	 *            исходный путь
	 * 
	 * @return - путь, с учетом текущей userdata
	 */
	public static String getPathWithUserData(final String path) {
		String userdataId =
			com.google.gwt.user.client.Window.Location.getParameter(URL_PARAM_USERDATA);
		if ((userdataId == null) || ("".equals(userdataId))) {
			userdataId = SHOWCASE_USER_DATA_DEFAULT;
		}

		return SOLUTIONS + "/" + userdataId + "/" + path;
	}

}
