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
import ru.curs.showcase.app.api.event.CompositeContext;
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
	static final String VALUE12 = "value1";
	static final String KEY1 = "key1";
	static final String FAKE_SESSION_ID = "fake-session-id";
	static final String USERDATA_ID = "test1";
	static final String NOT_EXIST_USERDATA_ID = "test123";

	/**
	 * Базовый тест на запись и чтение URLParams.
	 * 
	 * @throws IOException
	 * @throws GeneralServerException
	 * @throws SAXException
	 */
	@Test
	public void testWriteAndRead() throws IOException, GeneralServerException, SAXException {

		Map<String, List<String>> params = new TreeMap<String, List<String>>();
		ArrayList<String> value1 = new ArrayList<String>();
		value1.add(VALUE12);
		params.put(KEY1, value1);
		ArrayList<String> value2 = new ArrayList<String>();
		value2.add("value21");
		value2.add("value22");
		params.put("key2", value2);
		ArrayList<String> value3 = new ArrayList<String>();
		value3.add(USERDATA_ID);
		params.put(AppProps.URL_PARAM_USERDATA, value3);

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

		String sessionContext = context.getSession();
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = db.parse(new InputSource(new StringReader(sessionContext)));

		assertEquals(1,
				doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.USERNAME_TAG)
						.getLength());

		assertEquals(1,
				doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.URL_PARAMS_TAG)
						.getLength());
		Node node =
			doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.URL_PARAMS_TAG)
					.item(0);
		assertEquals(SessionInfoGenerator.URL_PARAM_TAG, node.getChildNodes().item(1)
				.getNodeName());
		assertEquals(2, node.getChildNodes().item(1).getAttributes().getLength());
		assertEquals(KEY1, node.getChildNodes().item(1).getAttributes().getNamedItem(NAME_TAG)
				.getNodeValue());
		assertEquals("[" + VALUE12 + "]", node.getChildNodes().item(1).getAttributes()
				.getNamedItem(VALUE_TAG).getNodeValue());

		assertEquals(1,
				doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.USERDATA_TAG)
						.getLength());
		node =
			doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.USERDATA_TAG)
					.item(0);
		assertEquals(USERDATA_ID, node.getTextContent());

		assertEquals(AUTH_VIA_AUTH_SERVER, AppInfoSingleton.getAppInfo()
				.getAuthViaAuthServerForSession(FAKE_SESSION_ID));
		assertEquals(TEMP_PASS, AppInfoSingleton.getAppInfo()
				.getAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID));

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
				doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.USERDATA_TAG)
						.getLength());
		assertEquals(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT, doc.getDocumentElement()
				.getElementsByTagName(SessionInfoGenerator.USERDATA_TAG).item(0).getTextContent());

		assertEquals(0,
				doc.getDocumentElement().getElementsByTagName(SessionInfoGenerator.URL_PARAMS_TAG)
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
		params.put(AppProps.URL_PARAM_USERDATA, value3);
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
		params.put(AppProps.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurrentUserDataId(params);
	}

}
