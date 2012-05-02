package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.FontModifier;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.InteractionType;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.test.AbstractTest;

/**
 * Компонентные тесты LiveGrid и его внутренних компонентов.
 * 
 */
public class LiveGridComponentTest extends AbstractTest {

	private static final String HEADER =
		"<h3 class=\"testStyle\" >Потери - Всего зерна, тыс. тонн </h3>";
	private static final String FOOTER =
		"<h3 class=\"testStyle\" >Футер. Потери - Всего зерна, тыс. тонн </h3>";

	private static final String HEADER2 =
		"<h3 class=\"testStyle\" >Новый способ загрузки - отдельные процедуры для METADATA и DATA</h3>";

	private static final int LIVE_INFO_OFFSET = 0;
	private static final int LIVE_INFO_LIMIT = 50;
	private static final int LIVE_INFO_TOTALCOUNT = 81;

	private static final int LIVE_INFO_LIMIT2 = 100;
	private static final int LIVE_INFO_TOTALCOUNT2 = 10430;

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 400;
	private static final Integer UI_SETTINGS_ROW_HEIGHT = 20;

	private static final String COL_ID = "col1";
	private static final String COL_CAPTION = "Регион";

	private static final String COL_CAPTION2 = "_Id";

	private static final String FONT_SIZE = "1em";

	private static final String REC_ID = "9";
	private static final String REC_ID2 = "36";

	private static final int DATA_SIZE = 50;

	@Test
	public void testLiveGridMetadata1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "61");

		LiveGridMetadataGetCommand command = new LiveGridMetadataGetCommand(context, elInfo);
		LiveGridMetadata lgm = command.execute();

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, lgm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT, lgm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT, lgm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, lgm.getUISettings().getRowHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getCaption());
		assertEquals(com.extjs.gxt.ui.client.Style.HorizontalAlignment.LEFT,
				lgm.getColumns().get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION, lgm.getOriginalColumnSet().getColumns().get(0).getId());

		assertEquals(FONT_SIZE, lgm.getFontSize());

		assertNull(lgm.getTextColor());
		assertNull(lgm.getBackgroundColor());
		assertFalse(lgm.getFontModifiers().contains(FontModifier.BOLD));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.ITALIC));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));

	}

	@Test
	public void testLiveGridData1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "61");

		LiveGridDataGetCommand command = new LiveGridDataGetCommand(context, elInfo);
		LiveGridData<LiveGridModel> lgd = command.execute();

		assertEquals(LIVE_INFO_OFFSET, lgd.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT, lgd.getTotalLength());
		assertEquals(DATA_SIZE, lgd.getData().size());

		LiveGridExtradata lge = lgd.getLiveGridExtradata();

		assertNull(lge.getAutoSelectColumnId());
		assertEquals(REC_ID, lge.getAutoSelectRecordId());

		assertEquals("1", lge.getEventManager().getEvents().get(0).getId1().getString());
		assertEquals(InteractionType.SINGLE_CLICK, lge.getEventManager().getEvents().get(0)
				.getInteractionType());

	}

	@Test
	public void testLiveGridMetadata2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "62");

		LiveGridMetadataGetCommand command = new LiveGridMetadataGetCommand(context, elInfo);
		LiveGridMetadata lgm = command.execute();

		assertEquals(HEADER2, lgm.getHeader());
		assertEquals("", lgm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, lgm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT2, lgm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT2, lgm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, lgm.getUISettings().getRowHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION2, lgm.getColumns().get(0).getCaption());
		assertEquals(com.extjs.gxt.ui.client.Style.HorizontalAlignment.RIGHT, lgm.getColumns()
				.get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION2, lgm.getOriginalColumnSet().getColumns().get(0).getId());
	}

	@Test
	public void testLiveGridData2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "62");

		LiveGridDataGetCommand command = new LiveGridDataGetCommand(context, elInfo);
		LiveGridData<LiveGridModel> lgd = command.execute();

		assertEquals(LIVE_INFO_OFFSET, lgd.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT2, lgd.getTotalLength());
		assertEquals(DATA_SIZE, lgd.getData().size());

		LiveGridExtradata lge = lgd.getLiveGridExtradata();

		assertNull(lge.getAutoSelectColumnId());
		assertEquals(REC_ID2, lge.getAutoSelectRecordId());

		assertEquals("2", lge.getEventManager().getEvents().get(1).getId1().getString());
		assertEquals(InteractionType.SINGLE_CLICK, lge.getEventManager().getEvents().get(1)
				.getInteractionType());

	}

}
