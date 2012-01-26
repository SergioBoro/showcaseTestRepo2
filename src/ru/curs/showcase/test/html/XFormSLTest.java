package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormSLTest extends AbstractTest {
	private static final String SHOWCASE_DATA_XML = "Showcase_Data.xml";
	private static final String DATA_XFORMS = "data\\xforms\\";
	private static final String SHOWCASE_DATA_COPY_XML = "Showcase_Data_Copy.xml";
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
	public void testXSLTSubmission() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("xformsxslttransformation_test.xsl");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	@Test
	public void testXSLSubmissionInJython() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("transform/test.py");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	@Test
	public void testXSLSubmissionInSP() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("xforms_transform_test");
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
	public void testXFormsXMLUploadWithFileXSLGood() throws IOException {
		uploadTestBase("proc7");
	}

	private void uploadTestBase(final String linkId) throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms2Info();
		final String fileName = "ru/curs/showcase/test/util/TestTextSample.xml";
		OutputStreamDataFile file = getTestFile(fileName);
		XFormUploadCommand command = new XFormUploadCommand(context, element, linkId, file);
		command.execute();
		assertNotNull(context.getSession());
	}

	@Test
	public void testXFormsXMLUploadWithXSLJythonGood() throws IOException {
		uploadTestBase("proc7jj");
	}

	@Test
	public void testXFormsXMLUploadWithXSLStoredProcGood() throws IOException {
		uploadTestBase("proc7spsp");
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 */
	@Test
	public void testXFormsXMLDownloadXSLFileGood() {
		downloadTestBase("proc6");
	}

	private void downloadTestBase(final String linkId) {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		XFormDownloadCommand command = new XFormDownloadCommand(context, elementInfo, linkId);
		command.execute();
	}

	@Test
	public void testXFormsXMLDownloadXSLJythonGood() {
		downloadTestBase("proc6jj");
	}

	@Test
	public void testXFormsXMLDownloadXSLStoredProcGood() {
		downloadTestBase("proc6spsp");
	}

	@Test
	public void testJythonGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("XFormGetJythonProc.py");
		elementInfo.setTemplateName("Showcase_Template.xml");
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertEquals(1, xforms.getEventManager().getEvents().size());
		assertNotNull(xforms.getDefaultAction());
		assertNotNull(xforms.getXFormParts().get(0));
		assertNotNull(xforms.getXFormParts().get(2));
		assertNotNull(xforms.getXFormParts().get(2 + 1));
	}

	@Test
	public void testJythonTemplateGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("XFormGetJythonProc.py");
		elementInfo.setTemplateName("template/Base.py");
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertNotNull(xforms);
		assertNotNull(xforms.getXFormParts());
		final int numParts = 4;
		assertEquals(numParts, xforms.getXFormParts().size());
	}

	@Test
	public void testSPTemplateGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("xforms_proc_all");
		elementInfo.setTemplateName("xforms_template_uploaders_simple");
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertNotNull(xforms);
		assertNotNull(xforms.getXFormParts());
		final int numParts = 4;
		assertEquals(numParts, xforms.getXFormParts().size());
	}

	@Test(expected = GeneralException.class)
	public void testJythonNotExists() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("__fake__proc__.py");
		elementInfo.setTemplateName("Showcase_Template.xml");
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		command.execute();
	}

	@Test
	public void testSaveXFormByJython() throws IOException {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		XFormContext context = new XFormContext(generateContextWithSessionInfo());
		String inputData =
			XMLUtils.streamToString(AppProps.loadUserDataToStream(DATA_XFORMS + SHOWCASE_DATA_XML));
		context.setFormData(inputData);
		context.setAdditional(SHOWCASE_DATA_COPY_XML);
		File file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\" + DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML);
		if (file.exists()) {
			file.delete();
		}
		DataPanelElementInfo elementInfo = getTestXForms1Info();
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("saveproc");
		proc.setName("xform/XFormSaveProc.py");
		proc.setType(DataPanelElementProcType.SAVE);
		elementInfo.getProcs().clear();
		elementInfo.getProcs().put(proc.getId(), proc);

		XFormSaveCommand command = new XFormSaveCommand(context, elementInfo);
		command.execute();

		file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\" + DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML);
		assertTrue(file.exists());
		String outputData =
			XMLUtils.streamToString(AppProps.loadUserDataToStream(DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML));
		assertEquals(inputData, outputData);
	}
}
