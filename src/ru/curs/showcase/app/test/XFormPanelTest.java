package ru.curs.showcase.app.test;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.utils.UploadWindow;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

/**
 * Класс для тестирования XFormPanel.
 */
public class XFormPanelTest extends GWTTestCase {

	private static final String DYNASTYLE = "dynastyle";
	private static final String TARGET = "target";

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	@Override
	public void gwtSetUp() {

		XFormTestsCommon.clearDOM();

	}

	/**
	 * Тест без начального показа XFormPanel.
	 */
	public void testConstr1() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests1();
		assertNotNull(xfp);

		assertEquals("1", xfp.getElementInfo().getId());
		assertNull(xfp.getContext());
		assertTrue(xfp.getIsFirstLoading());

		assertNotNull(xfp.getSelSrv());
		assertNotNull(xfp.getPanel());
		assertNull(xfp.getElement());
		assertNull(xfp.getDataService());

		UploadWindow uw = new UploadWindow("TestUploadWindow");
		uw.hide();
		xfp.setUw(uw);
		assertEquals("TestUploadWindow", xfp.getUw().getText());

		xfp.showPanel();
		assertTrue(xfp.getPanel().isVisible());
		xfp.hidePanel();
		assertFalse(xfp.getPanel().isVisible());

		xfp.setElementInfo(null);
		assertNull(xfp.getElementInfo());

	}

	/**
	 * Тест с начальным показом XFormPanel.
	 */
	public void testConstr2() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		assertNotNull(xfp.getContext());
		assertEquals("1", xfp.getElementInfo().getId());
		assertFalse(xfp.getIsFirstLoading());

		generalPartTest(xfp);

	}

	private void generalPartTest(final XFormPanel xfp) {
		assertEquals(1, xfp.getPanel().getWidgetCount());

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById(DYNASTYLE);
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
		assertEquals(2, target.getChildCount());

		assertEquals(XFormTestsCommon.LEN_MAININSTANCE, xfp.fillAndGetMainInstance().trim()
				.length());

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());
	}

	/**
	 * Тест1 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel1() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests1();
		assertNotNull(xfp);

		CompositeContext context = new CompositeContext();

		XForm xform = XFormTestsCommon.createXForms2();

		xfp.reDrawPanelExt(context, true, xform);
		assertNotNull(xfp.getContext());

		generalPartTest(xfp);

	}

	/**
	 * Тест2 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel2() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		CompositeContext context = new CompositeContext();

		XForm xform = XFormTestsCommon.createXForms2();

		xfp.reDrawPanelExt(context, true, xform);

		assertEquals(1, xfp.getPanel().getWidgetCount());

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById(DYNASTYLE);
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
		assertEquals(2, target.getChildCount());

	}

	/**
	 * Тест ф-ции destroyXForms.
	 */
	public void testDestroyXForms() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		XFormPanel.destroyXForms();

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById(DYNASTYLE);
		assertNull(dynastyle);

		com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
		assertEquals(0, target.getChildCount());

	}

	/**
	 * Тест ф-ции saveSettings.
	 */
	public void testSaveSettings() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		xfp.prepareSettings(false);
		xfp.prepareSettings(true);

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById(DYNASTYLE);
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
		assertEquals(2, target.getChildCount());

	}

	/**
	 * Тест ф-ции beforeModalWindow.
	 */
	public void testBeforeModalWindow() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		XFormPanel bep = new XFormPanel(null);
		XFormPanel.beforeModalWindow(bep);

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById(DYNASTYLE);
		assertNull(dynastyle);

		com.google.gwt.user.client.Element target = DOM.getElementById(TARGET);
		assertEquals(0, target.getChildCount());

	}

	/**
	 * Тест ф-ции reDrawBeforeModalWindow.
	 */
	public void testReDrawBeforeModalWindow() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		xfp.reDrawBeforeModalWindow();

		assertEquals(1, xfp.getPanel().getWidgetCount());
		assertEquals(Constants.PLEASE_WAIT_XFORM_3, ((HTML) xfp.getPanel().getWidget(0)).getHTML());

	}

}
