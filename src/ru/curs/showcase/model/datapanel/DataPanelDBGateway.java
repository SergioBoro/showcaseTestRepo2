package ru.curs.showcase.model.datapanel;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.util.DataFile;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class DataPanelDBGateway extends SPCallHelper implements DataPanelGateway {

	private static final int MAIN_CONTEXT_INDEX = 2;
	private static final int SESSION_CONTEXT_INDEX = 3;
	private static final int DP_INDEX = 4;
	private static final int ERROR_MES_INDEX = 5;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String dataPanelId) {
		setProcName(dataPanelId);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		try {
			prepareStatementWithErrorMes();
			setSQLXMLParamByString(getSessionContextIndex(getTemplateIndex()),
					context.getSession());
			getStatement().setString(getMainContextIndex(getTemplateIndex()), context.getMain());
			getStatement().registerOutParameter(DP_INDEX, java.sql.Types.SQLXML);
			execute();
			checkErrorCode();
			InputStream stream = getStatement().getSQLXML(DP_INDEX).getBinaryStream();
			return new DataFile<InputStream>(stream, getProcName());
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public void setSourceName(final String aName) {
		setProcName(aName);
	}

	@Override
	protected int getMainContextIndex(final int index) {
		return MAIN_CONTEXT_INDEX;
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
