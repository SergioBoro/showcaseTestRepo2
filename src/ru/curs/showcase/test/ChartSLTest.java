package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;

/**
 * Тесты фабрики графиков.
 * 
 * @author den
 * 
 */
public class ChartSLTest extends AbstractTest {

	/**
	 * Проверка работы адаптера в JSON.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testAdaptChartForJS() throws GeneralException {
		CompositeContext context = getTestContext3();
		DataPanelElementInfo element = getTestChartInfo();

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		Chart chart = serviceLayer.getChart(context, element);

		assertNotNull(context.getSession());
		assertNotNull(chart.getJavaDynamicData());
		assertNotNull(chart.getJsDynamicData());
		assertTrue(chart.getJsDynamicData().startsWith("{"));
		assertTrue(chart.getJsDynamicData().endsWith("}"));
	}
}
