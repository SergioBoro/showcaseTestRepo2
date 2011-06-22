package ru.curs.showcase.app.test;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForms;
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

	static final int LEN_MAININSTANCE = 539;

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

		assertEquals(1, xfp.getPanel().getWidgetCount());

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
		assertEquals(2, target.getChildCount());

		assertEquals(LEN_MAININSTANCE, xfp.getMainInstance().trim().length());

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

		XForms xform = XFormTestsCommon.createXForms2();

		xfp.reDrawPanelExt(context, true, xform);
		assertNotNull(xfp.getContext());

		assertEquals(1, xfp.getPanel().getWidgetCount());

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
		assertEquals(2, target.getChildCount());

		assertEquals(LEN_MAININSTANCE, xfp.getMainInstance().trim().length());

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест2 ф-ции reDrawPanel.
	 */
	public void testReDrawPanel2() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		CompositeContext context = new CompositeContext();

		XForms xform = XFormTestsCommon.createXForms2();

		xfp.reDrawPanelExt(context, true, xform);

		assertEquals(1, xfp.getPanel().getWidgetCount());

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
		assertEquals(2, target.getChildCount());

	}

	/**
	 * Тест ф-ции destroyXForms.
	 */
	public void testDestroyXForms() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		XFormPanel.destroyXForms();

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertNull(dynastyle);

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
		assertEquals(0, target.getChildCount());

	}

	/**
	 * Тест ф-ции saveSettings.
	 */
	public void testSaveSettings() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		assertNotNull(xfp);

		xfp.saveSettings(false);
		xfp.saveSettings(true);

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertEquals(1, dynastyle.getChildCount());

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
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

		com.google.gwt.user.client.Element dynastyle = DOM.getElementById("dynastyle");
		assertNull(dynastyle);

		com.google.gwt.user.client.Element target = DOM.getElementById("target");
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
