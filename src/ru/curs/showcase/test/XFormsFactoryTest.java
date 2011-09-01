package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.*;
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

	@Test
	public void testUserdataAddToXForms() throws Exception {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("Showcase_Template_all.xml");
		XFormsContext context = new XFormsContext(getTestContext1());
		generateTestTabWithElement(elInfo);
		XFormsGateway gateway = new XFormsDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, elInfo);
		XFormsFactory factory = new XFormsFactory(raw);
		XForms result = factory.build();

		assertTrue(result
				.getXFormParts()
				.get(2)
				.indexOf(
						String.format("%s/%s?%s=%s&", ExchangeConstants.SECURED_SERVLET_PREFIX,
								ExchangeConstants.SUBMIT_SERVLET,
								ExchangeConstants.URL_PARAM_USERDATA,
								ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT)) > 0);
	}
}
