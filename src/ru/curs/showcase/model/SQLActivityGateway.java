package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.util.ConnectionFactory;

/**
 * Класс шлюза-исполнителя вызовов SQL хранимых процедур.
 * 
 * @author den
 * 
 */
public class SQLActivityGateway extends SPCallHelper implements ActivityGateway {

	static final String EXEC_ERROR = "При исполнении хранимой процедуры '%s' произошла ошибка: %s";

	@Override
	public void exec(final CompositeContext context, final ServerActivity activity) {
		setContext(context);

		try {
			setDb(ConnectionFactory.getConnection());
			try {
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
			} finally {
				releaseResources();
			}
		} catch (SQLException e) {
			if (ValidateInDBException.isSolutionDBException(e)) {
				throw new ValidateInDBException(e);
			} else {
				if (!checkProcExists()) {
					throw new SPNotExistsException(getProcName());
				}
				throw new DBQueryException(e, getProcName());
			}
		}
	}

	@Override
	public String getOutSettingsParam() {
		return null;
	}

	@Override
	protected String getSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?)}";
	}

	@Override
	protected String getSettingsSchema() {
		return null;
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return null;
	}

}
