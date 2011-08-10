package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextSLTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основной тест для проверки работы WebTextDBGateway.
	 * 
	 */
	@Test
	public void testGetData() throws GeneralException {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "1");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(context, element);

		assertNotNull(context.getSession());
		assertEquals(0, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	/**
	 * Тест на выборку событий и действия по умолчанию из БД.
	 * 
	 */
	@Test
	public void testEventsAndDefAction() throws GeneralException {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "3");
		CompositeContext clonedContext = context.gwtClone();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(clonedContext, element);

		assertEquals(1, wt.getEventManager().getEvents().size());
		assertEquals("0", wt.getEventManager().getEvents().get(0).getId1());

		Action action = wt.getEventManager().getEvents().get(0).getAction();
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		action = wt.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		assertNotNull(wt.getData());
	}

	private void stdCheckAction(final CompositeContext context, final Action action) {
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertEquals(context, action.getContext());
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals(context, action.getDataPanelLink().getElementLinks().get(0).getContext());
		assertNull(action.getContext().getSession());
	}

	/**
	 * Тест для случая, когда не задана хранимая процедура, возвращающая данные.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetStaticDataByXSLT() throws GeneralException {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = new CompositeContext();
		el.setTransformName("bal_test.xsl");
		generateTestTabWithElement(el);
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText wt = serviceLayer.getWebText(context, el);

		assertTrue(wt.getData().startsWith("<h3>Здесь находится просто статический текст</h3>"));
		assertTrue(wt.getData().endsWith(
				"<p>Коля у Светы спёр кассеты, а Света у Коли уперла костет</p>"));
	}
}
