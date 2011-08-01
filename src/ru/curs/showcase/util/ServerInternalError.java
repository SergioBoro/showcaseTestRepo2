package ru.curs.showcase.util;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Класс внутренней ошибки Showcase. Используется для перехвата исключений,
 * которых "не должно быть".
 * 
 * @author den
 * 
 */
public class ServerInternalError extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4193893671079202405L;

	public ServerInternalError(final String aMessage) {
		super(ExceptionType.APP, aMessage);
	}

	public ServerInternalError(final Exception ex) {
		super(ExceptionType.APP, ex);
	}

}
