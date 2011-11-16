package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.model.grid.GridGetCommand;
import ru.curs.showcase.model.navigator.NavigatorGetCommand;
import ru.curs.showcase.model.webtext.WebTextGetCommand;

/**
 * Проверка ActionTabFinder через SL.
 * 
 * @author den
 * 
 */
public class ActionTabFinderSLTest extends AbstractTest {
	@Test
	public void testReadFirstTabFromDBFromNavigatorDynSessionContext() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator navigator = command.execute();

		assertEquals("1", navigator.getGroupById("00").getElementById("04").getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromEventDynMainContext() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_main");

		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertEquals("01", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromEventDynSessionContext() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_dyn_dp_session");

		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertEquals("1", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromSettingsDynMainContext() {
		GridContext context = GridContext.createFirstLoadDefault();
		context.apply(getTestContext1());
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_dyn_dp_main");
		generateTestTabWithElement(elInfo);

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();

		assertEquals("01", grid.getDefaultAction().getDataPanelLink().getTabId());
	}

	@Test
	public void testReadFirstTabFromDBFromSettingsDynSessionContext() {
		GridContext context = GridContext.createFirstLoadDefault();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_dyn_dp_session");
		generateTestTabWithElement(elInfo);

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();

		assertEquals("1", grid.getDefaultAction().getDataPanelLink().getTabId());
	}
}
