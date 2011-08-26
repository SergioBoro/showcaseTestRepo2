package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;

/**
 * Вспомогательный класс для получения данных элементов инф. панели, основанных
 * на компонентах.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementSPCallHelper extends ElementSPCallHelper {
	/**
	 * Стандартная функция выполнения запроса с проверкой на возврат результата.
	 */
	protected void stdGetResults() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			boolean hasResult = getStatement().execute();
			if (!hasResult) {
				throw new DBQueryException(getElementInfo(), getContext(),
						CompBasedElementSPCallHelper.NO_RESULTSET_ERROR);
			}
		} else {
			getConn().setAutoCommit(false);
			getStatement().execute();
		}
	}

	public static final String NO_RESULTSET_ERROR = "хранимая процедура не возвратила данные";

	/**
	 * Стандартный метод возврата данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - инф. об элементе.
	 */
	protected ElementRawData stdGetData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		try {
			prepareStdStatement();
			stdGetResults();
			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	protected int getAdjustParamIndexAccordingToSQLServerType(final int index) {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return index;
		} else {
			return index + 1;
		}
	}

}
