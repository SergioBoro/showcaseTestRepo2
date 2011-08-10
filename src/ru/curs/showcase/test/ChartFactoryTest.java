package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.LegendPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.model.chart.*;

/**
 * Тесты фабрики графиков.
 * 
 * @author den
 * 
 */
public class ChartFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String FIRST_COL_CAPTION = "3кв. 2005г.";
	private static final String SELECTOR_COL_FIRST_VALUE =
		"Запасы на конец отчетного периода - Всего";

	/**
	 * Основной тест.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testGetData() throws Exception {
		final int seriesCount = 9;
		final LegendPosition defaultPos = LegendPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 200;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement("test2.xml", "2", "22");

		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(context, element);
		ChartDBFactory factory = new ChartDBFactory(raw);
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
		assertEquals(0, chart.getEventManager().getEvents().size());
	}

	/**
	 * Тест, проверяющий возврат событий для графика.
	 * 
	 */
	@Test
	public void testGetEventsAndColors() throws Exception {
		final String seriesName = "Алтайский край";
		final String secondGridId = "4";

		CompositeContext context = getTestContext3();
		// график со второй вкладки в панели a.xml
		DataPanelElementInfo element = getTestChartInfo();

		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(context, element);
		ChartDBFactory factory = new ChartDBFactory(raw);
		Chart chart = factory.build();

		assertNotNull(chart.getDefaultAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, chart.getDefaultAction()
				.getDataPanelActionType());
		// assertTrue();
		assertEquals(secondGridId, chart.getDefaultAction().getDataPanelLink().getElementLinks()
				.get(0).getId());
		assertNotNull(chart.getEventManager().getEvents());
		assertTrue(chart.getEventManager().getEvents().size() > 0);

		Event event = chart.getEventManager().getEvents().get(0);
		assertEquals(InteractionType.SINGLE_CLICK, event.getInteractionType());
		assertNull(event.getId2());
		assertEquals(seriesName, event.getId1());

		assertNotNull(event.getAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, event.getAction()
				.getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, event.getAction().getNavigatorActionType());
		CompositeContext calcContext = element.getContext(event.getAction());
		assertNotNull(calcContext);
		assertEquals(context.getMain(), calcContext.getMain());
		assertNull(calcContext.getSession());
		assertFalse(event.getAction().getDataPanelLink().getElementLinks().get(0)
				.getRefreshContextOnly());

		// второй грид со второй вкладки в панели a.xml
		DataPanelElementInfo secondGrid = getDPElement("test.xml", "2", secondGridId);
		calcContext = secondGrid.getContext(event.getAction());
		assertNotNull(calcContext);
		assertEquals(context, calcContext);

		// проверяем цвета
		assertEquals("#00FFFF", chart.getJavaDynamicData().getSeries().get(0).getColor());
	}

	/**
	 * Тест, проверяющий возврат подписей для графика.
	 * 
	 */
	@Test
	public void testGetHints() throws Exception {
		CompositeContext context = getTestContext3();
		// график со второй вкладки в панели a.xml
		DataPanelElementInfo element = getTestChartInfo();

		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(context, element);
		ChartDBFactory factory = new ChartDBFactory(raw);
		Chart chart = factory.build();
		ChartData data = chart.getJavaDynamicData();
		ChartSeriesValue value = data.getSeries().get(0).getData().get(0);
		assertEquals(
				String.format("%d (%s): %s", 1, data.getLabelsX().get(1).getText(), value.getY()),
				value.getTooltip());
		assertEquals(data.getLabelsX().get(1).getText(), value.getLegend());
	}

	/**
	 * Проверка работы фабрики, если данные транспонированы.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFlipedData() throws Exception {
		final int seriesCount = 24;
		final int labelsXCount = 9;
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement("test2.xml", "2", "210");

		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(context, element);
		ChartDBFactory factory = new ChartDBFactory(raw);
		Chart chart = factory.build();

		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertEquals(FIRST_COL_CAPTION, chart.getJavaDynamicData().getSeries().get(0).getName());
		assertEquals(labelsXCount + 1, chart.getJavaDynamicData().getLabelsX().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(SELECTOR_COL_FIRST_VALUE, chart.getJavaDynamicData().getLabelsX().get(1)
				.getText());
		assertEquals(0, chart.getJavaDynamicData().getLabelsY().size());
		assertNotNull(chart.getEventManager().getEvents());
	}
}
