package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты на получение информации о сессии пользователя.
 * 
 * @author den
 * 
 */
public class SessionInfoTest extends AbstractTest {
	private static final boolean AUTH_VIA_AUTH_SERVER = true;
	private static final String TEMP_PASS = "pass";
	private static final String FAKE_SESSION_ID = "fake-session-id";
	private static final String NOT_EXIST_USERDATA_ID = "test123";

	/**
	 * Простой тест на установку текущего userdataId.
	 */
	@Test
	public void testCurUserDataIdSet() {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(
				generateTestURLParamsForSL(TEST1_USERDATA));
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
	}

	/**
	 * Проверка того, что значение getCurrentUserDataId сразу после запуска
	 * равно null.
	 */
	@Test
	public void testInitialCurUserDataIdValue() {
		setDefaultUserData();
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
	}

	/**
	 * Базовый тест на запись и чтение URLParams.
	 * 
	 * @throws IOException
	 * @throws GeneralException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetChart() throws IOException, GeneralException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);

		AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(FAKE_SESSION_ID,
				AUTH_VIA_AUTH_SERVER);
		AppInfoSingleton.getAppInfo().setAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID,
				TEMP_PASS);

		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(FAKE_SESSION_ID);
		serviceLayer.getChart(context, element);

		checkTestUrlParams(context);
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		assertEquals(AUTH_VIA_AUTH_SERVER, AppInfoSingleton.getAppInfo()
				.getAuthViaAuthServerForSession(FAKE_SESSION_ID));
		assertEquals(TEMP_PASS, AppInfoSingleton.getAppInfo()
				.getAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID));

	}

	private void checkTestUrlParams(final CompositeContext context) throws SAXException,
			IOException {
		String sessionContext = context.getSession();
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = db.parse(new InputSource(new StringReader(sessionContext)));

		assertEquals(1,
				doc.getDocumentElement()
						.getElementsByTagName(SessionContextGenerator.USERNAME_TAG).getLength());

		assertEquals(
				1,
				doc.getDocumentElement()
						.getElementsByTagName(SessionContextGenerator.URL_PARAMS_TAG).getLength());
		Node node =
			doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.URL_PARAMS_TAG)
					.item(0);
		assertEquals(SessionContextGenerator.URL_PARAM_TAG, node.getChildNodes().item(1)
				.getNodeName());
		assertEquals(2, node.getChildNodes().item(1).getAttributes().getLength());
		assertEquals(KEY1, node.getChildNodes().item(1).getAttributes().getNamedItem(NAME_TAG)
				.getNodeValue());
		assertEquals("[" + VALUE12 + "]", node.getChildNodes().item(1).getAttributes()
				.getNamedItem(VALUE_TAG).getNodeValue());

		assertEquals(1,
				doc.getDocumentElement()
						.getElementsByTagName(SessionContextGenerator.USERDATA_TAG).getLength());
		node =
			doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.USERDATA_TAG)
					.item(0);
		assertEquals(TEST1_USERDATA, node.getTextContent());
	}

	/**
	 * Проверка установки информации о сессии для функции получения инф. панели.
	 * 
	 * @throws IOException
	 * @throws GeneralException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetDP() throws IOException, GeneralException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(FAKE_SESSION_ID);
		final int elID = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, elID);
		action.setSessionContext(params);
		serviceLayer.getDataPanel(action);
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		checkTestUrlParams(action.getContext());
		checkTestUrlParams(action.getDataPanelLink().getElementLinks().get(0).getContext());
	}

	/**
	 * Проверка считывания информации о сессии, если userdata не задана.
	 * 
	 * @throws IOException
	 * @throws GeneralException
	 * @throws SAXException
	 */
	@Test
	public void testWriteAndReadIfNoURLParams() throws IOException, GeneralException, SAXException {
		Map<String, List<String>> params = new TreeMap<String, List<String>>();

		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(FAKE_SESSION_ID);
		serviceLayer.getChart(context, element);

		String sessionContext = context.getSession();
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = db.parse(new InputSource(new StringReader(sessionContext)));
		assertEquals(1,
				doc.getDocumentElement()
						.getElementsByTagName(SessionContextGenerator.USERDATA_TAG).getLength());
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, doc.getDocumentElement()
				.getElementsByTagName(SessionContextGenerator.USERDATA_TAG).item(0)
				.getTextContent());

		assertEquals(
				0,
				doc.getDocumentElement()
						.getElementsByTagName(SessionContextGenerator.URL_PARAMS_TAG).getLength());

	}

	/**
	 * Проверка установки и чтения текущей userdata.
	 */
	@Test
	public void testCurrentUserdata() {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(
				new TreeMap<String, ArrayList<String>>());
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
		assertNotNull(AppProps.getUserDataCatalog());

		Map<String, ArrayList<String>> params = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> value3 = new ArrayList<String>();
		value3.add(TEST1_USERDATA);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(params);
		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		assertNotNull(AppProps.getUserDataCatalog());
	}

	/**
	 * Проверка установки несуществующей userdata.
	 */
	@Test(expected = NoSuchUserDataException.class)
	public void testNotExistUserdata() {
		Map<String, ArrayList<String>> params = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> value3 = new ArrayList<String>();
		value3.add(NOT_EXIST_USERDATA_ID);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(params);
	}

	/**
	 * Проверка вызова функции getServerCurrentState.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetServerCurrentState() throws GeneralException {
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		CompositeContext context = new CompositeContext();
		ServerCurrentState scs = sl.getServerCurrentState(context);
		assertNotNull(scs);
		assertEquals(AppInfoSingleton.getAppInfo().getServletContainerVersion(),
				scs.getServletContainerVersion());
		assertEquals(System.getProperty("java.version"), scs.getJavaVersion());
	}

	@Test
	public void testRelatedData() throws UnsupportedEncodingException {
		CompositeContext context = CompositeContext.createCurrent();
		getExtGridContext(context);
		String res = SessionContextGenerator.generate(TEST_SESSION, context);
		assertTrue(res.contains("<selectedRecordId>r2</selectedRecordId>"));
		assertTrue(res.contains("<currentColumnId>curColumnId</currentColumnId>"));
		assertTrue(res.contains("size=\"2\""));
		assertTrue(res.contains("<add>value</add>"));
	}
}
