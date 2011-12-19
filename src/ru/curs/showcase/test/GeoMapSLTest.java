package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.IncorrectElementException;
import ru.curs.showcase.model.geomap.GeoMapGetCommand;
import ru.curs.showcase.model.svg.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

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
	public void testJython() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		elInfo.setProcName("geomap/GeoMapSimple.py");
		generateTestTabWithElement(elInfo);

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

	@Test
	public void testSVGGet() throws IOException {
		String inputFile = RU_CURS_SHOWCASE_TEST + GEOMAP_WOHEADER_SVG;
		InputStream is = FileUtils.loadResToStream(inputFile);
		String svg = TextUtils.streamToString(is);
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		SVGGetCommand scommand = new SVGGetCommand(context, new GeoMapExportSettings(), svg);

		OutputStreamDataFile result = scommand.execute();

		assertTrue(result.getTextData().startsWith(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8));
		assertEquals(svg.length() + 1 + XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.length(), result
				.getTextData().length());
	}

	@Test
	public void testPNGGet() throws IOException {
		String inputFile = RU_CURS_SHOWCASE_TEST + GEOMAP_WOHEADER_SVG;
		InputStream is = FileUtils.loadResToStream(inputFile);
		String svg = TextUtils.streamToString(is);
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		PNGGetCommand command = new PNGGetCommand(context, new GeoMapExportSettings(), svg);
		OutputStreamDataFile result = command.execute();

		assertTrue(result.getData().size() > 0);
		assertNull(result.getTextData());
		assertTrue(result.getName().endsWith("png"));
	}

	@Test
	public void testJPGGet() throws IOException {
		String inputFile = RU_CURS_SHOWCASE_TEST + GEOMAP_WOHEADER_SVG;
		InputStream is = FileUtils.loadResToStream(inputFile);
		String svg = TextUtils.streamToString(is);
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		JPGGetCommand command = new JPGGetCommand(context, new GeoMapExportSettings(), svg);
		OutputStreamDataFile result = command.execute();

		assertTrue(result.getData().size() > 0);
		assertNull(result.getTextData());
		assertTrue(result.getName().endsWith("jpg"));
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 */
	@Test
	public void testWrongElement1() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);

		GeoMapGetCommand command = new GeoMapGetCommand(new CompositeContext(), elInfo);
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
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", null);
		elInfo.setProcName("proc");

		GeoMapGetCommand command = new GeoMapGetCommand(new CompositeContext(), elInfo);
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	@Test
	public void testForNull() {
		GeoMapGetCommand command = new GeoMapGetCommand(new CompositeContext(), null);
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

}
