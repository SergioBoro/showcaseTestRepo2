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

	private static final int SESSION_CONTEXT_INDEX = 2;
	private static final int FRAME_DATA_INDEX = 3;
	private static final int ERROR_MES_INDEX = 4;

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		setProcName(frameSource);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?, ?, ?)}";
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
				setSQLXMLParamByString(getSessionContextIndex(getTemplateIndex()),
						context.getSession());
				LOGGER.info("context=" + context.toString());
				getStatement().registerOutParameter(FRAME_DATA_INDEX, java.sql.Types.VARCHAR);

				execute();
				checkErrorCode();

				String result = getStatement().getString(FRAME_DATA_INDEX);
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

	@Override
	protected int getSessionContextIndex(final int index) {
		return SESSION_CONTEXT_INDEX;
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

}
