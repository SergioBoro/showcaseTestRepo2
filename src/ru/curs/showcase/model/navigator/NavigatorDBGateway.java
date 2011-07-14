package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;
import ru.curs.showcase.util.*;

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
	public InputStream getData(final CompositeContext context) {
		setConn(ConnectionFactory.getConnection());
		setProcName(AppProps.getRequiredValueByName(NAVIGATOR_PROCNAME_PARAM));
		try {
			String sql = String.format(getSqlTemplate(), getProcName());
			CallableStatement cs = getConn().prepareCall(sql);
			cs.setString(SESSION_CONTEXT_PARAM, context.getSession());
			cs.registerOutParameter(NAVIGATOR_TAG, java.sql.Types.SQLXML);
			cs.execute();
			InputStream stream = cs.getSQLXML(NAVIGATOR_TAG).getBinaryStream();
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	protected String getSqlTemplate() {
		return "exec [dbo].[%s] ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}
}
