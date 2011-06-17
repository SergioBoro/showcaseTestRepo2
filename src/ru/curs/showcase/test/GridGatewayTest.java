package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.IncorrectElementException;
import ru.curs.showcase.model.grid.*;

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
		gateway.getFactorySource(context, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GRID);

		GridGateway gateway = new GridDBGateway();
		gateway.getFactorySource(null, element);
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
		gateway.getFactorySource(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		GridGateway gateway = new GridDBGateway();
		gateway.getFactorySource(null, null);
	}
}
