package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.*;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridSLTest extends AbstractTest {

	private static final Integer CITIES_COUNT = 11_111;

	/**
	 * Основной тест для фабрики гридов.
	 */
	@Test
	public void testGetData() {
		final int colCount = 26;
		final int pagesCount = 6;
		final int pageSize = 15;
		final Integer autoSelectRecord = 16;
		final String precision = "2";

		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getTestGridInfo();

		GridGetCommand command = new GridGetCommand(context, element, true);
		Grid grid = command.execute();

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
	public void testWithRequestedSettings() {
		final int pageSize = 5;
		final int pageNum = 10;
		final String firstColName = "3кв. 2005г.";
		DataPanelElementInfo element = getTestGridInfo();

		GridContext gc = generateReloadContextForGridBalProc(pageSize, pageNum, firstColName);

		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		AppInfoSingleton.getAppInfo().storeElementState(SessionUtils.TEST_SESSION, element, gc,
				generateTestGridServerState());

		GridGetCommand command = new GridGetCommand(gc, element, true);
		Grid grid = command.execute();

		assertNotNull(gc.getSession());
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getRecordsCount());
		assertEquals(firstColName, gc.getSortedColumns().iterator().next().getId());
		assertEquals(firstColName, gc.getCurrentColumnId());
		assertEquals("1", gc.getCurrentRecordId());
		assertEquals(1, gc.getSelectedRecordIds().size());
		assertEquals("1", gc.getSelectedRecordIds().get(0));
	}

	/**
	 * Проверка работы опции fireGeneralAndConcreteEvents у грида.
	 */
	@Test
	public void testFireGeneralAndConcreteEvents() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "8", "83");

		GridGetCommand command = new GridGetCommand(context, element, true);
		Grid grid = command.execute();

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
	public void test2StepGridLoadBySL() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
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
	public void test2StepGridLoadBySLWhenUpdate() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridContext gc = new GridContext();
		List<Column> aSortedColumns = new ArrayList<>();
		Column col = new Column();
		col.setId("Name");
		col.setSorting(Sorting.ASC);
		col.setWidth("200px");
		aSortedColumns.add(col);
		gc.setSortedColumns(aSortedColumns);
		gc.apply(context);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		state.setTotalCount(CITIES_COUNT);
		state.setAutoSelectRecordId(2);
		state.setAutoSelectRelativeRecord(true);

		AppInfoSingleton.getAppInfo().storeElementState(ServletUtils.TEST_SESSION, elInfo,
				context, state);

		GridGetCommand command = new GridGetCommand(gc, elInfo, true);
		Grid grid = command.execute();

		assertNotNull(grid);
		final int pageSize = GridContext.DEF_PAGE_SIZE_VAL;
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		final int pageNum = 1;
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		final int pagesCount = CITIES_COUNT / GridContext.DEF_PAGE_SIZE_VAL + 1;
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());
		assertEquals(state.getAutoSelectRecordId().toString(), grid.getAutoSelectRecord().getId());
	}

	/**
	 * Проверка получения грида через SL с помощью 2-х процедур.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test2StepGridLoadBySLWhenUpdateVar2() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridContext gc = new GridContext();
		final int pageNumber = 2;
		gc.setPageNumber(pageNumber);
		final int pageSize = 10;
		gc.setPageSize(pageSize);
		gc.apply(context);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		state.setTotalCount(CITIES_COUNT);
		final int autoSelectRecordId = 5;
		state.setAutoSelectRecordId(autoSelectRecordId);
		state.setAutoSelectRelativeRecord(false);
		AppInfoSingleton.getAppInfo().storeElementState(ServletUtils.TEST_SESSION, elInfo,
				context, state);

		GridGetCommand command = new GridGetCommand(gc, elInfo, true);
		Grid grid = command.execute();

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
	public void test2StepGridLoadBySLWhenUpdateOutOfBounds() {
		DataPanelElementInfo elInfo = getTestGridInfo2();

		final int pageSize = 100;
		final int pageNumber = 200;
		CompositeContext context = getTestContext1();
		GridContext gc = new GridContext();
		gc.setPageNumber(pageNumber);
		gc.setPageSize(pageSize);
		gc.apply(context);

		setDefaultUserData();
		GridServerState state = new GridServerState();
		state.setTotalCount(CITIES_COUNT);

		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		AppInfoSingleton.getAppInfo().storeElementState(ServletUtils.TEST_SESSION, elInfo,
				context, state);

		GridGetCommand command = new GridGetCommand(gc, elInfo, true);
		Grid grid = command.execute();

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
	public void test1StepGridLoadWhenUpdateOutOfBounds() {
		DataPanelElementInfo elInfo = getTestGridInfo();
		final int pageSize = 100;
		final int pageNumber = 200;
		CompositeContext context = getTestContext1();
		GridContext gc = new GridContext();
		gc.setPageNumber(pageNumber);
		gc.setPageSize(pageSize);
		gc.apply(context);

		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		AppInfoSingleton.getAppInfo().storeElementState(SessionUtils.TEST_SESSION, elInfo, gc,
				generateTestGridServerState());

		GridGetCommand command = new GridGetCommand(gc, elInfo, true);
		Grid grid = command.execute();

		assertNotNull(grid);
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		assertEquals(pageNumber, grid.getDataSet().getRecordSet().getPageNumber());
		assertEquals(0, grid.getDataSet().getRecordSet().getRecordsCount());
	}

	private GridServerState generateTestGridServerState() {
		GridServerState state = new GridServerState();
		state.setProfile("default.properties");
		final int totalCount = 81;
		state.setTotalCount(totalCount);
		return state;
	}

	/**
	 * Проверка получения грида через SL с помощью 1-й процедуры с последующим
	 * обновлением.
	 */
	@Test
	public void test1StepGridLoadAndUpdate() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getTestGridInfo();

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		final String colId = "Картинка";
		Column col = grid.getColumnById(colId);
		Action defAction = grid.getDefaultAction();

		final int pageSize = 100;
		final int pageNumber = 200;
		GridContext gc = new GridContext();
		gc.setPageNumber(pageNumber);
		gc.setPageSize(pageSize);
		gc.apply(context);
		command = new GridGetCommand(gc, elInfo, true);
		grid = command.execute();

		assertNotNull(grid.getColumnById(colId));
		assertEquals(col.getValueType(), grid.getColumnById(colId).getValueType());
		assertEquals(col.getDisplayMode(), grid.getColumnById(colId).getDisplayMode());
		assertEquals(col.getHorizontalAlignment(), grid.getColumnById(colId)
				.getHorizontalAlignment());
		assertEquals(defAction, grid.getDefaultAction());
	}

	@Test
	public void testErrorByReturnCode1() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0901");

		try {
			GridGetCommand command = new GridGetCommand(context, elInfo, true);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(ValidateException.class, e.getCause().getClass());
			ValidateException vid = (ValidateException) e.getCause();
			assertEquals(
					"Ошибка построения XML c метаданными в процедуре \"grid_cities_metadata_ec\" (1)",
					vid.getUserMessage().getText());
			assertEquals("1", vid.getUserMessage().getId());
		}
	}

	@Test
	public void testErrorByReturnCode2() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getDPElement(TEST1_1_XML, "09", "0903");

		try {
			GridGetCommand command = new GridGetCommand(context, elInfo, true);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(ValidateException.class, e.getCause().getClass());
			ValidateException vid = (ValidateException) e.getCause();
			assertEquals("Нет ничего в процедуре \"grid_cities_one_ec\" (1)", vid.getUserMessage()
					.getText());
			assertEquals("1", vid.getUserMessage().getId());
		}
	}

	@Test
	public void testGridNoEventsAndDefAction() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getTestGridInfo();
		elInfo.setProcName("grid_bal_noevents");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();

		assertTrue(grid.getEventManager().getEvents().isEmpty());
		assertNull(grid.getDefaultAction());
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test
	public void testWrongElement1() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GRID);

		GridGetCommand command = new GridGetCommand(new GridContext(), elInfo, true);
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test
	public void testWrongElement2() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elInfo.setProcName("proc");

		GridGetCommand command = new GridGetCommand(new GridContext(), elInfo, true);
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test
	public void testWrongElement3() {
		GridGetCommand command = new GridGetCommand(new GridContext(), null, true);
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	@Test
	public void testGridEstimate() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		long estimateSize = grid.sizeEstimate();
		assertTrue(estimateSize > 0);
		assertTrue(ReflectionUtils.getObjectSizeBySerialize(grid) > 0);
	}

	@Test
	public void testGridFileDownloadCommand() {
		ID linkId = new ID("grid_download1");
		String recordId = "1";
		GridContext context = getTestGridContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_download_load");
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(linkId);
		proc.setName("grid_download1");
		elInfo.getProcs().put(linkId, proc);
		generateTestTabWithElement(elInfo);
		GridFileDownloadCommand command =
			new GridFileDownloadCommand(context, elInfo, linkId, recordId);
		OutputStreamDataFile file = command.execute();

		assertNotNull(file.getData());
		assertNotNull(file.getName());
		assertEquals(TextUtils.JDBC_ENCODING, file.getEncoding());
	}

	@Test
	public void gridWithTwoProcsShouldReloadWithoutExceptionsIfStateAbsentInCache() {
		GridContext context = new GridContext(getTestContext1());
		context.setIsFirstLoad(false);
		context.setCurrentRecordId("01");
		context.setPageInfo(new PageInfo(2, 2));
		context.setCurrentColumnId("Name");

		DataPanelElementInfo element = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		element.addDataAndMetaDataProcs("grid_cities");
		generateTestTabWithElement(element);
		element.getRelated().add(element.getId());

		GridGetCommand command = new GridGetCommand(context, element, true);
		command.execute();
	}
}
