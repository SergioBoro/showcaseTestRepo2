package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.grid.GridTransformer;
import ru.curs.showcase.test.AbstractTest;

/**
 * Модульные тесты (без обращения к БД или файловой системе) LiveGrid и его
 * внутренних компонентов.
 * 
 */
public class LiveGridModuleTest extends AbstractTest {

	private static final String HEADER = "Хедер";
	private static final String FOOTER = "Футер";

	private static final int LIVE_INFO_OFFSET = 2;
	private static final int LIVE_INFO_LIMIT = 80;
	private static final int LIVE_INFO_TOTALCOUNT = 1000;

	private static final Integer UI_SETTINGS_GRID_HEIGHT = 200;
	private static final Integer UI_SETTINGS_ROW_HEIGHT = 30;

	private static final String REC_ID = "rec1";
	private static final String COL_ID = "col1";
	private static final String COL_CAPTION = "Столбец1";

	private static final String FONT_SIZE = "2em";
	private static final String TEXT_COLOR = "red";
	private static final String BACKGROUND_COLOR = "blue";

	private static final String ROWSTYLE = "extlivegrid-record-bold";

	@Test
	public void testTransformGridToLiveGridMetadata() {
		Grid grid = generateTestGrid();
		LiveGridMetadata lgm = GridTransformer.gridToLiveGridMetadata(grid);

		assertEquals(HEADER, lgm.getHeader());
		assertEquals(FOOTER, lgm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, lgm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT, lgm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT, lgm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, lgm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, lgm.getUISettings().getRowHeight());

		assertEquals(COL_CAPTION, lgm.getColumns().get(0).getCaption());
		assertEquals(HorizontalAlignment.RIGHT, lgm.getColumns().get(0).getHorizontalAlignment());

		assertEquals(COL_ID, lgm.getOriginalColumnSet().getColumns().get(0).getId());

		assertEquals(FONT_SIZE, lgm.getFontSize());
		assertEquals(TEXT_COLOR, lgm.getTextColor());
		assertEquals(BACKGROUND_COLOR, lgm.getBackgroundColor());
		assertTrue(lgm.getFontModifiers().contains(FontModifier.BOLD));
		assertTrue(lgm.getFontModifiers().contains(FontModifier.ITALIC));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertFalse(lgm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));

	}

	@Test
	public void testTransformGridToLiveGridData() {
		Grid grid = generateTestGrid();
		LiveGridData<LiveGridModel> lgd = GridTransformer.gridToLiveGridData(grid);

		assertEquals(LIVE_INFO_OFFSET, lgd.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT, lgd.getTotalLength());
		assertEquals(1, lgd.getData().size());

		LiveGridExtradata lge = lgd.getLiveGridExtradata();

		assertEquals(COL_ID, lge.getAutoSelectColumnId());
		assertEquals(REC_ID, lge.getAutoSelectRecordId());

	}

	private Grid generateTestGrid() {
		Grid grid = new Grid();
		grid.setHeader(HEADER);
		grid.setFooter(FOOTER);

		grid.getLiveInfo().setOffset(LIVE_INFO_OFFSET);
		grid.getLiveInfo().setTotalCount(LIVE_INFO_TOTALCOUNT);

		grid.getUISettings().setGridHeight(UI_SETTINGS_GRID_HEIGHT);
		grid.getUISettings().setRowHeight(UI_SETTINGS_ROW_HEIGHT);

		DataSet ds = new DataSet();
		ColumnSet cs = new ColumnSet();
		Column col = new Column();
		col.setId(COL_ID);
		col.setCaption(COL_CAPTION);
		col.setValueType(GridValueType.DATE);
		col.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		cs.setColumns(new ArrayList<Column>(Arrays.asList(col)));

		RecordSet rs = new RecordSet();
		rs.setPageSize(LIVE_INFO_LIMIT);
		Record rec = new Record();
		rec.setId(REC_ID);
		rec.setFontSize(FONT_SIZE);
		rec.setTextColor(TEXT_COLOR);
		rec.setBackgroundColor(BACKGROUND_COLOR);
		rec.addFontModifier(FontModifier.BOLD);
		rec.addFontModifier(FontModifier.ITALIC);
		rs.setRecords(Arrays.asList(rec));

		grid.setAutoSelectColumn(col);
		grid.setAutoSelectRecord(rec);

		ds.setColumnSet(cs);
		ds.setRecordSet(rs);
		grid.setDataSet(ds);

		return grid;
	}

	@Test
	public void testLiveGridData() {
		LiveGridModel lgm = new LiveGridModel();

		lgm.set("id", REC_ID);
		assertEquals(REC_ID, lgm.getId());

		lgm.set("rowstyle", ROWSTYLE);
		assertEquals(ROWSTYLE, lgm.getRowStyle());

		LiveGridModel lgm1 = new LiveGridModel();
		lgm1.setId("1");
		LiveGridModel lgm2 = new LiveGridModel();
		lgm2.setId("1");
		assertTrue(lgm1.equals(lgm2));

		Object obj = new Object();
		assertFalse(lgm1.equals(obj));

		lgm2.setId("2");
		assertFalse(lgm1.equals(lgm2));

	}

	@Test
	public void testLiveGridDataSerialize() {
		LiveGridModel lgm = new LiveGridModel();

		lgm.set("id", REC_ID);
		lgm.set("rowstyle", ROWSTYLE);

		org.w3c.dom.Document doc = ru.curs.showcase.util.xml.XMLUtils.objectToXML(lgm);
		assertNotNull(doc);
	}

	@Test
	public void testLiveGridPagingLoadResultSerialize() {
		ArrayList<LiveGridModel> sublist = new ArrayList<LiveGridModel>();
		LiveGridModel lgm = new LiveGridModel();
		lgm.setId(REC_ID);
		sublist.add(lgm);

		LiveGridData<LiveGridModel> lgd =
			new LiveGridData<LiveGridModel>(sublist, LIVE_INFO_OFFSET, LIVE_INFO_TOTALCOUNT);

		LiveGridExtradata lge = new LiveGridExtradata();
		lge.setAutoSelectRecordId(REC_ID);

		lgd.setLiveGridExtradata(lge);

		org.w3c.dom.Document doc = ru.curs.showcase.util.xml.XMLUtils.objectToXML(lgd);
		assertNotNull(doc);

	}

}
