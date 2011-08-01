package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Класс исключений, генерируемых при выполнении XSLT-преобразования
 * XMLUtils.xsltTransform.
 */
public class XSLTTransformException extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1418394624700606049L;

	public XSLTTransformException(final String message, final Throwable cause) {
		super(ExceptionType.SOLUTION, message, cause);
	}
}
