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

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String dataPanelId) {
		setProcName(dataPanelId);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call [dbo].[%s](?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		try {
			prepareStatementWithErrorMes();
			getStatement().setString(SESSION_CONTEXT_PARAM, context.getSession());
			getStatement().setString(MAIN_CONTEXT_TAG, context.getMain());
			getStatement().registerOutParameter(DP_TAG, java.sql.Types.SQLXML);
			getStatement().execute();
			checkErrorCode();
			InputStream stream = getStatement().getSQLXML(DP_TAG).getBinaryStream();
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
}
