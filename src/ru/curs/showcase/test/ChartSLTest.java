package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.IncorrectElementException;
import ru.curs.showcase.model.chart.ChartGetCommand;

/**
 * Тесты фабрики графиков.
 * 
 * @author den
 * 
 */
public class ChartSLTest extends AbstractTest {

	/**
	 * Проверка работы адаптера в JSON.
	 */
	@Test
	public void testAdaptChartForJS() {
		CompositeContext context = getTestContext3();
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		Chart chart = command.execute();

		assertNotNull(context.getSession());
		assertNull(chart.getJavaDynamicData());
		assertNotNull(chart.getJsDynamicData());
		assertTrue(chart.getJsDynamicData().startsWith("{"));
		assertTrue(chart.getJsDynamicData().endsWith("}"));
	}

	@Test
	public void testChartCreate() {
		Chart chart = new Chart();

		assertTrue(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement1() throws Throwable {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), element);
		try {
			command.execute();
		} catch (GeneralException e) {
			throw e.getCause();
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement2() throws Throwable {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), element);
		try {
			command.execute();
		} catch (GeneralException e) {
			throw e.getCause();
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongElement3() throws Throwable {
		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), null);
		try {
			command.execute();
		} catch (GeneralException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testJython() {
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(MAIN_CONTEXT_TAG);
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elInfo.setProcName("chart/ChartSimple.py");
		generateTestTabWithElement(elInfo);

		ChartGetCommand command = new ChartGetCommand(context, elInfo);
		Chart chart = command.execute();

		assertNotNull(chart.getJsDynamicData());
	}
}
