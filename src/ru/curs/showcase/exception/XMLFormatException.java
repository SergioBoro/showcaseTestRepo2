package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Ошибка получения XML документа из XML поля.
 * 
 * @author den
 * 
 */
public class XMLFormatException extends BaseException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при разборе XML данных";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7758790066616498408L;

	public XMLFormatException(final String fileName, final Throwable cause) {
		super(ExceptionType.SOLUTION, ERROR_MES + ": " + fileName, cause);
	}

	public XMLFormatException(final String fileName) {
		super(ExceptionType.SOLUTION, ERROR_MES + ": " + fileName);
	}
}
