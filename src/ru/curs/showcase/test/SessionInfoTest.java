package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.exception.NoSuchUserDataException;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Тесты на получение информации о сессии пользователя.
 * 
 * @author den
 * 
 */
public class SessionInfoTest extends AbstractTestBasedOnFiles {
	static final boolean AUTH_VIA_AUTH_SERVER = true;
	static final String TEMP_PASS = "pass";
	static final String FAKE_SESSION_ID = "fake-session-id";
	static final String USERDATA_ID = "test1";
	static final String NOT_EXIST_USERDATA_ID = "test123";

	/**
	 * Простой тест на установку текущего userdataId.
	 */
	@Test
	public void testCurUserDataIdSet() {
		AppInfoSingleton.getAppInfo().setCurrentUserDataId(generateTestURLParamsForSL("test1"));
		assertEquals("test1", AppInfoSingleton.getAppInfo().getCurrentUserDataId());
	}

	/**
	 * Проверка того, что значение getCurrentUserDataId сразу после запуска
	 * равно null.
	 */
	@Test
	public void testInitialCurUserDataIdValue() {
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, AppInfoSingleton.getAppInfo()
				.getCurrentUserDataId());
	}

	/**
	 * Базовый тест на запись и чтение URLParams.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetChart() throws IOException, GeneralServerException,
			SAXException {
		Map<String, List<String>> params = generateTestURLParams(USERDATA_ID);

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
		assertEquals(USERDATA_ID, AppInfoSingleton.getAppInfo().getCurrentUserDataId());
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
				doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.USERNAME_TAG)
						.getLength());

		assertEquals(1,
				doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.URL_PARAMS_TAG)
						.getLength());
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
				doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.USERDATA_TAG)
						.getLength());
		node =
			doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.USERDATA_TAG)
					.item(0);
		assertEquals(USERDATA_ID, node.getTextContent());
	}

	/**
	 * Проверка установки информации о сессии для функции получения инф. панели.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetDP() throws IOException, GeneralServerException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(USERDATA_ID);
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(FAKE_SESSION_ID);
		final int elID = 5;
		Action action = getAction("tree_multilevel.xml", 0, elID);
		action.setSessionContext(params);
		serviceLayer.getDataPanel(action);
		assertEquals(USERDATA_ID, AppInfoSingleton.getAppInfo().getCurrentUserDataId());
		checkTestUrlParams(action.getContext());
		checkTestUrlParams(action.getDataPanelLink().getElementLinks().get(0).getContext());
	}

	/**
	 * Проверка считывания информации о сессии, если userdata не задана.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 * @throws SAXException
	 */
	@Test
	public void testWriteAndReadIfNoURLParams() throws IOException, GeneralServerException,
			SAXException {
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
				doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.USERDATA_TAG)
						.getLength());
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, doc.getDocumentElement()
				.getElementsByTagName(SessionContextGenerator.USERDATA_TAG).item(0).getTextContent());

		assertEquals(0,
				doc.getDocumentElement().getElementsByTagName(SessionContextGenerator.URL_PARAMS_TAG)
						.getLength());

	}

	/**
	 * Проверка установки и чтения текущей userdata.
	 */
	@Test
	public void testCurrentUserdata() {
		AppInfoSingleton.getAppInfo().setCurrentUserDataId(
				new TreeMap<String, ArrayList<String>>());
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, AppInfoSingleton.getAppInfo()
				.getCurrentUserDataId());
		assertNotNull(AppProps.getUserDataCatalog());

		Map<String, ArrayList<String>> params = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> value3 = new ArrayList<String>();
		value3.add(USERDATA_ID);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurrentUserDataId(params);
		assertEquals(USERDATA_ID, AppInfoSingleton.getAppInfo().getCurrentUserDataId());
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
		AppInfoSingleton.getAppInfo().setCurrentUserDataId(params);
	}

}
