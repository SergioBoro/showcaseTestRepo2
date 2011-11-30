package ru.curs.showcase.test.html;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.model.html.HTMLBasedElementRawData;
import ru.curs.showcase.model.html.xform.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Специальный тестовый класс для проверки замены переменных в HTML блоках в
 * Showcase. TODO - доделать.
 * 
 * @author den
 * 
 */
public class HTMLVariablesFactoryTest extends AbstractTestWithDefaultUserData {

	/**
	 * Переменные в XForms + подстановка userdata.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testXFormsVariables() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", "0205");

		XFormGateway gateway = new XFormFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		XFormFactory factory = new XFormFactory(raw);
		XForm xforms = factory.build();
		assertTrue(xforms.getXFormParts().get(2).indexOf("?userdata=default") > -1);
	}
}
