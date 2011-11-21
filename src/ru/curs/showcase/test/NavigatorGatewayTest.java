package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.xml.*;

/**
 * Класс тестов для шлюза навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorGatewayTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест функции получения данных для навигатора для default userdata.
	 * 
	 */
	@Test
	public void testGetData() throws SAXException, IOException {
		DocumentBuilder builder = XMLUtils.createBuilder();
		Document doc = null;
		NavigatorSelector selector = new NavigatorSelector();
		try (NavigatorGateway gw = selector.getGateway()) {
			InputStream xml = gw.getRawData(new CompositeContext(), selector.getSourceName());
			doc = builder.parse(xml);
		}
		assertEquals(GeneralXMLHelper.NAVIGATOR_TAG, doc.getDocumentElement().getNodeName());
	}

	@Test
	public void testJythonNavigator() {
		AppInfoSingleton.getAppInfo().setCurUserDataId(TEST1_USERDATA);
		CompositeContext context = new CompositeContext();
		context.setSession("<sessioninfo/>");
		NavigatorGateway gateway = new NavigatorJythonGateway();
		InputStream is = gateway.getRawData(context, "navigator/NavJythonProc.py");

		assertNotNull(is);
	}
}
