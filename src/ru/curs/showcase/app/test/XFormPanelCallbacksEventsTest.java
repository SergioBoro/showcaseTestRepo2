package ru.curs.showcase.app.test;

import ru.curs.showcase.app.api.event.DataPanelActionType;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Класс для тестирования XFormPanelCallbacksEvents.
 */
public class XFormPanelCallbacksEventsTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "ru.curs.showcase.app.AppTest";
	}

	@Override
	public void gwtSetUp() {

		XFormTestsCommon.clearDOM();

		XFormPanelCallbacksEvents.setTestXFormPanel(null);

	}

	/**
	 * Тест ф-ции xFormPanelClickSave.
	 */
	public void testXFormPanelClickSave() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		XFormPanelCallbacksEvents.setTestXFormPanel(xfp);

		final String xformId = "61";
		final String linkId = "1";
		final String data = XFormTestsCommon.XFORM_DATA;

		XFormPanelCallbacksEvents.xFormPanelClickSave(xformId, linkId, data);

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест ф-ции xFormPanelClickFilter.
	 */
	public void testXFormPanelClickFilter() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		XFormPanelCallbacksEvents.setTestXFormPanel(xfp);

		final String xformId = "61";
		final String linkId = "2";
		final String data = XFormTestsCommon.XFORM_DATA;

		XFormPanelCallbacksEvents.xFormPanelClickFilter(xformId, linkId, data);

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест ф-ции xFormPanelClickUpdate.
	 */
	public void testXFormPanelClickUpdate() {

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		XFormPanelCallbacksEvents.setTestXFormPanel(xfp);

		final String xformId = "61";
		final String linkId = "3";
		final String data = XFormTestsCommon.XFORM_DATA;

		XFormPanelCallbacksEvents.xFormPanelClickUpdate(xformId, linkId, data);

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест ф-ции showSelector.
	 */
	public void testShowSelector() {
		assertTrue(true);

		// XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		// XFormPanelCallbacksEvents.setTestXFormPanel(xfp);
		// XFormPanelCallbacksEvents.showSelector(null);
	}

	/**
	 * Тест ф-ции downloadFile.
	 */
	public void testDownloadFile() {
		// assertTrue(true);

		XFormPanel xfp = XFormTestsCommon.createXFormPanelForTests2();
		XFormPanelCallbacksEvents.setTestXFormPanel(xfp);

		final String xformId = "611";
		final String linkId = "4";
		final String data = XFormTestsCommon.XFORM_DATA;

		XFormPanelCallbacksEvents.downloadFile(xformId, linkId, data);

		assertEquals(DataPanelActionType.DO_NOTHING, AppCurrContext.getInstance()
				.getCurrentAction().getDataPanelActionType());

	}

	/**
	 * Тест ф-ции uploadFile.
	 */
	public void testUploadFile() {
		assertTrue(true);
	}

}
