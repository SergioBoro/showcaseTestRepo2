package ru.curs.showcase.security.logging;

import java.sql.*;

import ru.curs.showcase.runtime.ConnectionFactory;

/**
 * Шлюз для обработки события с использованием хранимой процедуры.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingDBGateway implements SecurityLoggingGateway {
	private static final int PARAM_SESSIONID = 1;
	private static final int PARAM_USERNAME = 2;
	private static final int PARAM_IP = 3;
	private static final int PARAM_TYPE = 4;
	private String procName;

	public SecurityLoggingDBGateway(final String sProcName) {
		super();
		this.procName = sProcName;
	}

	@Override
	public void doLogging(final Event event) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		try {
			CallableStatement cs = conn.prepareCall("{call " + this.procName + "(?,?,?,?)}");
			cs.setString(PARAM_SESSIONID, event.getSessionid());
			cs.setString(PARAM_USERNAME, event.getUsername());
			cs.setString(PARAM_IP, event.getIp());
			cs.setString(PARAM_TYPE, event.getTypeEvent().name());

			cs.execute();
		} finally {
			ConnectionFactory.getInstance().release(conn);
		}
	}
}
