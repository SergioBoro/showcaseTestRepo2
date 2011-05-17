package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.DataFile;
import ru.curs.showcase.model.datapanel.*;

/**
 * Тесты для фабрики информационных панелей.
 * 
 * @author den
 * 
 */
public class DataPanelFactoryTest extends AbstractTestBasedOnFiles {

	/**
	 * Основной тест.
	 * 
	 */
	@Test
	public void testGetData() {
		final int panelsCount = 3;
		final int firstPanelSecondTabElCount = 5;

		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML("test.xml");
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);

		DataPanelTab tab;
		DataPanelElementInfo el;

		assertEquals(panelsCount, panel.getTabs().size());
		assertEquals(DataPanelRefreshMode.BY_TIMER, panel.getRefreshMode());
		final int refreshInterval = 120;
		assertEquals(refreshInterval, panel.getRefreshInterval().intValue());
		assertNotNull(panel.getTabById("1"));
		el = panel.getTabById("1").getElementInfoById("1");
		assertFalse(el.getHideOnLoad());
		assertNotNull(el);
		assertTrue(el.needTransform());
		tab = panel.getTabById("2");
		assertNotNull(tab);
		assertEquals("2", tab.getId());
		assertEquals(1, tab.getPosition().intValue());
		assertEquals("Балансы", tab.getName());
		assertEquals(firstPanelSecondTabElCount, tab.getElements().size());
		assertNotNull(tab.getElementInfoById("2"));
		el = tab.getElementInfoById("3");
		assertNotNull(el);
		assertEquals("3", el.getId());
		assertEquals(DataPanelElementType.CHART, el.getType());
		assertEquals("chart_bal", el.getProcName());
		assertTrue(el.getHideOnLoad());
	}

	/**
	 * Метод, эмулирующий вызов GWT на уровне сервера.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testFull() throws GeneralServerException {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("test2.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl();
		DataPanel panel = serviceLayer.getDataPanel(action);

		assertEquals(DataPanelRefreshMode.EVERY_TIME, panel.getRefreshMode());
		assertEquals(DataPanel.DEF_TIMER_INTERVAL, panel.getRefreshInterval().intValue());
	}

	/**
	 * Проверка загрузки процедур XForms.
	 * 
	 */
	@Test
	public void testXFormsProcLoad() {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML("test1.1.xml");
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("2").getElementInfoById("08");
		assertNotNull(el);
		assertEquals(2, el.getProcs().values().size());
		assertEquals("xforms_saveproc1", el.getSaveProcName());
		DataPanelElementProc proc = el.getProcs().get("proc2");
		assertNotNull(proc);
		assertEquals("proc2", proc.getId());
		assertEquals("xforms_submission1", proc.getName());
		assertEquals(DataPanelElementProcType.SUBMISSION, proc.getType());
		assertFalse(el.getNeverShowInPanel());
		el = panel.getTabById("2").getElementInfoById("09");
		assertTrue(el.getNeverShowInPanel());
	}

	/**
	 * Проверка работы функции DataPanelElementInfo.getKeepUserSettings().
	 */
	@Test
	public void testGetKeepUserSettings() {
		final String dataPanelId = "test.xml";
		final String firstElId = "2";
		final String tabId = "2";
		final String secElId = "3";

		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(dataPanelId);
		dpLink.setTabId(tabId);
		action.setDataPanelLink(dpLink);
		DataPanelElementLink elLink =
			new DataPanelElementLink(firstElId, CompositeContext.createCurrent());
		elLink.setKeepUserSettings(true);
		dpLink.getElementLinks().add(elLink);
		elLink = new DataPanelElementLink(secElId, CompositeContext.createCurrent());
		elLink.setKeepUserSettings(false);
		dpLink.getElementLinks().add(elLink);

		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML(dataPanelId);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);

		assertTrue(panel.getTabById(tabId).getElementInfoById(firstElId)
				.getKeepUserSettings(action));
		assertFalse(panel.getTabById(tabId).getElementInfoById(secElId)
				.getKeepUserSettings(action));
	}
}
