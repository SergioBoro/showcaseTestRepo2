package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.ServerActivity;
import ru.curs.showcase.exception.DBQueryException;
import ru.curs.showcase.util.ConnectionFactory;

/**
 * Класс шлюза-исполнителя вызовов SQL хранимых процедур.
 * 
 * @author den
 * 
 */
public class SQLActivityGateway extends SPCallHelper implements ActivityGateway {

	protected static final String EXEC_ERROR = "код ошибки %d, текст - %s";

	@Override
	public void exec(final ServerActivity activity) {
		setContext(activity.getContext());
		try {
			try {
				setDb(ConnectionFactory.getConnection());

				setProcName(activity.getName());
				String sql = String.format(getSqlTemplate(), getProcName());
				setCs(getDb().prepareCall(sql));
				getCs().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralParameters();
				getCs().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getCs().execute();
				int errorCode = getCs().getInt(1);
				if (errorCode != 0) {
					throw new DBQueryException(activity.getName(), String.format(EXEC_ERROR,
							errorCode, getCs().getString(ERROR_MES_COL)));
				}
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
	}

	@Override
	public String getOutSettingsParam() {
		return null;
	}

	@Override
	protected String getSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?)}";
	}

	@Override
	protected String getSettingsSchema() {
		return null;
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

}
