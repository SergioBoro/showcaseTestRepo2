package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormsGatewayTest extends AbstractTestBasedOnFiles {
	private static final String ELEMENT_0205 = "0205";
	private static final String XFORMS_SUBMISSION1 = "xforms_submission1";
	private static final String TEST_XML_FILE = "log4j.xml";
	private static final String TEST_DATA_TAG = "<data>test</data>";

	/**
	 * Тест для чтения из файла.
	 */
	@Test
	public void testFileGateWay() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);

		XFormsGateway gateway = new XFormsFileGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Тестируем обновление через FileGateway.
	 * 
	 */
	@Test
	public void testFileGatewayUpdate() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);

		XFormsGateway gateway = new XFormsFileGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsFileGateway();
		gateway.saveData(context, element, content);
		File file =
			new File(String.format(XFormsFileGateway.TMP_TEST_DATA_DIR + "/%s_updated.xml",
					element.getProcName()));
		assertTrue(file.exists());
	}

	/**
	 * Тест на трансформацию шаблона XForms с данными.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFileGateWayWithTransform() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);

		XFormsGateway gateway = new XFormsFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);

		DocumentBuilder db = XMLUtils.createBuilder();
		InputStream stream =
			AppProps.loadUserDataToStream(String.format("%s/%s", AppProps.XFORMS_DIR,
					element.getTemplateName()));
		Document doc = db.parse(stream);
		XFormProducer.getHTML(doc, raw.getData(), element.getId());
	}

	/**
	 * Проверка шлюза к БД.
	 * 
	 */
	@Test
	public void testDBgateway() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		XFormsGateway gateway = new XFormsDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway.
	 * 
	 */
	@Test
	public void testDBGatewayUpdate() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsDBGateway();
		gateway.saveData(context, element, content);
	}

	/**
	 * Тест на сохранение данных, который должен вернуть ошибку.
	 * 
	 */
	@Test
	public void testDBGatewayUpdateWithError() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormsDBGateway();
		try {
			gateway.saveData(context, element, content);
		} catch (ValidateInDBException e) {
			assertEquals("1", e.getUserMessage().getId());
			assertEquals("Неуловимая ошибка из БД, связанная с триггерами и блокировками (1)", e
					.getUserMessage().getText());
			return;
		}
		fail();
	}

	private String getNewContentBasedOnExisting(final CompositeContext context,
			final DataPanelElementInfo element, final XFormsGateway gateway) {
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		Element newChild = raw.getData().createElementNS("", "new");
		raw.getData().getDocumentElement().appendChild(newChild);
		String content = XMLUtils.documentToString(raw.getData());
		return content;
	}

	/**
	 * Функция тестирования работы XFormsDBGateway.handleSubmission.
	 * 
	 */
	@Test
	public void testSQLSubmission() {
		String data = TEST_DATA_TAG;
		XFormsGateway gateway = new XFormsDBGateway();
		String res = gateway.handleSubmission(XFORMS_SUBMISSION1, data);
		assertEquals(data, res);
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
	 * Проверка файлового шлюза для скачивания данных.
	 * 
	 */
	@Test
	public void testXFormsFileGatewayDownload() {
		XFormsGateway gateway = new XFormsFileGateway();
		final String linkId = TEST_XML_FILE;
		DataFile<ByteArrayOutputStream> file = gateway.downloadFile(null, null, linkId, null);
		assertNotNull(file);
		assertNotNull(file.getData());
		assertEquals(linkId, file.getName());
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
	 * Проверка файлового шлюза для закачивания данных.
	 * 
	 */
	@Test
	public void testXFormsFileGatewayUpload() {
		XFormsGateway gateway = new XFormsFileGateway();
		final String linkId = TEST_XML_FILE;
		DataFile<InputStream> file =
			new DataFile<InputStream>(AppProps.loadResToStream(linkId), linkId);
		gateway.uploadFile(null, null, linkId, null, file);
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
	 * Проверка загрузки на сервер не соответствующего схеме XML.
	 * 
	 * @throws IOException
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLUploadBad() throws IOException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		String linkId = "proc8";
		final String fileName = "ru/curs/showcase/test/TestTextSample.xml";
		DataFile<ByteArrayOutputStream> file = getTestFile(fileName);

		UserXMLTransformer transformer =
			new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
		transformer.checkAndTransform();

		XFormsGateway gateway = new XFormsDBGateway();
		gateway.uploadFile(context, elementInfo, linkId, null, transformer.getInputStreamResult());
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

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 * 
	 * @throws IOException
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLDownloadBad() throws IOException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		String linkId = "proc10";

		XFormsGateway gateway = new XFormsDBGateway();
		DataFile<ByteArrayOutputStream> file =
			gateway.downloadFile(context, elementInfo, linkId, null);

		UserXMLTransformer transformer =
			new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
		transformer.checkAndTransform();
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway c проверкой схемы и
	 * трансформацией.
	 * 
	 */
	@Test
	public void testDBGatewayUpdateWithTransform() throws IOException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0208");

		XFormsGateway gateway = new XFormsDBGateway();
		String content = getNewContentBasedOnExisting(context, elementInfo, gateway);

		UserXMLTransformer transformer =
			new UserXMLTransformer(content, elementInfo.getSaveProc());
		transformer.checkAndTransform();
		gateway = new XFormsDBGateway();
		gateway.saveData(context, elementInfo, transformer.getStringResult());
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway, приводящий к ошибке
	 * проверки XSD.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testDBUpdateWithInvalidXML() throws IOException {
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0209");

		String content = "<test/>";
		UserXMLTransformer transformer =
			new UserXMLTransformer(content, elementInfo.getSaveProc());
		transformer.checkAndTransform();
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway, приводящий к ошибке
	 * проверки XSD.
	 * 
	 */
	@Test(expected = NotXMLException.class)
	public void testDBUpdateWithNotXML() throws IOException {
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0209");

		String content = "<test>";
		UserXMLTransformer transformer =
			new UserXMLTransformer(content, elementInfo.getSaveProc());
		transformer.checkAndTransform();
	}
}
