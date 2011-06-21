package ru.curs.showcase.app.test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.client.XFormPanel;
import ru.curs.showcase.app.client.utils.UploadWindow;

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
	 * Тест без начального показа XForm.
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
	 * Тест с начальным показом XForm.
	 */
	public void testConstr2() {
		CompositeContext context = new CompositeContext();

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.XFORMS);

		// XForms xform = new XForms();
		XForms xform = null;

		XFormPanel xfp = new XFormPanel(context, dpei, xform);
		assertNotNull(xfp);

	}

	/**
	 * testReDrawPanel.
	 */
	public void testReDrawPanel() {
		XFormPanel xfp = new XFormPanel(null, null, null);
		assertNotNull(xfp);

		xfp.reDrawPanel(null, true);

		xfp.reDrawPanel(null, false);

	}

}
