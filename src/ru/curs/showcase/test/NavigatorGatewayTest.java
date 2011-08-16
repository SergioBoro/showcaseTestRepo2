package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.util.xml.*;

/**
 * Класс тестов для шлюза навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorGatewayTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест функции получения данных для навигатора.
	 * 
	 */
	@Test
	public void testGetData() throws SAXException, IOException {
		DocumentBuilder builder = XMLUtils.createBuilder();
		Document doc = null;
		NavigatorSelector selector = new NavigatorSelector();
		NavigatorGateway gw = selector.getGateway();
		try {
			InputStream xml = gw.getRawData(new CompositeContext(), selector.getSourceName());
			doc = builder.parse(xml);
		} finally {
			gw.releaseResources();
		}
		assertEquals(GeneralXMLHelper.NAVIGATOR_TAG, doc.getDocumentElement().getNodeName());
	}
}
