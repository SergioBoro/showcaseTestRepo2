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
public class XFormFactoryTest extends AbstractTestWithDefaultUserData {

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
	 * {@link ru.curs.showcase.model.xform.XFormFactory#build()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuild() throws Exception {
		XFormFactory factory = createFactory();
		factory.build();
	}

	private XFormFactory createFactory() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		XFormGateway gateway = new XFormDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		return new XFormFactory(raw);
	}

	@Test
	public void testUserdataAddToXForms() throws Exception {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("Showcase_Template_all.xml");
		XFormContext context = new XFormContext(getTestContext1());
		generateTestTabWithElement(elInfo);
		XFormGateway gateway = new XFormDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, elInfo);
		XFormFactory factory = new XFormFactory(raw);
		XForm result = factory.build();

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
