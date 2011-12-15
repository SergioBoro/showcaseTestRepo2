package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.geomap.GeoMapDBGateway;
import ru.curs.showcase.model.grid.RecordSetElementGateway;

/**
 * Тест шлюза получения данных карты.
 * 
 * @author den
 * 
 */
public class GeoMapGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основная функция тестирования шлюза.
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		gateway.getRawData(context, element);
	}
}
