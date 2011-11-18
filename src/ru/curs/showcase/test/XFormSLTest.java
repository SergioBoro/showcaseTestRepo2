package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.model.html.xform.*;
import ru.curs.showcase.util.*;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormSLTest extends AbstractTest {
	private static final String XFORMS_SUBMISSION1 = "xforms_submission1";
	private static final String TEST_DATA_TAG = "<data>test</data>";

	/**
	 * Тест функции получения XForms из сервисного уровня.
	 */
	@Test
	public void testServiceLayer() {
		XFormContext xcontext = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms1Info();

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xforms = command.execute();

		assertNotNull(xcontext.getSession());
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

	@Test
	public void testSaveXForms() {
		String data =
			"<schema xmlns=\"\"><info><name/><growth/><eyescolour/><music/><comment/></info></schema>";
		XFormContext xcontext = new XFormContext(getTestContext1());
		xcontext.setFormData(data);
		DataPanelElementInfo element = getTestXForms1Info();
		XFormSaveCommand command = new XFormSaveCommand(xcontext, element);
		command.execute();
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer.
	 */
	@Test
	public void testSQLSubmissionBySL() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo(XFORMS_SUBMISSION1);
		XFormSQLTransformCommand command = new XFormSQLTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals(data, res);
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer c передачей
	 * null в параметре content.
	 */
	@Test
	public void testSQLSubmissionBySLWithNullData() {
		String content = null;
		XFormContext context = new XFormContext();
		context.setFormData(content);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo(XFORMS_SUBMISSION1);
		XFormSQLTransformCommand command = new XFormSQLTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals("handleSQLSubmission должен вернуть пустую строку в ответ на null", "", res);
	}

	/**
	 * Функция тестирования работы XSLT Submission через ServiceLayer.
	 */
	@Test
	public void testXSLTSubmissionBySL() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("xformsxslttransformation_test.xsl");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	/**
	 * Проверка скачивания файла для XForms через ServiceLayer.
	 */
	@Test
	public void testXFormsFileDownloadBySL() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		String linkId = "proc4";
		XFormDownloadCommand command = new XFormDownloadCommand(context, elementInfo, linkId);
		OutputStreamDataFile file = command.execute();
		final int navigatorXMLLen = 231_478;
		assertNotNull(context.getSession());
		assertTrue(file.getData().size() > navigatorXMLLen);
		assertEquals(TextUtils.JDBC_ENCODING, file.getEncoding());
	}

	/**
	 * Проверка закачивания файла из XForms через ServiceLayer.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testXFormsFileUploadBySL() throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc5";
		final String fileName = TEST_XML_FILE;
		OutputStreamDataFile file = getTestFile(fileName);
		XFormUploadCommand command = new XFormUploadCommand(context, element, linkId, file);
		command.execute();
		assertNotNull(context.getSession());
	}

	/**
	 * Проверка загрузки на сервер правильного XML.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testXFormsXMLUploadGood() throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms2Info();
		String linkId = "proc7";
		final String fileName = "ru/curs/showcase/test/TestTextSample.xml";
		OutputStreamDataFile file = getTestFile(fileName);
		XFormUploadCommand command = new XFormUploadCommand(context, element, linkId, file);
		command.execute();
		assertNotNull(context.getSession());
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 */
	@Test
	public void testXFormsXMLDownloadGood() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		String linkId = "proc6";
		XFormDownloadCommand command = new XFormDownloadCommand(context, elementInfo, linkId);
		command.execute();
	}
}
