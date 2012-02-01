package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;

/**
 * Тесты для фабрики информационных панелей.
 * 
 * @author den
 * 
 */
public class DataPanelSLTest extends AbstractTest {

	@Test
	public void testBySLFromFile() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("test2.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		DataPanelElementInfo elInfo = panel.getTabs().get(0).getElements().get(0);
		assertFalse(elInfo.getCacheData());
		assertFalse(elInfo.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, elInfo.getRefreshInterval()
				.intValue());
	}

	@Test
	public void testBySLFromDB() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("dp0903");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		checkTestDP(panel);
	}

	@Test
	public void testBySLFromJython() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("datapanel/dp0903.py");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		checkTestDP(panel);
	}

	private void checkTestDP(final DataPanel panel) {
		assertEquals("dp0903", panel.getId().getString());
		final int tabsCount = 5;
		assertEquals(tabsCount, panel.getTabs().size());
		final int elCount = 3;
		assertEquals(elCount, panel.getTabById("02").getElements().size());
		assertNotNull(panel.getTabById("02").getElementInfoById("0202"));
	}

	@Test
	public void testShowLoadingMessage() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("1103.xml");
		action.setDataPanelLink(dpLink);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel panel = command.execute();

		assertTrue(panel.getTabById("08").getElementInfoById("0801").getShowLoadingMessage());
		assertFalse(panel.getTabById("08").getElementInfoById("d1").getShowLoadingMessage());
		assertFalse(panel.getTabById("08").getElementInfoById("d2").getShowLoadingMessage());
		assertFalse(new DataPanelElementInfo().getShowLoadingMessage());
	}

}
