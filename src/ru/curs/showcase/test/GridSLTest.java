package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.ValidateInDBException;
import ru.curs.showcase.model.grid.GridServerState;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridSLTest extends AbstractTest {

	private static final String NO_DATA_ERROR =
		"Нет данных в процедуре \"grid_cities_data_ec\" (1)";
	private static final Integer CITIES_COUNT = 11111;

	/**
	 * Основной тест для фабрики гридов.
	 */
	@Test
	public void testGetData() throws GeneralException {
		final int colCount = 26;
		final int pagesCount = 6;
		final int pageSize = 15;
		final Integer autoSelectRecord = 16;
		final String precision = "2";

		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, null);

		assertNotNull(context.getSession());
		assertNotNull(grid);
		assertNotNull(grid.getDataSet());
		assertFalse(grid.getHeader().isEmpty());
		assertTrue(grid.getHeader().contains("class=\"testStyle\""));
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
		assertEquals(HorizontalAlignment.RIGHT, col.getHorizontalAlignment());
		assertEquals(precision, col.getFormat());

		assertEquals(grid.getDataSet().getRecordSet().getPageSize(), grid.getDataSet()
				.getRecordSet().getRecordsCount());
		assertEquals(2, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());

		assertNotNull(grid.getDefaultAction());
		List<GridEvent> events =
			grid.getEventManager()
					.getEventForCell(
							autoSelectRecord.toString(),
							grid.getDataSet().getColumnSet().getColumnsByIndex().iterator().next()
									.getId(), InteractionType.SINGLE_CLICK);
		Action firstCellAction = events.get(0).getAction();
		assertEquals(firstCellAction, grid.getActionForDependentElements());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, grid.getDefaultAction()
				.getDataPanelActionType());
		assertEquals(context.getMain(), grid.getDefaultAction().getContext().getMain());
		assertNull(grid.getDefaultAction().getContext().getSession());
		assertNotNull(grid.getAutoSelectRecord());
		assertEquals(autoSelectRecord.toString(), grid.getAutoSelectRecord().getId());

		assertTrue(grid.getUISettings().isSelectOnlyRecords());
		assertFalse(grid.getUISettings().isSingleClickBeforeDoubleClick());
		assertNotNull(grid.getEventManager().getEventForCell(autoSelectRecord.toString(), null,
				InteractionType.SELECTION));
	}

	/**
	 * Тест на обновление грида с измененными параметрами постраничной выборки.
	 */
	@Test
	public void testWithRequestedSettings() throws GeneralException {
		final int maxColIndex = 5;
		final int pageSize = 5;
		final int pageNum = 10;
		final String firstColName = "3кв. 2005г.";
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestGridInfo();

		GridRequestedSettings settings = new GridRequestedSettings();
		settings.setPageNumber(pageNum);
		settings.setPageSize(pageSize);
		addSortedColumn(settings, "3кв. 2007г.", maxColIndex);
		addSortedColumn(settings, "3кв. 2006г.", 1);
		addSortedColumn(settings, firstColName, 0);
		assertNull(settings.getCurrentColumnId());
		assertNull(settings.getCurrentRecordId());
		assertEquals(0, settings.getSelectedRecordIds().size());
		settings.setCurrentColumnId(firstColName);
		settings.setCurrentRecordId("1");
		settings.getSelectedRecordIds().add("1");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, settings);

		assertNotNull(context.getSession());
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getRecordsCount());
		assertEquals(firstColName, settings.getSortedColumns().iterator().next().getId());
		assertEquals(firstColName, settings.getCurrentColumnId());
		assertEquals("1", settings.getCurrentRecordId());
		assertEquals(1, settings.getSelectedRecordIds().size());
		assertEquals("1", settings.getSelectedRecordIds().get(0));
	}

	/**
	 * Проверка работы опции fireGeneralAndConcreteEvents у грида.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testFireGeneralAndConcreteEvents() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "8", "83");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, element, null);

		assertTrue(grid.getEventManager().getFireGeneralAndConcreteEvents());
		List<GridEvent> events =
			grid.getEventManager().getEventForCell("1", "Название", InteractionType.DOUBLE_CLICK);
		assertEquals(2, events.size());
		assertNull(events.get(0).getId2());
		assertNotNull(events.get(1).getId2());
	}

	/**
	 * Проверка получения грида через SL с помощью 2-х процедур.
	 */
	@Test
	public void test2StepGridLoadBySL() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, null);
		assertNotNull(grid);
		final int pageSize = 15;
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		final int pageNum = 3;
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
	}

	/**
	 * Проверка получения грида через SL с помощью 2-х процедур.
	 */
	@Test
	public void test2StepGridLoadBySLWhenUpdate() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridRequestedSettings settings = new GridRequestedSettings();
		Collection<Column> aSortedColumns = new ArrayList<Column>();
		Column col = new Column();
		col.setId("Name");
		col.setSorting(Sorting.ASC);
		col.setWidth("200px");
		aSortedColumns.add(col);
		settings.setSortedColumns(aSortedColumns);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(TEST_SESSION, elInfo, context, state);
		state.setTotalCount(CITIES_COUNT);
		state.setAutoSelectRecordId(2);
		state.setAutoSelectRelativeRecord(true);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, settings);

		assertNotNull(grid);
		final int pageSize = GridRequestedSettings.DEF_PAGE_SIZE_VAL;
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		final int pageNum = 1;
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		final int pagesCount = CITIES_COUNT / GridRequestedSettings.DEF_PAGE_SIZE_VAL + 1;
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());
		assertEquals("2", grid.getAutoSelectRecord().getId());
	}

	/**
	 * Проверка получения грида через SL с помощью 2-х процедур.
	 */
	@Test
	public void test2StepGridLoadBySLWhenUpdateVar2() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridRequestedSettings settings = new GridRequestedSettings();
		final int pageNumber = 2;
		settings.setPageNumber(pageNumber);
		final int pageSize = 10;
		settings.setPageSize(pageSize);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(TEST_SESSION, elInfo, context, state);
		state.setTotalCount(CITIES_COUNT);
		final int autoSelectRecordId = 5;
		state.setAutoSelectRecordId(autoSelectRecordId);
		state.setAutoSelectRelativeRecord(false);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, settings);

		assertNotNull(grid);
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageNumber, grid.getDataSet().getRecordSet().getPageNumber());
		final int pagesCount = CITIES_COUNT / pageSize + 1;
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());
		assertNull(grid.getAutoSelectRecord());
	}

	/**
	 * Проверка получения грида через SL с помощью 2-х процедур с запросом
	 * несуществующих данных.
	 */
	@Test
	public void test2StepGridLoadBySLWhenUpdateOutOfBounds() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridRequestedSettings settings = new GridRequestedSettings();
		final int pageNumber = 200;
		settings.setPageNumber(pageNumber);
		final int pageSize = 100;
		settings.setPageSize(pageSize);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(TEST_SESSION, elInfo, context, state);
		state.setTotalCount(CITIES_COUNT);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, settings);

		assertNotNull(grid);
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageNumber, grid.getDataSet().getRecordSet().getPageNumber());
		final int pagesCount = CITIES_COUNT / pageSize + 1;
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());
		assertEquals(0, grid.getDataSet().getRecordSet().getRecordsCount());
		assertNull(grid.getAutoSelectRecord());
	}

	/**
	 * Проверка обновления грида через SL с помощью 1-й процедуры с запросом
	 * несуществующих данных.
	 */
	@Test
	public void test1StepGridLoadBySLWhenUpdateOutOfBounds() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo();

		GridRequestedSettings settings = new GridRequestedSettings();
		final int pageNumber = 200;
		settings.setPageNumber(pageNumber);
		final int pageSize = 100;
		settings.setPageSize(pageSize);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, settings);

		assertNotNull(grid);
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageNumber, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(0, grid.getDataSet().getRecordSet().getRecordsCount());
	}

	/**
	 * Проверка получения грида через SL с помощью 1-й процедуры с последующим
	 * обновлением.
	 */
	@Test
	public void test1StepGridLoadAndUpdate() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, null);
		final String colId = "Картинка";
		Column col = grid.getColumnById(colId);
		Action defAction = grid.getDefaultAction();

		GridRequestedSettings settings = new GridRequestedSettings();
		final int pageNumber = 200;
		settings.setPageNumber(pageNumber);
		final int pageSize = 100;
		settings.setPageSize(pageSize);
		grid = serviceLayer.getGrid(context, elInfo, settings);

		assertNotNull(grid.getColumnById(colId));
		assertEquals(col.getValueType(), grid.getColumnById(colId).getValueType());
		assertEquals(col.getDisplayMode(), grid.getColumnById(colId).getDisplayMode());
		assertEquals(col.getHorizontalAlignment(), grid.getColumnById(colId)
				.getHorizontalAlignment());
		assertEquals(defAction, grid.getDefaultAction());
	}

	@Test
	public void testErrorByReturnCode1() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0901");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getGrid(context, elInfo, null);
		} catch (GeneralException e) {
			assertEquals(ValidateInDBException.class, e.getCause().getClass());
			ValidateInDBException vid = (ValidateInDBException) e.getCause();
			assertEquals(
					"Ошибка построения XML c метаданными в процедуре \"grid_cities_metadata_ec\" (1)",
					vid.getUserMessage().getText());
			assertEquals("1", vid.getUserMessage().getId());
		}
	}

	@Test
	public void testErrorByReturnCode1ForUpdateCase() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0901");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getGrid(context, elInfo, new GridRequestedSettings());
		} catch (GeneralException e) {
			assertEquals(ValidateInDBException.class, e.getCause().getClass());
			assertEquals(NO_DATA_ERROR, ((ValidateInDBException) e.getCause()).getUserMessage()
					.getText());
		}
	}

	@Test
	public void testErrorByReturnCode2() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0902");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getGrid(context, elInfo, null);
		} catch (GeneralException e) {
			assertEquals(ValidateInDBException.class, e.getCause().getClass());
			ValidateInDBException vid = (ValidateInDBException) e.getCause();
			assertEquals(NO_DATA_ERROR, vid.getUserMessage().getText());
			assertEquals("1", vid.getUserMessage().getId());
		}
	}

	@Test
	public void testErrorByReturnCode3() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0903");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getGrid(context, elInfo, null);
		} catch (GeneralException e) {
			assertEquals(ValidateInDBException.class, e.getCause().getClass());
			ValidateInDBException vid = (ValidateInDBException) e.getCause();
			assertEquals("Нет ничего в процедуре \"grid_cities_one_ec\" (1)", vid.getUserMessage()
					.getText());
			assertEquals("1", vid.getUserMessage().getId());
		}
	}

	private void addSortedColumn(final GridRequestedSettings settings, final String name,
			final int index) {
		Column col = new Column();
		col.setId(name);
		col.setSorting(Sorting.ASC);
		col.setIndex(index);
		settings.getSortedColumns().add(col);
	}

	@Test
	public void testGridNoEventsAndDefAction() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo();
		elInfo.setProcName("grid_bal_noevents");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Grid grid = serviceLayer.getGrid(context, elInfo, null);

		assertTrue(grid.getEventManager().getEvents().isEmpty());
		assertNull(grid.getDefaultAction());
	}
}
