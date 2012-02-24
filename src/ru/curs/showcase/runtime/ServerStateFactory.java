package ru.curs.showcase.runtime;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.regex.*;

import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Построитель объекта с текущим состоянием серверной части.
 * 
 * @author den
 * 
 */
public final class ServerStateFactory {

	private static final String DOJO_VERSION_FILE = "/js/dojo/package.json";
	private static final String GWTVERSION_FILE = "gwtversion";
	private static final String BUILD_FILE = "build";
	private static final String VERSION_FILE = "version.properties";

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
	public static ServerState build(final String sessionId) throws SQLException {
		ServerState state = new ServerState();
		state.setServerTime(TextUtils.getCurrentLocalDate());
		state.setAppVersion(getAppVersion());
		state.setServletContainerVersion(AppInfoSingleton.getAppInfo()
				.getServletContainerVersion());

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			state.setIsNativeUser(false);
		} else {
			state.setIsNativeUser(!((UserAndSessionDetails) SecurityContextHolder.getContext()
					.getAuthentication().getDetails()).isAuthViaAuthServer());
		}

		state.setJavaVersion(System.getProperty("java.version"));

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			state.setUserInfo(((UserAndSessionDetails) SecurityContextHolder.getContext()
					.getAuthentication().getDetails()).getUserInfo());
		}
		state.setSqlVersion(getSQLVersion());
		state.setDojoVersion(getDojoVersion());
		state.setGwtVersion(getGwtVersion());
		state.setCaseSensivityIDs(IDSettings.getInstance().getCaseSensivity());
		return state;
	}

	private static String getGwtVersion() {
		String data;
		try {
			InputStream is = FileUtils.loadResToStream(GWTVERSION_FILE);
			data = TextUtils.streamToString(is);
		} catch (IOException e) {
			return null;
		}

		Pattern pattern = Pattern.compile("Google Web Toolkit ([0-9.]+)");
		Matcher matcher = pattern.matcher(data);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private static String getDojoVersion() {
		File file = new File(AppInfoSingleton.getAppInfo().getWebAppPath() + DOJO_VERSION_FILE);
		if (!file.exists()) {
			return null;
		}

		String data;
		try {
			InputStream is = new FileInputStream(file);
			data = TextUtils.streamToString(is);
		} catch (IOException e) {
			return null;
		}
		Pattern pattern = Pattern.compile("\"version\":\"([a-z0-9.]+)\"");
		Matcher matcher = pattern.matcher(data);
		matcher.find();
		if (matcher.groupCount() > 0) {
			return matcher.group(1);
		}
		return null;
	}

	private static String getAppVersion() {
		return getAppVersion("");
	}

	public static String getAppVersion(final String baseDir) {
		Properties prop = new Properties();
		try {
			InputStream is = FileUtils.loadResToStream(baseDir + VERSION_FILE);
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
		} catch (IOException | NullPointerException e) {
			throw new SettingsFileOpenException(baseDir + VERSION_FILE, SettingsFileType.VERSION);
		}
		String major = prop.getProperty("version");

		String build;
		try {
			InputStream is = FileUtils.loadResToStream(baseDir + BUILD_FILE);
			try (BufferedReader buf = new BufferedReader(new InputStreamReader(is));) {
				build = buf.readLine();
				Pattern pattern = Pattern.compile("(\\d+|development)");
				Matcher matcher = pattern.matcher(build);
				matcher.find();
				build = matcher.group();
			}
		} catch (IOException | NullPointerException e) {
			throw new SettingsFileOpenException(baseDir + BUILD_FILE, SettingsFileType.VERSION);
		}
		return String.format("%s.%s", major, build);
	}

	private static String getSQLVersion() throws SQLException {

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

		Connection conn = ConnectionFactory.getInstance().acquire();
		try {
			try (PreparedStatement stat = conn.prepareStatement(sql)) {
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
			}
		} finally {
			ConnectionFactory.getInstance().release(conn);
		}
		return null;
	}
}
