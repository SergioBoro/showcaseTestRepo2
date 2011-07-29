package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Специальный класс ошибки для передачи наверх контролируемого исключения в
 * обработчике SAX парсера.
 * 
 * @author den
 * 
 */
public final class SAXError extends BaseException {

	public SAXError(final Throwable aCause) {
		super(ExceptionType.APP, aCause);
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5024218668352683986L;

}
