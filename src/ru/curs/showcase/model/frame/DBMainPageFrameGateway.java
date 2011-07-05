package ru.curs.showcase.model.frame;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.DBQueryException;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.util.ConnectionFactory;

/**
 * Шлюз к БД для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
public class DBMainPageFrameGateway extends SPCallHelper implements MainPageFrameGateway {

	protected static final String FRAME_DATA_TAG = "framedata";
	protected static final String EXEC_ERROR =
		"этап - получение кода фрейма, код - %d, текст - %s";

	@Override
	public String get(final CompositeContext context, final String frameSource) {
		try {
			try {
				setDb(ConnectionFactory.getConnection());
				setProcName(frameSource);
				String sql = String.format(getSqlTemplate(), getProcName());
				setCs(getDb().prepareCall(sql));
				getCs().registerOutParameter(1, java.sql.Types.INTEGER);

				getCs().setString(SESSION_CONTEXT_PARAM, "");
				if (context != null) {
					if (context.getSession() != null) {
						getCs().setString(SESSION_CONTEXT_PARAM, context.getSession());
					}
				}
				LOGGER.info("context=" + context.toString());
				getCs().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getCs().registerOutParameter(FRAME_DATA_TAG, java.sql.Types.VARCHAR);
				getCs().execute();
				int errorCode = getCs().getInt(1);
				if (errorCode != 0) {
					String errorFromDB = getCs().getString(ERROR_MES_COL);
					throw new DBQueryException(frameSource, String.format(EXEC_ERROR, errorCode,
							errorFromDB));
				}
				String result = getCs().getString(FRAME_DATA_TAG);
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
	public String getOutSettingsParam() {
		return null;
	}

	@Override
	protected String getSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?)}";
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
