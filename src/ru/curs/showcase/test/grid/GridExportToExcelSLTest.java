package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.*;
import org.w3c.dom.*;

import ru.curs.gwt.datagrid.model.GridValueType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.ExcelFile;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Тесты для функции экспорта в Excel из грида.
 * 
 * @author den
 * 
 */
public class GridExportToExcelSLTest extends AbstractTest {
	/**
	 * Тест экспорта данных из текущей страницы.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testExportCurrentPage() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getTestGridInfo();

		GridGetCommand command = new GridGetCommand(context, element, true);
		Grid grid = command.execute();

		GridToExcelXMLFactory builder = new GridToExcelXMLFactory(grid);
		Document xml = builder.build();

		assertEquals(GridToExcelXMLFactory.TABLE_TAG, xml.getDocumentElement().getNodeName());

		assertEquals(GridToExcelXMLFactory.COLUMN_TAG, xml.getDocumentElement().getFirstChild()
				.getNodeName());
		assertNull(xml.getDocumentElement().getFirstChild().getFirstChild());
		assertNotNull(xml.getDocumentElement().getFirstChild().getAttributes()
				.getNamedItem(GeneralXMLHelper.WIDTH_TAG));

		// header row test above
		NodeList list =
			xml.getDocumentElement().getElementsByTagName(GridToExcelXMLFactory.ROW_TAG);
		assertTrue(list.getLength() > 0);
		assertEquals(GridValueType.STRING.toStringForExcel(), list.item(0).getFirstChild()
				.getAttributes().getNamedItem(GeneralXMLHelper.TYPE_TAG).getNodeValue());

		assertEquals(GridToExcelXMLFactory.ROW_TAG, xml.getDocumentElement().getLastChild()
				.getNodeName());
		assertEquals(GridToExcelXMLFactory.CELL_TAG, xml.getDocumentElement().getLastChild()
				.getFirstChild().getNodeName());
		assertNotNull(xml.getDocumentElement().getLastChild().getFirstChild().getFirstChild()
				.getNodeValue());
		assertNotNull(xml.getDocumentElement().getLastChild().getFirstChild().getAttributes()
				.getNamedItem(GeneralXMLHelper.TYPE_TAG));
		assertEquals(GridValueType.STRING.toStringForExcel(),
				xml.getDocumentElement().getLastChild().getFirstChild().getAttributes()
						.getNamedItem(GeneralXMLHelper.TYPE_TAG).getNodeValue());
		assertEquals(GridValueType.INT.toStringForExcel(), xml.getDocumentElement().getLastChild()
				.getLastChild().getAttributes().getNamedItem(GeneralXMLHelper.TYPE_TAG)
				.getNodeValue());
	}

	/**
	 * Тест экспорта всех страниц используя ServiceLayer.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testServiceForExportAll() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);

		GridExcelExportCommand command =
			new GridExcelExportCommand(gc, element, GridToExcelExportType.ALL);
		ExcelFile file = command.execute();

		assertNotNull(gc.getSession()); // побочный эффект - нет clone
		assertNotNull(file.getData());
		assertEquals("table.xls", file.getName());
	}

	/**
	 * Тест экспорта текущей страницы используя ServiceLayer.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testServiceForExportCurrent() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);
		gc.setPageNumber(1);
		gc.setPageSize(2);

		GridExcelExportCommand command =
			new GridExcelExportCommand(gc, element, GridToExcelExportType.CURRENTPAGE);
		command.execute();

		assertNotNull(gc.getSession());
	}
}
