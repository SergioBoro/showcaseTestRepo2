package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridRequestedSettings;
import ru.curs.showcase.exception.IncorrectElementException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.util.ConnectionFactory;

/**
 * Тесты для шлюза грида.
 * 
 * @author den
 * 
 */
public class GridGatewayTest extends AbstractTestBasedOnFiles {
	/**
	 * Основной тест на получение данных из БД.
	 * 
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext1();
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
	 */
	@Test
	public void testGetDataOnly() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test1.1.xml", "2", "2");
		Connection conn = ConnectionFactory.getConnection();
		GridGateway gateway = new GridDBGateway(conn);
		GridRequestedSettings settings = new GridRequestedSettings();
		settings.setPageNumber(2);
		settings.setPageSize(2);
		gateway.getRawData(context, element, settings);
	}

	/**
	 * Проверка получения только данных для грида.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetDataOnlyV2() throws SQLException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getDPElement("test1.1.xml", "2", "2");
		GridGateway gateway = new GridDBGateway();
		GridRequestedSettings settings = new GridRequestedSettings();
		ElementRawData res = gateway.getRawData(context, elInfo, settings);

		assertNotNull(res);
		assertEquals(context, res.getCallContext());
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
		DataPanelElementInfo elInfo = getDPElement("test1.1.xml", "2", "2");
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
