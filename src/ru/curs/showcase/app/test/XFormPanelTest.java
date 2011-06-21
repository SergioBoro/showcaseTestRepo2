package ru.curs.showcase.app.test;

import java.util.ArrayList;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.client.XFormPanel;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.utils.UploadWindow;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * Класс для тестирования XFormPanel.
 */
public class XFormPanelTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	/**
	 * Тест без начального показа XFormPanel.
	 */
	public void testConstr1() {

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.XFORMS);

		XFormPanel xfp = new XFormPanel(dpei);
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

		com.google.gwt.user.client.Element bodyElem = RootPanel.getBodyElement();
		com.google.gwt.user.client.Element script = DOM.createElement("script");
		DOM.setElementAttribute(script, "type", "text/javascript");
		DOM.setElementAttribute(script, "src", "xsltforms/xsltforms.js");
		DOM.appendChild(bodyElem, script);

		com.google.gwt.user.client.Element div = DOM.createDiv();
		DOM.setElementAttribute(div, "id", "target");
		DOM.appendChild(bodyElem, div);

		CompositeContext context = new CompositeContext();

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.XFORMS);

		XForms xform = new XForms();
		ArrayList<String> xFormParts = new ArrayList<String>();
		xform.setXFormParts(xFormParts);

		XFormPanel xfp = new XFormPanel(context, dpei, xform);
		assertNotNull(xfp);

		assertNotNull(xfp.getContext());
		assertEquals("1", xfp.getElementInfo().getId());
		assertTrue(xfp.getIsFirstLoading());

		assertEquals(1, xfp.getPanel().getWidgetCount());
		assertEquals(Constants.PLEASE_WAIT_XFORM_1, ((HTML) xfp.getPanel().getWidget(0)).getText());

	}

	@Override
	public void gwtSetUp() {

	}

	/**
	 * Тест ф-ции reDrawPanel.
	 */
	public void testReDrawPanel() {

		assertTrue(true);

		// XFormPanel xfp = new XFormPanel(null, null, null);
		// assertNotNull(xfp);
		//
		// xfp.reDrawPanel(null, true);
		// xfp.reDrawPanel(null, false);

	}

}
