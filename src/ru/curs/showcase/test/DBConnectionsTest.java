package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.runtime.ConnectionFactory;

/**
 * Тесты соединения с БД.
 * 
 * @author den
 * 
 */
public class DBConnectionsTest extends AbstractTestBasedOnFiles {

	/**
	 * Простой тест работы фабрики соединений.
	 * 
	 * @throws SQLException
	 * 
	 */
	@Test
	public void testConnectionsSimple() throws SQLException {
		Connection conn1 = ConnectionFactory.getConnection();
		try {
			assertNotNull(conn1);
		} finally {
			conn1.close();
		}

	}

	/**
	 * Проверка создания нескольких разных соединений.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testConnectionsMany() throws SQLException {
		Connection conn1 = ConnectionFactory.getConnection();
		Connection conn2 = ConnectionFactory.getConnection();
		Connection conn3 = ConnectionFactory.getConnection();
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
			conn1.close();
			conn2.close();
			conn3.close();
		}

	}
}
