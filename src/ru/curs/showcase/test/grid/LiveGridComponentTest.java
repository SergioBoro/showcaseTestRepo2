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
	public void testExtGridMetadata1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "61");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		ExtGridMetadata egm = GridTransformer.gridToExtGridMetadata(grid);

		assertEquals(HEADER, egm.getHeader());
		assertEquals(FOOTER, egm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, egm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT, egm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT, egm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, egm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, egm.getUISettings().getRowHeight());

		assertEquals(COL_ID, egm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION, egm.getColumns().get(0).getCaption());
		assertEquals(com.extjs.gxt.ui.client.Style.HorizontalAlignment.LEFT,
				egm.getColumns().get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION, egm.getOriginalColumnSet().getColumns().get(0).getId());

		assertEquals(FONT_SIZE, egm.getFontSize());

		assertNull(egm.getTextColor());
		assertNull(egm.getBackgroundColor());
		assertFalse(egm.getFontModifiers().contains(FontModifier.BOLD));
		assertFalse(egm.getFontModifiers().contains(FontModifier.ITALIC));
		assertFalse(egm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertFalse(egm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));

	}

	@Test
	public void testExtGridData1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "61");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		ExtGridPagingLoadResult<ExtGridData> egplr = GridTransformer.gridToExtGridData(grid);

		assertEquals(LIVE_INFO_OFFSET, egplr.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT, egplr.getTotalLength());
		assertEquals(DATA_SIZE, egplr.getData().size());

		ExtGridExtradata ege = egplr.getExtGridExtradata();

		assertNull(ege.getAutoSelectColumnId());
		assertEquals(REC_ID, ege.getAutoSelectRecordId());

		assertEquals("1", ege.getEventManager().getEvents().get(0).getId1().getString());
		assertEquals(InteractionType.SINGLE_CLICK, ege.getEventManager().getEvents().get(0)
				.getInteractionType());

	}

	@Test
	public void testExtGridMetadata2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "62");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		ExtGridMetadata egm = GridTransformer.gridToExtGridMetadata(grid);

		assertEquals(HEADER2, egm.getHeader());
		assertEquals("", egm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, egm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT2, egm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT2, egm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, egm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, egm.getUISettings().getRowHeight());

		assertEquals(COL_ID, egm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION2, egm.getColumns().get(0).getCaption());
		assertEquals(com.extjs.gxt.ui.client.Style.HorizontalAlignment.RIGHT, egm.getColumns()
				.get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION2, egm.getOriginalColumnSet().getColumns().get(0).getId());
	}

	@Test
	public void testExtGridData2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "6", "62");

		GridGetCommand command = new GridGetCommand(context, elInfo, true);
		Grid grid = command.execute();
		ExtGridPagingLoadResult<ExtGridData> egplr = GridTransformer.gridToExtGridData(grid);

		assertEquals(LIVE_INFO_OFFSET, egplr.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT2, egplr.getTotalLength());
		assertEquals(DATA_SIZE, egplr.getData().size());

		ExtGridExtradata ege = egplr.getExtGridExtradata();

		assertNull(ege.getAutoSelectColumnId());
		assertEquals(REC_ID2, ege.getAutoSelectRecordId());

		assertEquals("2", ege.getEventManager().getEvents().get(1).getId1().getString());
		assertEquals(InteractionType.SINGLE_CLICK, ege.getEventManager().getEvents().get(1)
				.getInteractionType());

	}

}
