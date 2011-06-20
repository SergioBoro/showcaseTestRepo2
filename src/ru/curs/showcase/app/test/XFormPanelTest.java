package ru.curs.showcase.app.test;

import ru.curs.showcase.app.client.XFormPanel;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Класс для тестирования XFormPanel.
 */
public class XFormPanelTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	/**
	 * testConstr1.
	 */
	public void testConstr1() {
		XFormPanel xfp = new XFormPanel(null);
		assertNotNull(xfp);

		// xfp.setIsFirstLoading(true);
		// assertTrue(xfp.getIsFirstLoading());

		xfp.setIsFirstLoading(false);
		assertFalse(xfp.getIsFirstLoading());

		// VerticalPanel mainPanel = new VerticalPanel();
		// mainPanel.add(xfp.getPanel());
		// Button addStockButton = new Button("Add");
		// mainPanel.add(addStockButton);
		//
		// RootPanel.get("showcaseAppContainer").add(mainPanel);

	}

	/**
	 * testConstr2.
	 */
	public void testConstr2() {
		XFormPanel xfp = new XFormPanel(null, null);
		// delayTestFinish(2000);
		assertNotNull(xfp);

	}

	/**
	 * testReDrawPanel.
	 */
	public void testReDrawPanel() {
		XFormPanel xfp = new XFormPanel(null, null);
		assertNotNull(xfp);

		xfp.reDrawPanel(null, true);

		xfp.reDrawPanel(null, false);

	}

}
