package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Ошибка при работе с полученным из БД ResultSet. Может быть вызвана потерей
 * соединения с сервером.
 * 
 * @author den
 * 
 */
public class ResultSetHandleException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка при работе с полученными из БД данными";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4089202125257954531L;

	public ResultSetHandleException(final Throwable aCause) {
		super(ExceptionType.SOLUTION, ERROR_MES, aCause);
	}

	public ResultSetHandleException(final String aString,
			final CompositeContext aCompositeContext,
			final DataPanelElementInfo aDataPanelElementInfo) {
		super(ExceptionType.SOLUTION, aString);
		setContext(new DataPanelElementContext(aCompositeContext, aDataPanelElementInfo));
	}

}
