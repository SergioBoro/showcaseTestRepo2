package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.runtime.ConnectionFactory;

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
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GRID);

		GridGateway gateway = new GridDBGateway();
		gateway.getRawDataAndSettings(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		element.setProcName("proc");

		GridGateway gateway = new GridDBGateway();
		gateway.getRawDataAndSettings(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		GridGateway gateway = new GridDBGateway();
		gateway.getRawDataAndSettings(null, null);
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
		Connection conn = ConnectionFactory.getConnection();
		try {
			GridGateway gateway = new GridDBGateway(conn);
			GridContext gc = new GridContext();
			gc.setPageNumber(2);
			gc.setPageSize(2);
			gc.apply(context);
			gateway.getRawData(gc, element);
		} finally {
			conn.close();
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
		assertNull(res.getProperties());
		assertNotNull(res.getSpCallHelper());
		assertFalse(res.getSpCallHelper().getConn().isClosed());
		assertNotNull(res.getSpCallHelper().getStatement().getResultSet());

		res.getSpCallHelper().releaseResources();
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
		assertNull(res.getProperties());
		res.prepareSettings();
		assertNotNull(res.getProperties());
		assertNotNull(res.getSpCallHelper());
		assertFalse(res.getSpCallHelper().getConn().isClosed());
		assertNull(res.getSpCallHelper().getStatement().getResultSet());

		res.getSpCallHelper().releaseResources();
	}
}
