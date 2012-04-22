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
	public void testTransformGridToExtGridMetadata() {
		Grid grid = generateTestGrid();
		ExtGridMetadata egm = GridTransformer.gridToExtGridMetadata(grid);

		assertEquals(HEADER, egm.getHeader());
		assertEquals(FOOTER, egm.getFooter());

		assertEquals(LIVE_INFO_OFFSET, egm.getLiveInfo().getOffset());
		assertEquals(LIVE_INFO_LIMIT, egm.getLiveInfo().getLimit());
		assertEquals(LIVE_INFO_TOTALCOUNT, egm.getLiveInfo().getTotalCount());

		assertEquals(UI_SETTINGS_GRID_HEIGHT, egm.getUISettings().getGridHeight());
		assertEquals(UI_SETTINGS_ROW_HEIGHT, egm.getUISettings().getRowHeight());

		assertEquals(COL_CAPTION, egm.getColumns().get(0).getCaption());
		assertEquals(com.extjs.gxt.ui.client.Style.HorizontalAlignment.RIGHT, egm.getColumns()
				.get(0).getHorizontalAlignment());

		assertEquals(COL_ID, egm.getOriginalColumnSet().getColumns().get(0).getId());

		assertEquals(FONT_SIZE, egm.getFontSize());
		assertEquals(TEXT_COLOR, egm.getTextColor());
		assertEquals(BACKGROUND_COLOR, egm.getBackgroundColor());
		assertTrue(egm.getFontModifiers().contains(FontModifier.BOLD));
		assertTrue(egm.getFontModifiers().contains(FontModifier.ITALIC));
		assertFalse(egm.getFontModifiers().contains(FontModifier.UNDERLINE));
		assertFalse(egm.getFontModifiers().contains(FontModifier.STRIKETHROUGH));

	}

	@Test
	public void testTransformGridToExtGridData() {
		Grid grid = generateTestGrid();
		ExtGridPagingLoadResult<ExtGridData> egplr = GridTransformer.gridToExtGridData(grid);

		assertEquals(LIVE_INFO_OFFSET, egplr.getOffset());
		assertEquals(LIVE_INFO_TOTALCOUNT, egplr.getTotalLength());
		assertEquals(1, egplr.getData().size());

		ExtGridExtradata ege = egplr.getExtGridExtradata();

		assertEquals(COL_ID, ege.getAutoSelectColumnId());
		assertEquals(REC_ID, ege.getAutoSelectRecordId());

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
	public void testExtGridData() {
		ExtGridData egd = new ExtGridData();

		egd.set("id", REC_ID);
		assertEquals(REC_ID, egd.getId());

		egd.set("rowstyle", ROWSTYLE);
		assertEquals(ROWSTYLE, egd.getRowStyle());

		ExtGridData egd1 = new ExtGridData();
		egd1.setId("1");
		ExtGridData egd2 = new ExtGridData();
		egd2.setId("1");
		assertTrue(egd1.equals(egd2));

		Object obj = new Object();
		assertFalse(egd1.equals(obj));

		egd2.setId("2");
		assertFalse(egd1.equals(egd2));

	}

}
