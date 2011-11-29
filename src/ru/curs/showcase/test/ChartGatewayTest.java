package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.chart.*;

/**
 * Тесты для шлюза данных для графиков.
 * 
 * @author den
 * 
 */
public class ChartGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основная функция тестирования шлюза.
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestChartInfo();

		ChartGateway gateway = new ChartDBGateway();
		gateway.getRawData(context, element);
	}
}
