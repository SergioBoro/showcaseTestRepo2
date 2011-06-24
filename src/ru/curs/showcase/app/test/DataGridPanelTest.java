package ru.curs.showcase.app.test;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.client.*;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * Класс для тестирования DataGridPanel.
 */
public class DataGridPanelTest extends GWTTestCase {

	static final String HEADER = "Это хедер";
	static final String FOOTER = "Это футер";

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	@Override
	public void gwtSetUp() {

		com.google.gwt.user.client.Element elem = DOM.getElementById("showcaseAppContainer");
		if (elem != null) {
			elem.removeFromParent();
		}

		com.google.gwt.user.client.Element bodyElem = RootPanel.getBodyElement();
		com.google.gwt.user.client.Element div = DOM.createDiv();
		DOM.setElementAttribute(div, "id", "showcaseAppContainer");
		DOM.insertChild(bodyElem, div, 0);

	}

	private DataGridPanel createDataGridPanelForTests1() {

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.GRID);

		return new DataGridPanel(dpei);
	}

	private ru.curs.showcase.app.api.grid.Grid createGrid() {

		ru.curs.showcase.app.api.grid.Grid grid = new ru.curs.showcase.app.api.grid.Grid();

		DataSet ds = new DataSet();
		ColumnSet cs = new ColumnSet();
		RecordSet rs = new RecordSet();

		grid.setDataSet(ds);

		ds.setColumnSet(cs);
		ds.setRecordSet(rs);

		Column c1 = new Column();
		c1.setId("Регион");
		c1.setCaption("Регион");
		c1.setIndex(0);
		c1.setVisible(true);
		c1.setWidth("100px");

		Column c2 = new Column();
		c2.setId("Картинка");
		c2.setCaption("Картинка");
		c2.setIndex(1);
		c2.setVisible(true);
		c2.setWidth("20px");
		c2.setHorizontalAlignment(HorizontalAlignment.CENTER);
		c2.setDisplayMode(ColumnValueDisplayMode.AUTOFIT);
		c2.setValueType(GridValueType.IMAGE);

		Column c3 = new Column();
		c3.setId("3кв. 2005г.");
		c3.setCaption("3кв. 2005г.");
		c3.setIndex(2);
		c3.setVisible(true);
		c3.setWidth("60px");

		Column c4 = new Column();
		c4.setId("4кв. 2005г.");
		c4.setCaption("4кв. 2005г.");
		final int index = 3;
		c4.setIndex(index);
		c4.setVisible(true);
		c4.setWidth("60px");

		cs.setColumns(new ArrayList<Column>(Arrays.asList(c1, c2, c3, c4)));

		final int pageNumber = 1;
		final int pageSize = 3;
		final int pagesTotal = 27;

		rs.setPageNumber(pageNumber);
		rs.setPageSize(pageSize);
		rs.setPagesTotal(pagesTotal);

		Record r1 = new Record();
		r1.setId("0");
		r1.setValue("Регион", "Итого по России");
		r1.setValue("Картинка", "solutions/default/resources/imagesingrid/test.jpg");
		r1.setValue("3кв. 2005г.", "377,63");
		r1.setValue("4кв. 2005г.", "293,42");

		Record r2 = new Record();
		r2.setId("1");
		r2.setValue("Регион", "Алтайский край");
		r2.setValue("Картинка", "solutions/default/resources/imagesingrid/test.jpg");
		r2.setValue("3кв. 2005г.", "17,40");
		r2.setValue("4кв. 2005г.", "15,87");

		Record r3 = new Record();
		r3.setId("2");
		r3.setValue("Регион", "Тестовый регион");
		r3.setValue("Картинка", "solutions/default/resources/imagesingrid/test.jpg");
		r3.setValue("3кв. 2005г.", "15,27");
		r3.setValue("4кв. 2005г.", "0,30");

		rs.setRecords(Arrays.asList(r1, r2, r3));

		grid.setAutoSelectColumn(c3);
		grid.setAutoSelectRecord(r2);

		Action ac = new Action();
		ac.setDataPanelActionType(DataPanelActionType.DO_NOTHING);
		grid.setDefaultAction(ac);
		Event ev = new GridEvent();
		ev.setAction(ac);
		((GridEvent) ev).setColId(grid.getAutoSelectColumn().getId());
		((GridEvent) ev).setRecordId(grid.getAutoSelectRecord().getId());
		((GridEvent) ev).setInteractionType(InteractionType.SINGLE_CLICK);
		grid.getEventManager().setEvents(Arrays.asList(ev));

		grid.setHeader(HEADER);
		grid.setFooter(FOOTER);

		DataGridSettings settings = new DataGridSettings();
		final int pagerDuplicateRecords = 4;
		settings.setPagerDuplicateRecords(pagerDuplicateRecords);
		final int pagesButtonCount = 17;
		settings.setPagesButtonCount(pagesButtonCount);
		settings.setRightClickEnabled(false);
		settings.setMiddleClickEnabled(false);
		settings.setSingleClickBeforeDoubleClick(false);
		settings.setDoubleClickEnabled(true);
		final int doubleClickTime = 300;
		settings.setDoubleClickTime(doubleClickTime);
		settings.setSelectOnlyRecords(false);
		settings.setSelectOnDoubleClick(true);
		settings.setSelectRecordOnClick(true);
		settings.setUnselectRecordOnClick(false);
		settings.setUnselectCellOnClick(false);
		settings.setHorizontalScrollable(true);
		settings.setColumnGapHtml("");
		settings.setColumnGapWidth(2);
		grid.setUISettings(settings);

		return grid;

	}

	private DataGridPanel createDataGridPanelForTests2() {

		CompositeContext context = new CompositeContext();
		context.setMain("Ввоз, включая импорт - Всего");

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.GRID);
		dpei.setProcName("grid_bal");

		ru.curs.showcase.app.api.grid.Grid grid = createGrid();

		return new DataGridPanel(context, dpei, grid);
	}

	/**
	 * Тест без начального показа DataGridPanel.
	 */
	public void testConstr1() {

		DataGridPanel dgp = createDataGridPanelForTests1();
		assertNotNull(dgp);

		assertEquals("1", dgp.getElementInfo().getId());
		assertNull(dgp.getContext());
		assertTrue(dgp.getIsFirstLoading());

		assertNotNull(dgp.getPanel());
		assertNull(dgp.getElement());

		dgp.showPanel();
		assertTrue(dgp.getPanel().isVisible());
		dgp.hidePanel();
		assertFalse(dgp.getPanel().isVisible());

		dgp.setElementInfo(null);
		assertNull(dgp.getElementInfo());

	}

	/**
	 * Тест с начальным показом DataGridPanel.
	 */
	public void testConstr2() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		assertNotNull(dgp.getContext());
		assertEquals("1", dgp.getElementInfo().getId());
		assertFalse(dgp.getIsFirstLoading());

		final int widgetCount1 = 4;
		assertEquals(widgetCount1, dgp.getPanel().getWidgetCount());
		assertEquals(HEADER,
				((HTML) ((HorizontalPanel) dgp.getPanel().getWidget(0)).getWidget(0)).getHTML());
		final int widgetCount2 = 3;
		assertEquals(widgetCount2,
				((HorizontalPanel) dgp.getPanel().getWidget(1)).getWidgetCount());
		final int indWidget = 3;
		assertEquals(FOOTER,
				((HTML) ((HorizontalPanel) dgp.getPanel().getWidget(indWidget)).getWidget(0))
						.getHTML());

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест1 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel1() {

		DataGridPanel dgp = createDataGridPanelForTests1();
		assertNotNull(dgp);

		CompositeContext context = new CompositeContext();

		ru.curs.showcase.app.api.grid.Grid grid = createGrid();

		dgp.reDrawPanelExt(context, true, grid);
		assertNotNull(dgp.getContext());

		final int widgetCount1 = 4;
		assertEquals(widgetCount1, dgp.getPanel().getWidgetCount());

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест2 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel2() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		CompositeContext context = new CompositeContext();

		ru.curs.showcase.app.api.grid.Grid grid = createGrid();

		dgp.reDrawPanelExt(context, true, grid);

		final int widgetCount1 = 4;
		assertEquals(widgetCount1, dgp.getPanel().getWidgetCount());

	}

	/**
	 * Тест3 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel3() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		CompositeContext context = new CompositeContext();

		ru.curs.showcase.app.api.grid.Grid grid = createGrid();

		dgp.reDrawPanelExt(context, false, grid);

		final int widgetCount1 = 4;
		assertEquals(widgetCount1, dgp.getPanel().getWidgetCount());

	}

	/**
	 * Тест ф-ции exportToExcel.
	 */
	public void testExportToExcel() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		dgp.exportToExcel(GridToExcelExportType.CURRENTPAGE);
		dgp.exportToExcel(GridToExcelExportType.ALL);

	}

	/**
	 * Тест ф-ции copyToClipboard.
	 */
	public void testCopyToClipboard() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		ClipboardDialog cd = dgp.copyToClipboard();
		assertTrue(cd.isVisible());
		cd.hide();

	}

	/**
	 * Тест ф-ции saveSettings.
	 */
	public void testSaveSettings() {

		DataGridPanel dgp = createDataGridPanelForTests2();
		assertNotNull(dgp);

		dgp.saveSettings(true);
		dgp.saveSettings(false);

	}

}
