package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.*;

import ru.curs.gwt.datagrid.model.GridValueType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.grid.GridXMLBuilder;
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
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testExportCurrentPage() throws GeneralException {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getTestGridInfo();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element);

		GridXMLBuilder builder = new GridXMLBuilder(grid);
		Document xml = builder.build();

		assertEquals(GridXMLBuilder.TABLE_TAG, xml.getDocumentElement().getNodeName());

		assertEquals(GridXMLBuilder.COLUMN_TAG, xml.getDocumentElement().getFirstChild()
				.getNodeName());
		assertNull(xml.getDocumentElement().getFirstChild().getFirstChild());
		assertNotNull(xml.getDocumentElement().getFirstChild().getAttributes()
				.getNamedItem(GeneralXMLHelper.WIDTH_TAG));

		// header row test above
		NodeList list = xml.getDocumentElement().getElementsByTagName(GridXMLBuilder.ROW_TAG);
		assertTrue(list.getLength() > 0);
		assertEquals(GridValueType.STRING.toStringForExcel(), list.item(0).getFirstChild()
				.getAttributes().getNamedItem(GeneralXMLHelper.TYPE_TAG).getNodeValue());

		assertEquals(GridXMLBuilder.ROW_TAG, xml.getDocumentElement().getLastChild().getNodeName());
		assertEquals(GridXMLBuilder.CELL_TAG, xml.getDocumentElement().getLastChild()
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
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testServiceForExportAll() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		ExcelFile file =
			serviceLayer.generateExcelFromGrid(GridToExcelExportType.ALL, gc, element, null);
		assertNotNull(gc.getSession()); // побочный эффект - нет clone
		assertNotNull(file.getData());
		assertEquals("table.xls", file.getName());
	}

	/**
	 * Тест экспорта текущей страницы используя ServiceLayer.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testServiceForExportCurrent() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();
		GridContext gc = new GridContext(context);
		gc.setPageNumber(1);
		gc.setPageSize(2);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.generateExcelFromGrid(GridToExcelExportType.CURRENTPAGE, gc, element, null);
		assertNotNull(gc.getSession());
	}
}
