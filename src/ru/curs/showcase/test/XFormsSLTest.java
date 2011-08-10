package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormsSLTest extends AbstractTest {
	private static final String XFORMS_SUBMISSION1 = "xforms_submission1";
	private static final String TEST_XML_FILE = "log4j.xml";
	private static final String TEST_DATA_TAG = "<data>test</data>";

	/**
	 * Тест функции получения XForms из сервисного уровня.
	 * 
	 */
	@Test
	public void testServiceLayer() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		XForms xforms = sl.getXForms(context, element, null);

		assertNotNull(context.getSession());
		Action action = xforms.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xforms.getEventManager().getEvents().size());
		action = xforms.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		assertNotNull(xforms.getXFormParts());
		assertTrue(xforms.getXFormParts().size() > 0);
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testSQLSubmissionBySL() throws GeneralException {
		String data = TEST_DATA_TAG;
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String res = sl.handleSQLSubmission(XFORMS_SUBMISSION1, data, null);
		assertEquals(data, res);
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer c передачей
	 * null в параметре content.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testSQLSubmissionBySLWithNullData() throws GeneralException {
		String content = null;
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String res = sl.handleSQLSubmission(XFORMS_SUBMISSION1, content, null);
		assertEquals(content, res);
	}

	/**
	 * Функция тестирования работы XSLT Submission через ServiceLayer.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testXSLTSubmissionBySL() throws GeneralException {
		String data = TEST_DATA_TAG;
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String res = sl.handleXSLTSubmission("xformsxslttransformation_test.xsl", data, null);
		assertNotNull(res);
	}

	/**
	 * Проверка скачивания файла для XForms через ServiceLayer.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testXFormsFileDownloadBySL() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc4";
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		DataFile<ByteArrayOutputStream> file =
			serviceLayer.getDownloadFile(context, element, linkId, null);
		final int navigatorXMLLen = 231478;
		assertNotNull(context.getSession());
		assertTrue(file.getData().size() > navigatorXMLLen);
	}

	/**
	 * Проверка закачивания файла из XForms через ServiceLayer.
	 * 
	 * @throws GeneralException
	 * @throws IOException
	 */
	@Test
	public void testXFormsFileUploadBySL() throws GeneralException, IOException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc5";
		final String fileName = TEST_XML_FILE;
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.uploadFile(context, element, linkId, null, file);
		assertNotNull(context.getSession());
	}

	private DataFile<ByteArrayOutputStream> getTestFile(final String linkId) throws IOException {
		DataFile<ByteArrayOutputStream> file =
			new DataFile<ByteArrayOutputStream>(StreamConvertor.inputToOutputStream(AppProps
					.loadResToStream(linkId)), linkId);
		return file;
	}

	/**
	 * Проверка загрузки на сервер правильного XML.
	 * 
	 * @throws IOException
	 * @throws GeneralException
	 */
	@Test
	public void testXFormsXMLUploadGood() throws IOException, GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc7";
		final String fileName = "ru/curs/showcase/test/TestTextSample.xml";
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.uploadFile(context, element, linkId, null, file);
		assertNotNull(context.getSession());
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testXFormsXMLDownloadGood() throws GeneralException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc6";
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.getDownloadFile(context, element, linkId, null);
	}
}
