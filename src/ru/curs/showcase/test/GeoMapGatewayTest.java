package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.geomap.GeoMapDBGateway;
import ru.curs.showcase.model.grid.RecordSetElementGateway;
import ru.curs.showcase.model.jython.RecordSetElementJythonGateway;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

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

	@Test
	public void testGetDataJython() {
		CompositeContext context = getTestContext1();
		context.setSession("</" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + ">");
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		element.setProcName("geomap/GeoMapSimple.py");
		generateTestTabWithElement(element);

		RecordSetElementGateway<CompositeContext> gateway = new RecordSetElementJythonGateway();
		gateway.getRawData(context, element);
	}
}
