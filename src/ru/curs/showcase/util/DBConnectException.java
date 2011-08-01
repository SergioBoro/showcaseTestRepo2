package ru.curs.showcase.util;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Ошибка соединения с БД.
 * 
 * @author den
 * 
 */
public class DBConnectException extends BaseException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при соединении с БД";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7586686198028153113L;

	public DBConnectException(final Throwable cause) {
		super(ExceptionType.SOLUTION, ERROR_MES, cause);
	}

}
