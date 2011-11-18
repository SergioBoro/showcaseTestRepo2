package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Ошибка при чтении тестового файла.
 * 
 * @author den
 * 
 */
public final class TestFileExchangeException extends BaseException {

	private static final String ERROR_MES = "Ошибка при чтении тестового файла: ";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6810662604700277735L;

	public TestFileExchangeException(final String error, final Throwable aCause) {
		super(ExceptionType.SOLUTION, ERROR_MES + error, aCause);
	}

}
