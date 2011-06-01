package ru.curs.showcase.exception;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Исключение, возникающее при запросе к БД.
 * 
 * @author den
 * 
 */
public class DBQueryException extends BaseException {

	static final String ERROR_MES_TEXT = "Текст ошибки: ";
	static final String PARAMS_TEXT = "Параметры запроса: ";
	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_HEADER =
		"Произошла ошибка при выполнении хранимой процедуры";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 4849562484767586377L;

	public DBQueryException(final SQLException cause, final String aProcName,
			final CompositeContext aContext) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER, aProcName,
				PARAMS_TEXT, aContext.toString()), cause);
	}

	public DBQueryException(final SQLException cause, final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER,
				aElementInfo.getProcName(), PARAMS_TEXT, aContext.toString()), cause);
	}

	public DBQueryException(final SQLException cause, final String aProcName) {
		super(ExceptionType.SOLUTION, String.format("%s %s.", ERROR_HEADER, aProcName), cause);
	}

	public DBQueryException(final DataPanelElementInfo aElementInfo, final String aErrorText) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER,
				aElementInfo.getProcName(), ERROR_MES_TEXT, aErrorText));
	}

	public DBQueryException(final String procName, final String aErrorText) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER, procName,
				ERROR_MES_TEXT, aErrorText));
	}

}
