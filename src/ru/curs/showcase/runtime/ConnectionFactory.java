package ru.curs.showcase.runtime;

import java.sql.*;

import com.ziclix.python.sql.PyConnection;

/**
 * Фабрика для создания соединений с БД.
 * 
 * @author den
 * 
 */
public final class ConnectionFactory extends PoolByUserdata<Connection> {
	/**
	 * Параметр файла настроек приложения, содержащий адрес для соединения с SQL
	 * сервером через JDBC.
	 */
	public static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	private static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	private static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	private static ConnectionFactory instance;

	private ConnectionFactory() {
		super();
	}

	public static ConnectionFactory getInstance() {
		if (instance == null) {
			instance = new ConnectionFactory();
		}
		return instance;
	}

	@Override
	protected Pool<Connection> getLock() {
		return ConnectionFactory.getInstance();
	}

	@Override
	protected Connection createReusableItem() {
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

	public static PyConnection getPyConnection() {
		try {
			return new PyConnection(getInstance().acquire());
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

	public static Driver unregisterDrivers() {
		Driver result = null;
		while (DriverManager.getDrivers().hasMoreElements()) {
			try {
				result = DriverManager.getDrivers().nextElement();
				DriverManager.deregisterDriver(result);
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		}
		return result;
	}
}
