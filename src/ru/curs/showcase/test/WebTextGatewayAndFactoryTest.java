package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextGatewayAndFactoryTest extends AbstractTestWithDefaultUserData {
	/**
	 * Тест на случай, когда не задано преобразование.
	 * 
	 */
	@Test
	public void testGetStaticDataBySP() {
		String prefix = "<root>";
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "2");

		WebTextGateway wtgateway = new WebTextDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
		String out = XMLUtils.documentToString(rawWT.getData());
		new WebText(out);
		assertTrue(out.startsWith(prefix));
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() {
		DataPanelElementInfo element =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);

		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		WebTextGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, null);
	}
}
