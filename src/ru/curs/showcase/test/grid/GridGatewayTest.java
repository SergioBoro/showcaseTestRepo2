package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тесты для шлюза грида.
 * 
 * @author den
 * 
 */
public class GridGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основной тест на получение данных из БД.
	 * 
	 */
	@Test
	public void testGetData() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getTestGridInfo();

		GridGateway gateway = new GridDBGateway();
		gateway.getRawDataAndSettings(context, element);
	}

	/**
	 * Проверка получения только данных для грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetDataOnly() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo2();

		try (Connection conn = ConnectionFactory.getConnection()) {
			GridGateway gateway = new GridDBGateway(conn);
			GridContext gc = new GridContext();
			gc.setPageNumber(2);
			gc.setPageSize(2);
			gc.apply(context);
			gateway.getRawData(gc, element);
		}
	}

	/**
	 * Проверка получения только данных для грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetDataOnlyV2() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridGateway gateway = new GridDBGateway();
		GridContext gc = new GridContext(context);
		ElementRawData res = gateway.getRawData(gc, elInfo);

		assertNotNull(res);
		assertEquals(gc, res.getCallContext());
		assertEquals(elInfo, res.getElementInfo());
		assertNull(res.getSettings());
		assertNotNull(res.getSpQuery());
		assertFalse(res.getSpQuery().getConn().isClosed());
		assertNotNull(res.getSpQuery().getStatement().getResultSet());

		res.getSpQuery().close();
	}

	/**
	 * Проверка получения настроек грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetSettingsOnly() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		ElementSettingsGateway gateway = new ElementSettingsDBGateway();
		ElementRawData res = gateway.getRawData(context, elInfo);

		assertNotNull(res);
		assertEquals(context, res.getCallContext());
		assertEquals(elInfo, res.getElementInfo());
		assertNull(res.getSettings());
		res.prepareSettings();
		assertNotNull(res.getSettings());
		assertNotNull(res.getSpQuery());
		assertFalse(res.getSpQuery().getConn().isClosed());
		assertNull(res.getSpQuery().getStatement().getResultSet());

		res.getSpQuery().close();
	}
}
