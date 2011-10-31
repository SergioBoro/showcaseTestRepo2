package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.model.geomap.GeoMapGetCommand;

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
	 */
	@Test
	public void testFromDBStaticData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		GeoMapGetCommand command = new GeoMapGetCommand(context, element);
		GeoMap map = command.execute();
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

		assertEquals(ChildPosition.BOTTOM, map.getLegendPosition());
		assertNotNull(map.getJsDynamicData());
		assertNull(map.getJavaDynamicData());

		assertEquals(map.getActionForDependentElements(), map.getDefaultAction());

		assertNotNull(map.getUiSettings());
		assertNotNull(map.getUiSettings().getButtonsPanelPosition());
		assertNotNull(map.getUiSettings().getButtonsPanelVisible());
		assertNotNull(map.getUiSettings().getExportToSVGButtonVisible());
	}

	@Test
	public void testMapWithOutIndicators() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		generateTestTabWithElement(elInfo);
		elInfo.setProcName("geomap_bal_lite");

		GeoMapGetCommand command = new GeoMapGetCommand(context, elInfo);
		command.execute();
	}

	@Test
	public void testReplaceVariables() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		generateTestTabWithElement(elInfo);
		elInfo.setProcName("geomap_func2_ym");

		GeoMapGetCommand command = new GeoMapGetCommand(context, elInfo);
		GeoMap map = command.execute();

		assertTrue(map.getTemplate().indexOf("registerSolutionMap:") == -1);
		assertTrue(map.getTemplate().indexOf("registerModules:") > -1);
		assertTrue(map.getTemplate().indexOf("managerModule:") > -1);
	}

}
