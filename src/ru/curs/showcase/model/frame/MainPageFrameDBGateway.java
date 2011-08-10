package ru.curs.showcase.model.frame;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.runtime.*;

/**
 * Шлюз к БД для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
public class MainPageFrameDBGateway extends SPCallHelper implements MainPageFrameGateway {

	private static final int SESSION_CONTEXT_PARAM_INDEX = 2;

	private static final String FRAME_DATA_TAG = "framedata";
	private static final int FRAME_DATA_TAG_INDEX = 3;

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		setProcName(frameSource);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return "{? = call [dbo].[%s](?, ?, ?)}";
		} else {
			// return "{? = call %s(?, ?, ?)}";
			// Это эквивалентно
			return "{call %s(?, ?, ?, ?)}";
		}
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

	@Override
	public String getRawData(final CompositeContext context) {
		try {
			try {
				prepareStatementWithErrorMes();
				if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
					getStatement().setString(SESSION_CONTEXT_PARAM, context.getSession());
				} else {
					getStatement().setString(SESSION_CONTEXT_PARAM_INDEX, context.getSession());
				}
				LOGGER.info("context=" + context.toString());
				if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
					getStatement().registerOutParameter(FRAME_DATA_TAG, java.sql.Types.VARCHAR);
				} else {
					getStatement().registerOutParameter(FRAME_DATA_TAG_INDEX,
							java.sql.Types.VARCHAR);
				}

				getStatement().execute();
				checkErrorCode();
				String result;
				if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
					result = getStatement().getString(FRAME_DATA_TAG);
				} else {
					result = getStatement().getString(FRAME_DATA_TAG_INDEX);
				}
				return result;
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return null;
	}

	@Override
	public void setSourceName(final String aName) {
		setProcName(aName);
	}

}
