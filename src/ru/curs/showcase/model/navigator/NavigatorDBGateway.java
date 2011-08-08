package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.runtime.*;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorDBGateway extends SPCallHelper implements NavigatorGateway {
	private static final int SESSION_CONTEXT_PARAM_INDEX = 1;
	private static final int NAVIGATOR_TAG_INDEX = 2;

	@Override
	public InputStream getRawData(final CompositeContext context, final String sourceName) {
		setProcName(sourceName);
		try {
			prepareSQL();
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				getStatement().setString(SESSION_CONTEXT_PARAM, context.getSession());
				getStatement().registerOutParameter(NAVIGATOR_TAG, java.sql.Types.SQLXML);
			} else {
				getStatement().setString(SESSION_CONTEXT_PARAM_INDEX, context.getSession());
				getStatement().registerOutParameter(NAVIGATOR_TAG_INDEX, java.sql.Types.SQLXML);
			}
			getStatement().execute();

			InputStream stream;
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				stream = getStatement().getSQLXML(NAVIGATOR_TAG).getBinaryStream();
			} else {
				stream = getStatement().getSQLXML(NAVIGATOR_TAG_INDEX).getBinaryStream();
			}
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return "{call [dbo].[%s](?, ?)}";
		} else {
			return "{call %s(?, ?)}";
		}
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}
}
