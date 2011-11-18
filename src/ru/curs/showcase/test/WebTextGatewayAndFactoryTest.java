package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.html.*;
import ru.curs.showcase.model.html.webtext.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextGatewayAndFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String WEB_TEXT_GET_JYTHON_PROC_PY = "WebTextGetJythonProc.py";

	/**
	 * Тест на случай, когда не задано преобразование.
	 * 
	 */
	@Test
	public void testGetStaticDataBySP() {
		String prefix = "<root>";
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "2");

		HTMLGateway wtgateway = new WebTextDBGateway();
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

		HTMLGateway wtgateway = new WebTextDBGateway();
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

		HTMLGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, element);
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() {
		HTMLGateway wtgateway = new WebTextDBGateway();
		wtgateway.getRawData(null, null);
	}

	@Test(expected = SettingsFileOpenException.class)
	public void testNotExistsJython() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_pas.py");
		CompositeContext context = new CompositeContext();
		HTMLJythonGateway gateway = new HTMLJythonGateway();
		gateway.getRawData(context, elInfo);
	}

	@Test
	public void testValidateExceptionInJython() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		CompositeContext context = new CompositeContext();
		context.setMain("плохой");
		context.setSession("<sessioninfo/>");
		HTMLJythonGateway gateway = new HTMLJythonGateway();
		try {
			gateway.getRawData(context, elInfo);
		} catch (ValidateException e) {
			assertEquals("проверка на ошибку сработала (1)", e.getLocalizedMessage());
			return;
		}
		fail();
	}

	@Test
	public void testJythonGetData() {
		final String region = "Алтайский край";

		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		elInfo.setTransformName("pas.xsl");
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(region);
		context.setAdditional(ADD_CONDITION);
		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertNotNull(webtext);
		assertNotNull(webtext.getData());
		assertTrue(webtext.getData().startsWith("<div>"));
		assertTrue(webtext.getData().indexOf(region) > -1);
		assertEquals(0, webtext.getEventManager().getEvents().size());
		assertNull(webtext.getDefaultAction());
	}

	@Test
	public void testJythonGetSettings() {
		final String region = "Алтайский край";

		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		elInfo.setTransformName("pas.xsl");
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(region);
		context.setAdditional("withsettings");
		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertNotNull(webtext);
		assertNotNull(webtext.getDefaultAction());
		assertEquals("я оригинальный", webtext.getDefaultAction().getDataPanelLink()
				.getElementLinkById("d2").getContext().getAdditional());
		assertEquals(1, webtext.getEventManager().getEvents().size());
		assertEquals("я оригинальный", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinkById("d2").getContext().getAdditional());
	}
}
