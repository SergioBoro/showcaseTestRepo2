package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.IncorrectElementException;
import ru.curs.showcase.model.geomap.*;

/**
 * Тест шлюза получения данных карты.
 * 
 * @author den
 * 
 */
public class GeoMapGatewayTest extends AbstractTestBasedOnFiles {
	/**
	 * Основная функция тестирования шлюза.
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		GeoMapGateway gateway = new GeoMapDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);

		GeoMapGateway gateway = new GeoMapDBGateway();
		gateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		GeoMapGateway gateway = new GeoMapDBGateway();
		gateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		GeoMapGateway gateway = new GeoMapDBGateway();
		gateway.getRawData(null, null);
	}
}
