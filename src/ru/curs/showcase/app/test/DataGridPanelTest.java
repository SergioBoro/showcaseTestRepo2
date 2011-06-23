package ru.curs.showcase.app.test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Класс для тестирования DataGridPanel.
 */
public class DataGridPanelTest extends GWTTestCase {

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

	private DataGridPanel createDataGridPanelForTests2() {

		CompositeContext context = new CompositeContext();

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.GRID);

		Grid grid = new Grid();

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

		final CompositeContext context = new CompositeContext();
		context.setMain("Ввоз, включая импорт - Всего");

		final DataPanelElementInfo elInfo =
			new DataPanelElementInfo("2", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("grid_bal");

		DataServiceAsync dataService = GWT.create(DataService.class);

		dataService.getGrid(context, elInfo, null, new GWTServiceCallback<Grid>(
				"Ошибка при получении данных таблицы с сервера") {

			@Override
			public void onSuccess(final Grid grid) {

				// DataGridPanel dgp = createDataGridPanelForTests2();
				DataGridPanel dgp = new DataGridPanel(context, elInfo, grid);
				assertNotNull(dgp);

				assertNotNull(dgp.getContext());
				assertEquals("1", dgp.getElementInfo().getId());
				// assertFalse(dgp.getIsFirstLoading());

				// assertEquals(1, dgp.getPanel().getWidgetCount());

				// assertEquals(DataPanelActionType.DO_NOTHING,
				// AppCurrContext.getInstance()
				// .getCurrentAction().getDataPanelActionType());

				finishTest();

			}
		});

		delayTestFinish(10000);

	}

	/**
	 * Тест ф-ции reDrawPanel.
	 */
	public void testReDrawPanel() {

		assertTrue(true);

	}

}
