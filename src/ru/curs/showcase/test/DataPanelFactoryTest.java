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
	 * Проверка значений атрибутов панели по умолчанию.
	 */
	@Test
	public void testSimpleCreate() {
		DataPanelElementInfo dpei = new DataPanelElementInfo("01", DataPanelElementType.GEOMAP);
		assertFalse(dpei.getCacheData());
		assertFalse(dpei.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, dpei.getRefreshInterval().intValue());
	}

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

		assertEquals("test", panel.getId());
		assertEquals(panelsCount, panel.getTabs().size());
		assertNotNull(panel.getTabById("1"));
		el = panel.getTabById("1").getElementInfoById("1");
		assertFalse(el.getHideOnLoad());
		assertNotNull(el);
		assertEquals(DataPanelElementType.WEBTEXT, el.getType());
		assertEquals("testStyle", el.getStyleClass());
		assertEquals("dpe_test_1", el.getFullId());
		assertEquals("dpe_test_1_current", el.getKeyForCaching(CompositeContext.createCurrent()));

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

		el = tab.getElementInfoById("05");
		assertNotNull(el);
		assertEquals(DataPanelElementType.GEOMAP, el.getType());
		assertTrue(el.getCacheData());
		assertTrue(el.getRefreshByTimer());
		final int refreshInterval = 120;
		assertEquals(refreshInterval, el.getRefreshInterval().intValue());
	}

	/**
	 * Метод, эмулирующий вызов GWT на уровне сервера.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testBySL() throws GeneralServerException {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
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
		assertEquals("xforms_saveproc1", el.getSaveProc().getName());
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
	 * Функция проверки считывания списка процедур элемента панели.
	 */
	@Test
	public void testDPProcs() {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML("test1.1.xml");
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("2").getElementInfoById("09");
		assertNotNull(el);
		final int numProc = 8;
		assertEquals(numProc, el.getProcs().values().size());
		DataPanelElementProc proc = el.getProcs().get("proc3");
		assertNull(proc.getTransformName());
		assertNull(proc.getSchemaName());
		proc = el.getProcs().get("proc6");
		assertEquals(DataPanelElementProcType.DOWNLOAD, proc.getType());
		assertEquals("test_good.xsl", proc.getTransformName());
		assertEquals("test_good_small.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc7");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertEquals("test_good.xsl", proc.getTransformName());
		assertEquals("test_good.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc8");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertNull(proc.getTransformName());
		assertEquals("test_bad.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc9");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertEquals("test_good.xsl", proc.getTransformName());
		assertNull(proc.getSchemaName());
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
