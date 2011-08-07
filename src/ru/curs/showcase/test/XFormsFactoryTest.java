package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты для фабрики XForms.
 * 
 * @author den
 * 
 */
public class XFormsFactoryTest extends AbstractTestBasedOnFiles {

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

	/**
	 * Тест функции получения XForms из сервисного уровня.
	 * 
	 */
	@Test
	public void testServiceLayer() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		XForms xforms = sl.getXForms(context, element, null);

		assertNotNull(context.getSession());
		Action action = xforms.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xforms.getEventManager().getEvents().size());
		action = xforms.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		assertNotNull(xforms.getXFormParts());
		assertTrue(xforms.getXFormParts().size() > 0);
	}

	/**
	 * Проверяет сериализацию текущего контекста в XML.
	 */
	@Test
	public void testContextToXML() {
		CompositeContext context = CompositeContext.createCurrent();
		Document doc = XMLUtils.objectToXML(context);
		assertEquals(Action.CONTEXT_TAG, doc.getDocumentElement().getNodeName());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("additional").getLength());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("main").getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName(Action.FILTER_TAG)
				.getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName("session").getLength());
		CompositeContext context2 =
			(CompositeContext) XMLUtils.xmlToObject(doc.getDocumentElement(),
					CompositeContext.class);
		assertEquals(context, context2);
	}

	/**
	 * Тестирования сериализации информации об элементе панели в XML.
	 * 
	 */
	@Test
	public void testDPElementInfoToXML() {
		DataPanelElementInfo element = getTestXForms1Info();
		Document doc = XMLUtils.objectToXML(element);
		assertEquals("element", doc.getDocumentElement().getNodeName());
		DataPanelElementInfo el2 =
			(DataPanelElementInfo) XMLUtils.xmlToObject(doc.getDocumentElement(),
					DataPanelElementInfo.class);
		assertEquals(element, el2);
	}

	private XFormsFactory createFactory() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		XFormsGateway gateway = new XFormsDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		return new XFormsFactory(raw);
	}
}
