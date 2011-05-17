package ru.curs.showcase.exception;

/**
 * Класс исключений, генерируемых при выполнении XSD-проверки
 * XMLUtils.xsdValidate.
 */
public class XSDValidateException extends AbstractShowcaseException {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4848505926999363486L;

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Документ не соответствует схеме";

	public XSDValidateException(final Throwable cause) {
		super(ERROR_MES, cause);
	}

	public XSDValidateException() {
		super(ERROR_MES);
	}

	public XSDValidateException(final String message, final Throwable cause) {
		super(ERROR_MES + ": " + message, cause);
	}

	public XSDValidateException(final String message) {
		super(message);
	}
}
