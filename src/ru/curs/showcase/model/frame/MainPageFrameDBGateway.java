package ru.curs.showcase.model.frame;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;

/**
 * Шлюз к БД для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
public class MainPageFrameDBGateway extends SPCallHelper implements MainPageFrameGateway {

	protected static final String FRAME_DATA_TAG = "framedata";

	@Override
	public String get(final CompositeContext context, final String frameSource) {
		try {
			try {
				setProcName(frameSource);

				prepareStdStatementWithErrorMes();
				getStatement().setString(SESSION_CONTEXT_PARAM, "");
				if (context != null) {
					if (context.getSession() != null) {
						getStatement().setString(SESSION_CONTEXT_PARAM, context.getSession());
					}
				}
				LOGGER.info("context=" + context.toString());
				getStatement().registerOutParameter(FRAME_DATA_TAG, java.sql.Types.VARCHAR);

				getStatement().execute();
				checkErrorCode();
				String result = getStatement().getString(FRAME_DATA_TAG);
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
	protected String getSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

}
