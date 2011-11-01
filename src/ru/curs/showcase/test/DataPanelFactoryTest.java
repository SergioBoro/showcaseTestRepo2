package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.SPNotExistsException;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.XSDValidateException;

/**
 * Тесты для фабрики информационных панелей.
 * 
 * @author den
 * 
 */
public class DataPanelFactoryTest extends AbstractTestWithDefaultUserData {

	/**
	 * Проверка значений атрибутов панели по умолчанию.
	 */
	@Test
	public void testCreateDPEI() {
		DataPanelElementInfo dpei = new DataPanelElementInfo("01", DataPanelElementType.GEOMAP);
		assertFalse(dpei.getCacheData());
		assertFalse(dpei.getRefreshByTimer());
		assertEquals(DataPanelElementInfo.DEF_TIMER_INTERVAL, dpei.getRefreshInterval().intValue());
	}

	@Test
	public void testCreateDPWithTable() {
		DataPanel dp = new DataPanel("zzz");
		DataPanelTab tab = dp.add("t01", "Табличная вкладка");
		tab.setLayout(DataPanelTabLayout.TABLE);
		DataPanelTR tr = tab.addTR();
		DataPanelTD td = tr.add();
		DataPanelElementInfo dpei = td.add("elId", DataPanelElementType.CHART);

		DataPanelTab tab2 = dp.add("t02", "Вертикальная вкладка");
		DataPanelElementInfo dpei2 = tab2.addElement("elId2", DataPanelElementType.GEOMAP);

		assertEquals(tab, dpei.getTab());
		assertEquals(tab, tr.getTab());
		assertEquals(tr, td.getTR());
		assertTrue(tab.getTrs().contains(tr));
		assertTrue(tr.getTds().contains(td));
		assertEquals(dpei, td.getElement());
		assertTrue(tab.getElements().isEmpty());

		assertEquals(tab2, dpei2.getTab());
		assertTrue(tab2.getElements().contains(dpei2));
		assertTrue(tab2.getTrs().isEmpty());
	}

	@Test
	public void testRichDP() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), RICH_DP);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);

		assertNotNull(panel);
		DataPanelTab tab = panel.getTabById("01");
		assertEquals(DataPanelTabLayout.TABLE, tab.getLayout());
		assertEquals(2, tab.getTrs().size());
		assertEquals(0, tab.getElements().size());
		DataPanelTR tr = tab.getTrs().get(0);
		assertEquals("500px", tr.getHeight());
		assertEquals("r01", tr.getId());
		assertEquals("border-width: medium", tr.getHtmlAttrs().getStyle());
		assertEquals("css-class", tr.getHtmlAttrs().getStyleClass());
		assertEquals(2, tr.getTds().size());
		DataPanelTD td = tr.getTds().get(1);
		assertEquals("d0102", td.getId());
		assertEquals("300px", td.getWidth());
		assertEquals("500px", td.getHeight());
		assertEquals("border-width: medium", td.getHtmlAttrs().getStyle());
		assertEquals("css-class", td.getHtmlAttrs().getStyleClass());
		assertEquals(2, td.getRowspan().intValue());
		assertNull(td.getColspan());
		DataPanelElementInfo dpei = td.getElement();
		assertNotNull(dpei);

		td = tab.getTrs().get(1).getTds().get(0);
		assertEquals(2, td.getColspan().intValue());
		assertNull(td.getRowspan());
	}

	/**
	 * Основной тест.
	 */
	@Test
	public void testGetData() {
		final int panelsCount = 3;
		final int firstPanelSecondTabElCount = 5;

		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST_XML);
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
		assertEquals("testStyle", el.getHtmlAttrs().getStyleClass());
		assertEquals("dpe_test__1", el.getFullId());
		assertEquals("dpe_test__1_current", el.getKeyForCaching(CompositeContext.createCurrent()));

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
	 * Проверка загрузки процедур XForms.
	 * 
	 */
	@Test
	public void testXFormsProcLoad() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST1_1_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("2").getElementInfoById("0206");
		assertNotNull(el);
		assertEquals(2, el.getProcs().values().size());
		assertEquals("xforms_saveproc1", el.getSaveProc().getName());
		DataPanelElementProc proc = el.getProcs().get("proc2");
		assertNotNull(proc);
		assertEquals("proc2", proc.getId());
		assertEquals("xforms_submission1", proc.getName());
		assertEquals(DataPanelElementProcType.SUBMISSION, proc.getType());
		assertFalse(el.getNeverShowInPanel());
		el = panel.getTabById("2").getElementInfoById("0207");
		assertTrue(el.getNeverShowInPanel());
	}

	/**
	 * Функция проверки считывания списка процедур элемента панели.
	 */
	@Test
	public void testDPProcs() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST1_1_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("2").getElementInfoById("0207");
		assertNotNull(el);
		final int numProc = 8;
		assertEquals(numProc, el.getProcs().values().size());
		DataPanelElementProc proc = el.getProcs().get("proc3");
		assertNull(proc.getTransformName());
		assertNull(proc.getSchemaName());
		proc = el.getProcs().get("proc6");
		assertEquals(DataPanelElementProcType.DOWNLOAD, proc.getType());
		assertEquals(TEST_GOOD_XSL, proc.getTransformName());
		assertEquals("test_good_small.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc7");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertEquals(TEST_GOOD_XSL, proc.getTransformName());
		assertEquals("test_good.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc8");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertNull(proc.getTransformName());
		assertEquals("test_bad.xsd", proc.getSchemaName());
		proc = el.getProcs().get("proc9");
		assertEquals(DataPanelElementProcType.UPLOAD, proc.getType());
		assertEquals(TEST_GOOD_XSL, proc.getTransformName());
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

		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), dataPanelId);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);

		assertTrue(panel.getTabById(tabId).getElementInfoById(firstElId)
				.getKeepUserSettings(action));
		assertFalse(panel.getTabById(tabId).getElementInfoById(secElId)
				.getKeepUserSettings(action));
	}

	/**
	 * Проверка получения метаданных и данных для загрузки элемента раздельно.
	 */
	@Test
	public void testMetadataProc() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST1_1_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("2").getElementInfoById("0201");
		DataPanelElementProc proc = el.getMetadataProc();

		assertNotNull(el);
		assertEquals(DataPanelElementProcType.METADATA, proc.getType());
	}

	@Test
	public void testReadElementWithRelated() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST1_1_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("10").getElementInfoById("1001");
		el.isCorrect();
		assertEquals(2, el.getRelated().size());
		assertEquals("1002", el.getRelated().get(0));
		assertEquals("1003", el.getRelated().get(1));
	}

	@Test
	public void testWrongReadElementWithRelated() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST1_1_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		DataPanelElementInfo el = panel.getTabById("11").getElementInfoById("1101");
		assertFalse(el.isCorrect());
	}

	@Test(expected = XSDValidateException.class)
	public void testWrongReadElementWithRelated2() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), "test.bad2.xml");
		DataPanelFactory factory = new DataPanelFactory();
		factory.fromStream(file);
	}

	@Test(expected = SPNotExistsException.class)
	public void testBySLFromDBSPNotExists() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("dp09031");
		action.setDataPanelLink(dpLink);

		DataPanelSelector selector = new DataPanelSelector(action.getDataPanelLink());
		DataPanelGateway gateway = selector.getGateway();
		try {
			gateway.getRawData(action.getContext());
		} finally {
			gateway.releaseResources();
		}
	}
}
