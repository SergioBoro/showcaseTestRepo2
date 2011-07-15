package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.util.AppProps;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorDBGateway extends SPCallHelper implements NavigatorGateway {
	static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";
	static final String DEF_USERNAME_PARAM = "main.def.username";

	@Override
	public InputStream getRawData(final CompositeContext context) {
		setProcName(AppProps.getRequiredValueByName(NAVIGATOR_PROCNAME_PARAM));
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
