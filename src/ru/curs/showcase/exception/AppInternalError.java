package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Класс внутренней ошибки Showcase. Используется для перехвата исключений,
 * которых "не должно быть".
 * 
 * @author den
 * 
 */
public class AppInternalError extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4193893671079202405L;

	public AppInternalError(final String aMessage) {
		super(ExceptionType.APP, aMessage);
	}

	public AppInternalError(final Exception ex) {
		super(ExceptionType.APP, ex);
	}

}
