package ru.curs.showcase.app.api;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Константы, используемые при обмене информацией между клиентом и сервером.
 * 
 * @author den
 * 
 */
public final class ExchangeConstants implements SerializableElement {

	private ExchangeConstants() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Префикс сервлетов, используемых для передачи или получения
	 * пользовательских данных.
	 */
	public static final String SECURED_SERVLET_PREFIX = "secured";

	/**
	 * Название параметра userdata в URL.
	 */
	public static final String URL_PARAM_USERDATA = "userdata";

	/**
	 * Идентификатор userdata по-умолчанию.
	 */
	public static final String SHOWCASE_USER_DATA_DEFAULT = "default";

	/**
	 * Разделитель строк для сообщений, показываемых пользователю (используется
	 * разделитель Windows). System.getProperty("line.separator"); - не работает
	 * в gwt
	 */
	public static final String LINE_SEPARATOR = "\r\n";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6722656736605297948L;

	/**
	 * Префикс для параметров сервлета, содержащих данные файла.
	 */
	public static final String FILE_DATA_PARAM_PREFIX = "@@filedata@@";

	public static final String SESSION_NOT_AUTH_SIGN = "SessionNotAuthenticated";

}
