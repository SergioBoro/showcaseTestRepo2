package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при запросе к БД.
 * 
 * @author den
 * 
 */
public class DBQueryException extends BaseException {

	private static final String ERROR_MES_TEXT = "Подробности";

	private static final String ERROR_HEADER =
		"Произошла ошибка при выполнении хранимой процедуры";

	private static final long serialVersionUID = 4849562484767586377L;

	public DBQueryException(final SQLException cause, final String aProcName,
			final DataPanelElementContext aContext,
			final Class<? extends SPQuery> gatewayClass) {
		super(ExceptionType.SOLUTION, String.format("Процесс: %s. %s %s.",
				ReflectionUtils.getProcessDescForClass(gatewayClass), ERROR_HEADER, aProcName),
				cause);
		setContext(aContext);
	}

	public DBQueryException(final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext, final String aErrorText) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER,
				aElementInfo.getProcName(), ERROR_MES_TEXT, aErrorText));
		setContext(new DataPanelElementContext(aContext, aElementInfo));
	}

}
