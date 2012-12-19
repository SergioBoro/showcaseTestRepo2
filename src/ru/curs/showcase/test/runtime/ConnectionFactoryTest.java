package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.*;

import ru.curs.showcase.runtime.*;

import com.ziclix.python.sql.PyConnection;

/**
 * JUnit тест фабрики соединений с БД.
 * 
 * @author bogatov
 * 
 */
public class ConnectionFactoryTest {
	/**
	 * Имя userdata.
	 */
	private static final String USERDATAID = "default";

	/**
	 * Путь к userdata.
	 */
	private static final String USERDATAPATH =
		"c:/_WORK/lancelot/src/workspace/showcase/userdatas/";

	/**
	 * Инициализация userdata, фабрики соединений.
	 */
	@BeforeClass
	public static void beforeClass() {
		// AppInitializer.initialize();
		AppInfoSingleton app = AppInfoSingleton.getAppInfo();
		app.setCurUserDataId(USERDATAID);
		app.addUserData(USERDATAID, USERDATAPATH + USERDATAID);
	}

	@AfterClass
	public static void afterClass() {
		ConnectionFactory.getInstance().clear();
	}

	/**
	 * Тест получения соединения.
	 */
	@Test
	public void connectionTest() {
		ConnectionFactory cf = ConnectionFactory.getInstance();
		Connection conn = cf.acquire();
		try {
			assertNotNull(conn);
		} finally {
			cf.release(conn);
		}
		assertEquals(1, cf.getAllCount());
	}

	/**
	 * Тест получения соединения для использования в Jython.
	 */
	@Test
	public void pyConnectionTest() {
		PyConnection conn = ConnectionFactory.getPyConnection();
		try {
			assertNotNull(conn);
		} finally {
			conn.close();
		}
	}
}
