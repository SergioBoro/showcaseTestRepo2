package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.*;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.core.chart.*;
import ru.curs.showcase.core.grid.GridGetCommand;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.core.html.xform.XFormGetCommand;
import ru.curs.showcase.core.primelements.navigator.NavigatorGetCommand;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Тесты работы с PostgreSQL.
 * 
 */
public class PostgreSQLTest extends AbstractTest {

	private static final String PG_USERDATA = "pg";
	private static final String PG_XML = "a.xml";

	private static final Integer CITIES_COUNT = 10_428;
	private static final String FIRST_COL_CAPTION = "3кв. 2005г.";
	private static final String SELECTOR_COL_FIRST_VALUE =
		"Запасы на конец отчетного периода - Всего";
	private static final String FIRST_PERIOD_CAPTION = "Период 1";

	private void setPGUserData() {
		AppInfoSingleton.getAppInfo().setCurUserDataId(PG_USERDATA);
	}

	@Before
	public void setUp() {
		setPGUserData();
	}

	/**
	 * Проверка получения навигатора.
	 */
	@Test
	public void testNavigator() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator nav = command.execute();
		final int groupsCount = 2;
		assertEquals(groupsCount, nav.getGroups().size());
		final int elementsCount = 1;
		assertEquals(elementsCount, nav.getGroups().get(0).getElements().size());
		final int subElementsCount = 8;
		assertEquals(subElementsCount, nav.getGroups().get(0).getElements().get(0).getElements()
				.size());
	}

	/**
	 * Проверка получения вебтекста.
	 */
	@Test
	public void testWebText() {
		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(PG_XML, "3", "77");

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		assertNotNull(context.getSession());
		assertEquals(1, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	/**
	 * Проверка получения XForm.
	 */
	@Test
	public void testXForm() {
		XFormContext xcontext = new XFormContext(getTestContext1());
		xcontext.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(PG_XML, "6", "61");

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xforms = command.execute();

		assertNotNull(xcontext.getSession());
		Action action = xforms.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xforms.getEventManager().getEvents().size());
		action = xforms.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		assertNotNull(xforms.getXFormParts());
		assertTrue(xforms.getXFormParts().size() > 0);
	}

	/**
	 * Проверка получения грида с помощью 2-х процедур.
	 */
	@Test
	public void testGrid2Proc() {
		GridContext context = getTestGridContext1();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo elInfo = getDPElement(PG_XML, "41", "0401");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();

		assertNotNull(grid);
		final int pageSize = 15;
		assertEquals(pageSize, grid.getDataSet().getRecordSet().getPageSize());
		final int pageNum = 3;
		assertEquals(pageNum, grid.getDataSet().getRecordSet().getPageNumber());
		final int pagesCount = CITIES_COUNT / pageSize + 1;
		assertEquals(pagesCount, grid.getDataSet().getRecordSet().getPagesTotal());

	}

	/**
	 * Проверка получения грида на основе xml-датасета.
	 * 
	 */
	@Test
	public void testGridXmlDs() {
		final int colCount = 6;
		final int pagesCount = 2;
		final int pageSize = 2;

		GridContext context = getTestGridContext1();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(PG_XML, "42", "0201");

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
		assertEquals("solutions/pg/resources/imagesingrid/test.jpg", grid.getDataSet()
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

	/**
	 * Проверка получения графика на основе xml-датасета.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testChartXmlDs() throws Exception {
		final int seriesCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(PG_XML, "5", "0");

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertFalse(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertTrue(chart.getTemplate().length() > 0);
		assertEquals(defaultWidth, chart.getJavaDynamicData().getWidth().intValue());
		assertEquals(defaultHeight, chart.getJavaDynamicData().getHeight().intValue());
		assertTrue(chart.getJavaDynamicData().getLabelsX().size() > 0);
		assertEquals(labelyCount, chart.getJavaDynamicData().getLabelsY().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(FIRST_COL_CAPTION, chart.getJavaDynamicData().getLabelsX().get(1).getText());
		assertEquals(defaultPos, chart.getLegendPosition());
		assertEquals(SELECTOR_COL_FIRST_VALUE, chart.getJavaDynamicData().getSeries().get(0)
				.getName());
		assertNotNull(chart.getEventManager().getEvents());
		assertTrue(chart.getEventManager().getEvents().size() > 0);

		assertNull(chart.getDefaultAction());

		Event event = chart.getEventManager().getEvents().get(0);
		assertEquals(InteractionType.SINGLE_CLICK, event.getInteractionType());
		assertNull(event.getId2());
		assertEquals(SELECTOR_COL_FIRST_VALUE, event.getId1().getString());

		assertNotNull(event.getAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, event.getAction()
				.getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, event.getAction().getNavigatorActionType());
		CompositeContext calcContext = element.getContext(event.getAction());
		assertNotNull(calcContext);
		assertEquals(context.getMain(), calcContext.getMain());
		assertNull(calcContext.getSession());

		assertEquals("#0000FF", chart.getJavaDynamicData().getSeries().get(0).getColor());

		ChartData data = chart.getJavaDynamicData();
		ChartSeriesValue value = data.getSeries().get(0).getData().get(0);
		assertEquals(data.getLabelsX().get(1).getText(), value.getLegend());
	}

	/**
	 * Проверка получения графика на основе xml-датасета, если данные
	 * транспонированы.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testChartXmlDsFliped() throws Exception {
		final int seriesCount = 24;
		final int labelsXCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(PG_XML, "51", "051");

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertFalse(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertTrue(chart.getTemplate().length() > 0);
		assertEquals(defaultWidth, chart.getJavaDynamicData().getWidth().intValue());
		assertEquals(defaultHeight, chart.getJavaDynamicData().getHeight().intValue());
		assertTrue(chart.getJavaDynamicData().getLabelsX().size() > 0);
		assertEquals(labelyCount, chart.getJavaDynamicData().getLabelsY().size());
		assertEquals(defaultPos, chart.getLegendPosition());
		assertNotNull(chart.getEventManager().getEvents());
		assertEquals(0, chart.getEventManager().getEvents().size());

		assertNull(chart.getDefaultAction());
		assertNotNull(chart.getJavaDynamicData().getSeriesById(FIRST_COL_CAPTION));
		assertEquals(labelsXCount + 1, chart.getJavaDynamicData().getLabelsX().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(FIRST_PERIOD_CAPTION, chart.getJavaDynamicData().getLabelsX().get(1)
				.getText());

	}

}
