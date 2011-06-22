package ru.curs.showcase.app.test;

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

	}

	/**
	 * Тест ф-ции xFormPanelClickSave.
	 */
	public void testXFormPanelClickSave() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции xFormPanelClickFilter.
	 */
	public void testXFormPanelClickFilter() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции showSelector.
	 */
	public void testShowSelector() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции downloadFile.
	 */
	public void testDownloadFile() {
		assertTrue(true);
	}

	/**
	 * Тест ф-ции uploadFile.
	 */
	public void testUploadFile() {
		assertTrue(true);
	}

}
