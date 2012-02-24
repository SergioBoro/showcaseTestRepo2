package ru.curs.showcase.core.sp;

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
public abstract class CompBasedElementSPQuery extends ElementSPQuery {
	/**
	 * Стандартная функция выполнения запроса с проверкой на возврат результата.
	 */
	protected void stdGetResults() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			boolean hasResult = execute();
			if (!hasResult) {
				checkErrorCode();
				throw new DBQueryException(getElementInfo(),
						CompBasedElementSPQuery.NO_RESULTSET_ERROR);
			}
		} else {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				getConn().setAutoCommit(false);
				// TODO проверить как поведет себя вышележащий код при
				// отсутствии датасета
			}
			execute();
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
	protected RecordSetElementRawData stdGetData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		try {
			prepareStdStatement();
			stdGetResults();
			return new RecordSetElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();

		registerOutParameterCursor();
	}

	protected abstract void registerOutParameterCursor() throws SQLException;

}
