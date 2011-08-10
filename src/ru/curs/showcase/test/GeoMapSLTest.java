package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.LegendPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;

/**
 * Тесты для фабрики карт.
 * 
 * @author den
 * 
 */
public class GeoMapSLTest extends AbstractTest {

	/**
	 * Тест на проверку статических свойств карты, созданной на основе данных из
	 * БД.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testFromDBStaticData() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		GeoMap map = serviceLayer.getGeoMap(context, element);
		assertNotNull(context.getSession());
		assertNotNull(map);

		assertNotNull(map.getHeader());
		assertNotNull(map.getFooter());

		Action action = map.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		assertNotNull(action.getDataPanelLink());
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals(context.getMain(), action.getContext().getMain());
		assertEquals(NavigatorActionType.CHANGE_NODE, action.getNavigatorActionType());
		assertNotNull(action.getNavigatorElementLink());
		assertEquals("9EF5F299-0AB3-486B-A810-5818D17047AC", action.getNavigatorElementLink()
				.getId());

		assertEquals(LegendPosition.BOTTOM, map.getLegendPosition());
		assertNotNull(map.getJsDynamicData());
		assertNotNull(map.getJavaDynamicData());

		assertEquals(map.getActionForDependentElements(), map.getDefaultAction());
	}

}
