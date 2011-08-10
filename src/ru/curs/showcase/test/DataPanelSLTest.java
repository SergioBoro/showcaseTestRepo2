package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;

/**
 * Тесты для фабрики информационных панелей.
 * 
 * @author den
 * 
 */
public class DataPanelSLTest extends AbstractTest {

	@Test
	public void testBySLFromFile() throws GeneralException {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("test2.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		DataPanel panel = serviceLayer.getDataPanel(action);

		DataPanelElementInfo elInfo = panel.getTabs().get(0).getElements().get(0);
		assertFalse(elInfo.getCacheData());
		assertFalse(elInfo.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, elInfo.getRefreshInterval()
				.intValue());
	}

	@Test
	public void testBySLFromDB() throws GeneralException {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("dp0903");
		action.setDataPanelLink(dpLink);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		DataPanel panel = serviceLayer.getDataPanel(action);
		assertEquals("dp0903", panel.getId());
		final int tabsCount = 5;
		assertEquals(tabsCount, panel.getTabs().size());
		final int elCount = 3;
		assertEquals(elCount, panel.getTabById("02").getElements().size());
		assertNotNull(panel.getTabById("02").getElementInfoById("0202"));
	}

}
