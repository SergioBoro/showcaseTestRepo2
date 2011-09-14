package ru.curs.showcase.runtime;

import java.sql.*;

/**
 * Фабрика для создания соединений с БД.
 * 
 * @author den
 * 
 */
public final class ConnectionFactory {
	/**
	 * Параметр файла настроек приложения, содержащий адрес для соединения с SQL
	 * сервером через JDBC.
	 */
	public static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	private static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	private static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	private ConnectionFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Функция получения нового соединения.
	 * 
	 * @return - соединение.
	 */
	public static Connection getConnection() {
		try {
			Connection result =
				DriverManager.getConnection(AppProps.getRequiredValueByName(CONNECTION_URL_PARAM),
						AppProps.getRequiredValueByName(CONNECTION_USERNAME_PARAM),
						AppProps.getRequiredValueByName(CONNECTION_PASSWORD_PARAM));
			return result;
		} catch (SQLException e) {
			throw new DBConnectException(e);
		}
	}

	/**
	 * Возвращает тип SQL сервера.
	 */
	public static SQLServerType getSQLServerType() {
		String url = AppProps.getRequiredValueByName(CONNECTION_URL_PARAM).toLowerCase();

		// TODO regexp
		final String mssql = "sqlserver";
		final String postgresql = "postgresql";
		final String oracle = "oracle";

		SQLServerType st = null;
		if (url.indexOf(mssql) > -1) {
			st = SQLServerType.MSSQL;
		} else {
			if (url.indexOf(postgresql) > -1) {
				st = SQLServerType.POSTGRESQL;
			} else {
				if (url.indexOf(oracle) > -1) {
					st = SQLServerType.ORACLE;
				}
			}
		}

		return st;
	}
}
