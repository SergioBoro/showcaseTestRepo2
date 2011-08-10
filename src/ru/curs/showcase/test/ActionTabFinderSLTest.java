package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;

/**
 * Проверка ActionTabFinder через SL.
 * 
 * @author den
 * 
 */
public class ActionTabFinderSLTest extends AbstractTest {
	@Test
	public void testReadFirstTabForDBDPFromNavigatorDynSessionContext() throws GeneralException {
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		Navigator navigator = sl.getNavigator(context);

		assertEquals("1", navigator.getGroupById("00").getElementById("04").getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromEventDynMain() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_main");
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText webtext = sl.getWebText(context, elInfo);

		assertEquals("01", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromEventDynSession() throws GeneralException {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_session");
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		WebText webtext = sl.getWebText(context, elInfo);

		assertEquals("1", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromSettingsDynMain() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_dyn_dp_main");
		generateTestTabWithElement(elInfo);
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = sl.getGrid(context, elInfo, null);

		assertEquals("01", grid.getDefaultAction().getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromSettingsDynSession() throws GeneralException {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_dyn_dp_session");
		generateTestTabWithElement(elInfo);
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = sl.getGrid(context, elInfo, null);

		assertEquals("1", grid.getDefaultAction().getDataPanelLink().getTabId());
	}
}
