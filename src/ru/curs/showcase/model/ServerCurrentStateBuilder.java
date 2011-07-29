package ru.curs.showcase.model;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.ServerCurrentState;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.util.*;

/**
 * Построитель ServerCurrentState.
 * 
 * @author den
 * 
 */
public final class ServerCurrentStateBuilder {

	static final String BUILD_FILE = "build";
	static final String VERSION_FILE = "version";

	private ServerCurrentStateBuilder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Построить объект ServerCurrentState.
	 * 
	 * @return ServerCurrentState.
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @throws SQLException
	 * @throws IOException
	 */
	public static ServerCurrentState build(final String sessionId) throws SQLException,
			IOException {
		ServerCurrentState state = new ServerCurrentState();
		state.setServerTime(TextUtils.getCurrentLocalDate());
		state.setAppVersion(getAppVersion());
		state.setServletContainerVersion(AppInfoSingleton.getAppInfo()
				.getServletContainerVersion());
		state.setIsNativeUser(!AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
				sessionId));
		state.setJavaVersion(System.getProperty("java.version"));
		state.setUserName(ServletUtils.getUserNameFromSession());
		state.setSqlVersion(getSQLVersion());
		return state;
	}

	private static String getAppVersion() throws IOException {
		InputStream is = AppProps.loadResToStream(VERSION_FILE);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String major;
		try {
			major = buf.readLine();
		} finally {
			buf.close();
		}

		is = AppProps.loadResToStream(BUILD_FILE);
		buf = new BufferedReader(new InputStreamReader(is));
		String build;
		try {

			build = buf.readLine();
		} finally {
			buf.close();
		}
		return String.format("%s.%s", major, build);
	}

	private static String getSQLVersion() throws SQLException {
		Connection conn = ConnectionFactory.getConnection();
		String sql = "select @@VERSION AS [Version]";
		PreparedStatement stat = conn.prepareStatement(sql);
		try {
			boolean hasResult = stat.execute();
			if (hasResult) {
				ResultSet rs = stat.getResultSet();
				if (rs.next()) {
					String fullVersion = rs.getString("Version");
					if (fullVersion != null) {
						return fullVersion.split("\t")[0];
					}
				}
			}
		} finally {
			stat.close();
			conn.close();
		}
		return null;
	}
}
