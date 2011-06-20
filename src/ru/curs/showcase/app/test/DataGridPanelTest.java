package ru.curs.showcase.app.test;

import ru.curs.showcase.app.client.DataGridPanel;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Класс для тестирования DataGridPanel.
 */
public class DataGridPanelTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	/**
	 * testConstr1.
	 */
	public void testConstr1() {
		DataGridPanel dgp = new DataGridPanel(null, null);
		assertNotNull(dgp);

	}

	/**
	 * testConstr2.
	 */
	public void testConstr2() {
		DataGridPanel dgp = new DataGridPanel(null, null, null);
		assertNotNull(dgp);

	}

	/**
	 * testReDrawPanel.
	 */
	public void testReDrawPanel() {
		DataGridPanel dgp = new DataGridPanel(null, null, null);
		assertNotNull(dgp);

		dgp.reDrawPanel(null, true);

		dgp.reDrawPanel(null, false);

	}

}
