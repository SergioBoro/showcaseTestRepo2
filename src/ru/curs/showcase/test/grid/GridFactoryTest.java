package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.gwt.datagrid.model.Record;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.InteractionType;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.sp.RecordSetElementRawData;
import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.exception.SettingsFileType;

import com.google.gwt.dom.client.Style.Unit;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String GRIDBAL_TEST_PROPERTIES = "gridbal.test.properties";

	/**
	 * Тестирует задание профайла настроек из хранимой процедуры.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProfileSelection() throws Exception {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "4");

		GridGateway gateway = new GridDBGateway();
		RecordSetElementRawData raw = gateway.getRawDataAndSettings(context, element);
		GridDBFactory factory = new GridDBFactory(raw);
		Grid grid = factory.build();
		assertEquals(GRIDBAL_TEST_PROPERTIES, factory.serverState().getProfile());

		assertEquals(1, grid.getDataSet().getRecordSet().getPageNumber());

		ProfileReader gp =
			new ProfileReader(GRIDBAL_TEST_PROPERTIES, SettingsFileType.GRID_PROPERTIES);
		gp.init();

		Boolean defSelectRecord =
			gp.getBoolValue(DefaultGridSettingsApplyStrategy.DEF_SELECT_WHOLE_RECORD);
		assertEquals(defSelectRecord, grid.getUISettings().isSelectOnlyRecords());
		final String fontWidth = "27";
		assertEquals(fontWidth, grid.getDataSet().getRecordSet().getRecords().get(0).getFontSize());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.model.grid.GridDBFactory#replaceXMLServiceSymbols}
	 * .
	 */
	@Test
	public void testGridLinkReplaceXMLServiceSymbols() {
		assertEquals("<link href=\"ya.ru?search=aa&amp;bla&amp;ab\" "
				+ "image=\"xxx.jpg\"  text=\"&lt;&quot; &lt;&gt; &gt; a&apos;&quot;\"  />",
				GridDBFactory.makeSafeXMLAttrValues("<link href=\"ya.ru?search=aa&amp;bla&ab\" "
						+ "image=\"xxx.jpg\"  text=\"<&quot; &lt;&gt; > a'\"\"  />"));
	}

	@Test
	public void testLoadIDAndCSS() throws Exception {
		GridContext context = new GridContext(getTestContext1());
		context.setIsFirstLoad(true);
		DataPanelElementInfo elInfo = new DataPanelElementInfo("01", DataPanelElementType.GRID);
		elInfo.setProcName("grid_portals_id_and_css");
		generateTestTabWithElement(elInfo);

		GridGateway gateway = new GridDBGateway();
		RecordSetElementRawData raw = gateway.getRawDataAndSettings(context, elInfo);
		GridDBFactory factory = new GridDBFactory(raw);
		Grid grid = factory.build();

		assertNotNull(grid.getAutoSelectRecord());
		final String recId = "77F60A7C-42EB-4E32-B23D-F179E58FB138";
		assertEquals(recId, grid.getAutoSelectRecord().getId());
		assertNotNull(grid.getEventManager().getEventForCell(recId, "URL",
				InteractionType.SINGLE_CLICK));
		assertEquals("grid-record-bold grid-record-italic", grid.getDataSet().getRecordSet()
				.getRecords().get(0).getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG));
	}

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void testFontSizeDetermine() {
		Record rec = new Record();

		final String fontSize = "1.1";
		rec.setFontSize(fontSize);
		final double accuracy = 0.01;
		assertEquals(Double.parseDouble(fontSize), rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.EM, rec.getFontSizeUnit());

		rec.setFontSize("1.1em");
		assertEquals(Double.parseDouble(fontSize), rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.EM, rec.getFontSizeUnit());

		rec.setFontSize("12px");
		final int fonSize2 = 12;
		assertEquals(fonSize2, rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.PX, rec.getFontSizeUnit());

		rec.setFontSize("120%");
		final int fonSize3 = 120;
		assertEquals(fonSize3, rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.PCT, rec.getFontSizeUnit());

		try {
			rec.setFontSize("%");
			rec.getFontSizeValue();
		} catch (NumberFormatException e) {
			rec.setFontSize("");
			rec.getFontSizeValue();
		}
		fail();
	}
}
