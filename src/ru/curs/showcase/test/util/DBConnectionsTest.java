package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тесты соединения с БД.
 * 
 * @author den
 * 
 */
public class DBConnectionsTest extends AbstractTestWithDefaultUserData {

	/**
	 * Простой тест работы фабрики соединений.
	 * 
	 * @throws SQLException
	 * 
	 */
	@Test
	public void testConnectionsSimple() {
		Connection conn1 = ConnectionFactory.getInstance().acquire();
		try {
			assertNotNull(conn1);
		} finally {
			ConnectionFactory.getInstance().release(conn1);
		}
	}

	/**
	 * Проверка создания нескольких разных соединений.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testConnectionsMany() throws SQLException {
		Connection conn1 = ConnectionFactory.getInstance().acquire();
		Connection conn2 = ConnectionFactory.getInstance().acquire();
		Connection conn3 = ConnectionFactory.getInstance().acquire();
		try {
			assertNotNull(conn1);
			assertNotNull(conn2);
			assertNotNull(conn3);
			assertNotSame(conn1, conn2);
			assertNotSame(conn2, conn3);
			assertFalse(conn1.isClosed());
			assertFalse(conn2.isClosed());
			assertFalse(conn3.isClosed());
		} finally {
			ConnectionFactory.getInstance().release(conn1);
			ConnectionFactory.getInstance().release(conn2);
			ConnectionFactory.getInstance().release(conn3);
		}
	}

	@Test
	public void testUnRegisterDrivers() throws SQLException {
		ConnectionFactory.getInstance().acquire();
		Driver driver = ConnectionFactory.unregisterDrivers();
		DriverManager.registerDriver(driver);
		ConnectionFactory.getInstance().acquire();
	}

	@Test
	public void testUnRegister0Drivers() throws SQLException {
		Driver driver = ConnectionFactory.unregisterDrivers();
		try {
			ConnectionFactory.unregisterDrivers();
		} finally {
			DriverManager.registerDriver(driver);
		}

	}
}
