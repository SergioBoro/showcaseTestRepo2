package ru.curs.showcase.util.xml;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.util.BaseException;

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

	public XSLTTransformException(final String message, final Exception cause,
			final DataPanelElementContext aContext) {
		super(ExceptionType.SOLUTION, message, cause);
		setContext(aContext);
	}
}
