package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.GridTransformer;
import ru.curs.showcase.test.AbstractTest;

/**
 * Модульные тесты (без обращения к БД или файловой системе) TreeGrid и его
 * внутренних компонентов.
 * 
 */
public class TreeGridModuleTest extends AbstractTest {

	private static final String HEADER = "Хедер tree-грида";
	private static final String FOOTER = "Футер tree-грида";

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 400;

	private static final String REC_ID = "rec2";
	private static final String COL_ID = "col2";
	private static final String COL_CAPTION = "Столбец2";

	private static final String FONT_SIZE = "1em";
	private static final String TEXT_COLOR = "yellow";
	private static final String BACKGROUND_COLOR = "black";

	private static final String ROWSTYLE = "exttreegrid-record-bold";

	@Test
	public void testTransformGridToLiveGridMetadata() {
		Grid grid = generateTestGrid();
		LiveGridMetadata lgm = GridTransformer.gridToLiveGridMetadata(grid);

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());

		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.RIGHT, lgm.getColumns().get(0).getHorizontalAlignment());

		assertEquals(COL_ID, lgm.getOriginalColumnSet().getColumns().get(0).getId());

		assertEquals(FONT_SIZE, lgm.getFontSize());
		assertEquals(TEXT_COLOR, lgm.getTextColor());
		assertEquals(BACKGROUND_COLOR, lgm.getBackgroundColor());
		assertTrue(lgm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertTrue(lgm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.BOLD));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.ITALIC));

	}

	@Test
	public void testTransformGridToTreeGridData() {
		Grid grid = generateTestGrid();
		TreeGridData<TreeGridModel> tgd = GridTransformer.gridToTreeGridData(grid);

		assertEquals(1, tgd.size());

		LiveGridExtradata lge = tgd.getLiveGridExtradata();

		assertEquals(COL_ID, lge.getAutoSelectColumnId());
		assertEquals(REC_ID, lge.getAutoSelectRecordId());

	}

	private Grid generateTestGrid() {
		Grid grid = new Grid();
		grid.setHeader(HEADER);
		grid.setFooter(FOOTER);

		grid.getUISettings().setGridHeight(UI_SETTINGS_GRID_HEIGHT);

		DataSet ds = new DataSet();
		ColumnSet cs = new ColumnSet();
		Column col = new Column();
		col.setId(COL_ID);
		col.setCaption(COL_CAPTION);
		col.setValueType(GridValueType.STRING);
		col.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		cs.setColumns(new ArrayList<Column>(Arrays.asList(col)));

		RecordSet rs = new RecordSet();

		Record rec = new Record();
		rec.setId(REC_ID);
		rec.setFontSize(FONT_SIZE);
		rec.setTextColor(TEXT_COLOR);
		rec.setBackgroundColor(BACKGROUND_COLOR);
		rec.addFontModifier(FontModifier.UNDERLINE);
		rec.addFontModifier(FontModifier.STRIKETHROUGH);
		rs.setRecords(Arrays.asList(rec));

		grid.setAutoSelectColumn(col);
		grid.setAutoSelectRecord(rec);

		ds.setColumnSet(cs);
		ds.setRecordSet(rs);
		grid.setDataSet(ds);

		return grid;
	}

	@Test
	public void testTreeGridData() {
		TreeGridModel tgm = new TreeGridModel();

		tgm.set("id", REC_ID);
		assertEquals(REC_ID, tgm.getId());

		tgm.set("rowstyle", ROWSTYLE);
		assertEquals(ROWSTYLE, tgm.getRowStyle());

		TreeGridModel tgm1 = new TreeGridModel();
		tgm1.setId("1");
		TreeGridModel tgm2 = new TreeGridModel();
		tgm2.setId("1");
		assertTrue(tgm1.equals(tgm2));

		Object obj = new Object();
		assertFalse(tgm1.equals(obj));

		tgm2.setId("2");
		assertFalse(tgm1.equals(tgm2));

	}

	@Test
	public void testTreeGridDataSerialize() {
		TreeGridModel tgm = new TreeGridModel();

		tgm.set("id", REC_ID);
		tgm.set("rowstyle", ROWSTYLE);
		tgm.setHasChildren(true);

		org.w3c.dom.Document doc = ru.curs.showcase.util.xml.XMLUtils.objectToXML(tgm);
		assertNotNull(doc);
	}

	@Test
	public void testTreeGridLoadResultSerialize() {

		TreeGridData<TreeGridModel> tgd = new TreeGridData<TreeGridModel>();

		TreeGridModel tgm = new TreeGridModel();
		tgm.setId(REC_ID);
		tgd.add(tgm);

		LiveGridExtradata lge = new LiveGridExtradata();
		lge.setAutoSelectRecordId(REC_ID);

		tgd.setLiveGridExtradata(lge);

		org.w3c.dom.Document doc = ru.curs.showcase.util.xml.XMLUtils.objectToXML(tgd);
		assertNotNull(doc);

	}

}
