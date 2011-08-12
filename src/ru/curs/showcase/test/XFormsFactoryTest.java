package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.xform.*;

/**
 * Тесты для фабрики XForms.
 * 
 * @author den
 * 
 */
public class XFormsFactoryTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест на создание фабрики.
	 * 
	 */
	@Test
	public void testXFormsFactory() {
		createFactory();
	}

	/**
	 * Test method for
	 * {@link ru.curs.showcase.model.xform.XFormsFactory#build()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuild() throws Exception {
		XFormsFactory factory = createFactory();
		factory.build();
	}

	private XFormsFactory createFactory() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		XFormsGateway gateway = new XFormsDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		return new XFormsFactory(raw);
	}
}
