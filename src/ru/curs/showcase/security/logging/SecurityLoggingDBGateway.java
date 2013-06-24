package ru.curs.showcase.security.logging;

import java.sql.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.ConnectionFactory;

/**
 * Шлюз для обработки события с использованием хранимой процедуры.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingDBGateway implements SecurityLoggingGateway {
	private static final int PARAM_SESSIO_CONTEXT = 1;
	private static final int PARAM_SESSIONID = 2;
	private static final int PARAM_IP = 3;
	private static final int PARAM_TYPE = 4;
	private final String procName;

	public SecurityLoggingDBGateway(final String sProcName) {
		super();
		this.procName = sProcName;
	}

	@Override
	public void doLogging(final Event event) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		try {
			CallableStatement cs = conn.prepareCall("{call " + this.procName + "(?,?,?,?)}");

			String sessionXml = "";
			CompositeContext context = event.getContext();
			if (context != null) {
				String session = context.getSession();
				if (session != null) {
					sessionXml = session;
				}
			}
			SQLXML sqlxml = conn.createSQLXML();
			sqlxml.setString(sessionXml);
			cs.setSQLXML(PARAM_SESSIO_CONTEXT, sqlxml);
			cs.setString(PARAM_SESSIONID, event.getSessionid());
			cs.setString(PARAM_IP, event.getIp());
			cs.setString(PARAM_TYPE, event.getTypeEvent().name());

			cs.execute();
		} finally {
			ConnectionFactory.getInstance().release(conn);
		}
	}
}
