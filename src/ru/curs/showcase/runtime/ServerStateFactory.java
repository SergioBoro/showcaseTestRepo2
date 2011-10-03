package ru.curs.showcase.runtime;

import java.io.*;
import java.sql.*;
import java.util.regex.*;

import ru.curs.showcase.app.api.ServerState;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Построитель объекта с текущим состоянием серверной части.
 * 
 * @author den
 * 
 */
public final class ServerStateFactory {

	private static final String BUILD_FILE = "build";
	private static final String VERSION_FILE = "version";

	private ServerStateFactory() {
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
	public static ServerState build(final String sessionId) throws SQLException, IOException {
		ServerState state = new ServerState();
		state.setServerTime(TextUtils.getCurrentLocalDate());
		state.setAppVersion(getAppVersion());
		state.setServletContainerVersion(AppInfoSingleton.getAppInfo()
				.getServletContainerVersion());
		state.setIsNativeUser(!AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
				sessionId));
		state.setJavaVersion(System.getProperty("java.version"));
		state.setUserName(ServletUtils.getCurrentSessionUserName());
		state.setSqlVersion(getSQLVersion());

		return state;
	}

	private static String getAppVersion() throws IOException {
		return getAppVersion("");
	}

	public static String getAppVersion(final String baseDir) throws IOException {
		InputStream is = FileUtils.loadResToStream(baseDir + VERSION_FILE);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String major;
		try {
			major = buf.readLine();
		} finally {
			buf.close();
		}

		is = FileUtils.loadResToStream(baseDir + BUILD_FILE);
		buf = new BufferedReader(new InputStreamReader(is));
		String build;
		try {
			build = buf.readLine();
			Pattern pattern = Pattern.compile("(\\d+|development)");
			Matcher matcher = pattern.matcher(build);
			matcher.find();
			build = matcher.group();
		} finally {
			buf.close();
		}
		return String.format("%s.%s", major, build);
	}

	private static String getSQLVersion() throws SQLException {
		Connection conn = ConnectionFactory.getConnection();

		String fileName =
			String.format("%s/version_%s.sql", AppProps.SCRIPTSDIR, ConnectionFactory
					.getSQLServerType().toString().toLowerCase());

		String sql = "";
		try {
			sql = TextUtils.streamToString(FileUtils.loadResToStream(fileName));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, SettingsFileType.SQLSCRIPT);
		}
		if (sql.trim().isEmpty()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.SQLSCRIPT);
		}

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
