package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.InteractionType;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.core.sp.RecordSetElementRawData;
import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String GRIDBAL_TEST_PROPERTIES = "gridbal.test.properties";

	/**
	 * Тестирует задание профайла настроек из хранимой процедуры.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProfileSelection() throws Exception {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "4");

		GridGateway gateway = new GridDBGateway();
		RecordSetElementRawData raw = gateway.getRawDataAndSettings(context, element);
		GridFactory factory = new GridFactory(raw);
		Grid grid = factory.build();
		assertEquals(GRIDBAL_TEST_PROPERTIES, factory.serverState().getProfile());

		assertEquals(1, grid.getDataSet().getRecordSet().getPageNumber());

		ProfileReader gp =
			new ProfileReader(GRIDBAL_TEST_PROPERTIES, SettingsFileType.GRID_PROPERTIES);
		gp.init();

		Boolean defSelectRecord =
			gp.getBoolValue(DefaultGridSettingsApplyStrategy.DEF_SELECT_WHOLE_RECORD);
		assertEquals(defSelectRecord, grid.getUISettings().isSelectOnlyRecords());
		final String fontWidth = "27";
		assertEquals(fontWidth, grid.getDataSet().getRecordSet().getRecords().get(0).getFontSize());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.core.grid.GridFactory#makeSafeXMLAttrValues} .
	 */
	@Test
	public void testGridLinkReplaceXMLServiceSymbols() {
		assertEquals("<link href=\"ya.ru?search=aa&amp;bla&amp;ab\" "
				+ "image=\"xxx.jpg\"  text=\"&lt;&quot; &lt;&gt; &gt; a&apos;&quot;\"  />",
				GridFactory.makeSafeXMLAttrValues("<link href=\"ya.ru?search=aa&amp;bla&ab\" "
						+ "image=\"xxx.jpg\"  text=\"<&quot; &lt;&gt; > a'\"\"  />"));
	}

	@Test
	public void testLoadIDAndCSS() throws Exception {
		GridContext context = new GridContext(getTestContext1());
		context.setIsFirstLoad(true);
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_portals_id_and_css");
		generateTestTabWithElement(elInfo);

		GridGateway gateway = new GridDBGateway();
		RecordSetElementRawData raw = gateway.getRawDataAndSettings(context, elInfo);
		GridFactory factory = new GridFactory(raw);
		Grid grid = factory.build();

		assertNotNull(grid.getAutoSelectRecord());
		final String recId = "77F60A7C-42EB-4E32-B23D-F179E58FB138";
		assertEquals(recId, grid.getAutoSelectRecord().getId());
		assertNotNull(grid.getEventManager().getEventForCell(recId, "URL",
				InteractionType.SINGLE_CLICK));
		assertEquals("grid-record-bold grid-record-italic", grid.getDataSet().getRecordSet()
				.getRecords().get(0).getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG));
	}

	/**
	 * Тест, проверяющий формирование грида на основе xml-датасета.
	 * 
	 */
	@Test
	public void testLoadByXmlDs() {
		final int colCount = 6;
		final int pagesCount = 2;
		final int pageSize = 2;

		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "5", "53");

		GridGetCommand command = new GridGetCommand(context, element, true);
		Grid grid = command.execute();

		assertNotNull(context.getSession());
		assertNotNull(grid);
		assertNotNull(grid.getDataSet());
		assertFalse(grid.getHeader().isEmpty());
		assertTrue(grid.getFooter().isEmpty());
		assertNotNull(grid.getEventManager().getEvents());
		assertTrue(grid.getEventManager().getEvents().size() > 0);

		assertNotNull(grid.getDataSet().getRecordSet());
		assertNotNull(grid.getDataSet().getColumnSet());
		assertEquals(colCount, grid.getDataSet().getColumnSet().getColumns().size());
		Column col = grid.getDataSet().getColumnSet().getColumns().get(0);
		assertEquals(HorizontalAlignment.LEFT, col.getHorizontalAlignment());
		assertNull(col.getFormat());
		col = grid.getDataSet().getColumnSet().getColumns().get(1);
		assertEquals(HorizontalAlignment.CENTER, col.getHorizontalAlignment());
		assertEquals(GridValueType.IMAGE, col.getValueType());
		assertEquals("solutions/default/resources/imagesingrid/test.jpg", grid.getDataSet()
				.getRecordSet().getRecords().get(0).getValue(col));
		col = grid.getDataSet().getColumnSet().getColumns().get(2);

		assertEquals(grid.getDataSet().getRecordSet().getPageSize(), grid.getDataSet()
				.getRecordSet().getRecordsCount());
		assertEquals(2, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());

		assertNull(grid.getDefaultAction());
		assertFalse(grid.getUISettings().isSelectOnlyRecords());

		assertNotNull(grid.getAutoSelectRecord());
		final String recId = "77F60A7C-42EB-4E32-B23D-F179E58FB138";
		assertEquals(recId, grid.getAutoSelectRecord().getId());
		assertNotNull(grid.getEventManager().getEventForCell(recId, "URL",
				InteractionType.SINGLE_CLICK));
		assertEquals("grid-record-bold grid-record-italic", grid.getDataSet().getRecordSet()
				.getRecords().get(0).getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG));

	}

}
