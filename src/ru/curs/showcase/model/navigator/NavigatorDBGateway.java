package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorDBGateway extends SPCallHelper implements NavigatorGateway {

	@Override
	public InputStream getRawData(final CompositeContext context, final String sourceName) {
		setProcName(sourceName);
		try {
			prepareSQL();
			getStatement().setString(SESSION_CONTEXT_PARAM, context.getSession());
			getStatement().registerOutParameter(NAVIGATOR_TAG, java.sql.Types.SQLXML);
			getStatement().execute();
			InputStream stream = getStatement().getSQLXML(NAVIGATOR_TAG).getBinaryStream();
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "exec [dbo].[%s] ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}
}
