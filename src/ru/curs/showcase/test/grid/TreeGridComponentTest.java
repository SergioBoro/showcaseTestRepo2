package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.InteractionType;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.test.AbstractTest;

/**
 * Компонентные тесты TreeGrid и его внутренних компонентов.
 * 
 */
public class TreeGridComponentTest extends AbstractTest {

	private static final String HEADER =
		"<h3 class=\"testStyle\" >Потери - Всего зерна, тыс. тонн </h3>";
	private static final String FOOTER =
		"<h3 class=\"testStyle\" >Футер. Потери - Всего зерна, тыс. тонн </h3>";

	private static final String HEADER2 = "<h3 class=\"testStyle\" >Хедер tree-грида</h3>";
	private static final String FOOTER2 = "<h3 class=\"testStyle\" >Футер tree-грида</h3>";

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 400;
	private static final Integer UI_SETTINGS_GRID_HEIGHT2 = 500;

	private static final String COL_ID = "col1";
	private static final String COL_CAPTION = "Регион";

	private static final String COL_CAPTION2 = "Название";

	private static final String FONT_SIZE = "1em";

	private static final String REC_ID = "9";
	private static final String REC_ID21 = "DF30DB9C-7DDF-48F8-9CD5-9E22F67FB45B";
	private static final String REC_ID22 = "A40012C8-8CBC-4B4E-B856-66F03DEFF041";

	private static final int DATA_SIZE = 50;
	private static final int DATA_SIZE21 = 21;
	private static final int DATA_SIZE22 = 15;

	private static final String EVENT_ID21 = "1744A518-B5EA-49EF-BC7C-12BD84E87FDB";
	private static final String EVENT_ID22 = "4766EEA0-5B02-46C0-A118-09ADD294DE44";

	private static final String PARENT_ID22 = "AFAF2D58-7016-4A0B-B228-8DC765444A37";

	@Test
	public void testTreeGridMetadata1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "71");

		LiveGridMetadataGetCommand command = new LiveGridMetadataGetCommand(context, elInfo);
		LiveGridMetadata lgm = command.execute();

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.LEFT, lgm.getColumns().get(0).getHorizontalAlignment());
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
	public void testTreeGridData1Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "71");

		TreeGridDataGetCommand command = new TreeGridDataGetCommand(context, elInfo);
		List<TreeGridModel> tgd = command.execute();

		assertEquals(DATA_SIZE, tgd.size());

		LiveGridExtradata lge = ((TreeGridData<TreeGridModel>) tgd).getLiveGridExtradata();

		assertNull(lge.getAutoSelectColumnId());
		assertEquals(REC_ID, lge.getAutoSelectRecordId());

		assertEquals("1", lge.getEventManager().getEvents().get(0).getId1().getString());
		assertEquals(InteractionType.SINGLE_CLICK, lge.getEventManager().getEvents().get(0)
				.getInteractionType());

	}

	@Test
	public void testTreeGridMetadata2Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "72");

		LiveGridMetadataGetCommand command = new LiveGridMetadataGetCommand(context, elInfo);
		LiveGridMetadata lgm = command.execute();

		assertEquals(HEADER2, lgm.getHeader());
		assertEquals(FOOTER2, lgm.getFooter());

		assertEquals(UI_SETTINGS_GRID_HEIGHT2, lgm.getUISettings().getGridHeight());

		assertEquals(COL_ID, lgm.getColumns().get(0).getId());
		assertEquals(COL_CAPTION2, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.LEFT, lgm.getColumns().get(0).getHorizontalAlignment());
		assertEquals(COL_CAPTION2, lgm.getOriginalColumnSet().getColumns().get(0).getId());
	}

	@Test
	public void testTreeGridData21Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		context.setParentId(null);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "72");

		TreeGridDataGetCommand command = new TreeGridDataGetCommand(context, elInfo);
		List<TreeGridModel> tgd = command.execute();

		assertEquals(DATA_SIZE21, tgd.size());

		LiveGridExtradata lge = ((TreeGridData<TreeGridModel>) tgd).getLiveGridExtradata();

		assertNull(lge.getAutoSelectColumnId());
		assertEquals(REC_ID21, lge.getAutoSelectRecordId());

		assertEquals(EVENT_ID21, lge.getEventManager().getEvents().get(1).getId1().getString());
		assertEquals(InteractionType.SELECTION, lge.getEventManager().getEvents().get(1)
				.getInteractionType());

	}

	@Test
	public void testTreeGridData22Proc() {
		GridContext context = getTestGridContext1();
		context.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		context.setParentId(PARENT_ID22);
		DataPanelElementInfo elInfo = getDPElement(TEST_XML, "7", "72");

		TreeGridDataGetCommand command = new TreeGridDataGetCommand(context, elInfo);
		List<TreeGridModel> tgd = command.execute();

		assertEquals(DATA_SIZE22, tgd.size());

		LiveGridExtradata lge = ((TreeGridData<TreeGridModel>) tgd).getLiveGridExtradata();

		assertNull(lge.getAutoSelectColumnId());
		assertEquals(REC_ID22, lge.getAutoSelectRecordId());

		assertEquals(EVENT_ID22, lge.getEventManager().getEvents().get(1).getId1().getString());
		assertEquals(InteractionType.SELECTION, lge.getEventManager().getEvents().get(1)
				.getInteractionType());

	}

}
