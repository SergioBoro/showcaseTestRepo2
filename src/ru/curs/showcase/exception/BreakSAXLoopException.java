package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Исключение для прерывания работы обработчика SAX.
 * 
 * @author den
 * 
 */
public class BreakSAXLoopException extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5726882721367127175L;

	public BreakSAXLoopException() {
		super(ExceptionType.CONTROL);
	}

}
