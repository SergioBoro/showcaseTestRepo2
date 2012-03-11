package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import net.sf.ehcache.CacheManager;

import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.chart.ChartGetCommand;
import ru.curs.showcase.core.command.ServerStateGetCommand;
import ru.curs.showcase.core.event.ExecServerActivityCommand;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.core.jython.JythonQuery;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.exception.SettingsFileOpenException;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;
import ch.qos.logback.classic.Level;

/**
 * Тесты на получение информации о сессии пользователя.
 * 
 * @author den
 * 
 */
public class RuntimeTest extends AbstractTest {
	private static final String LEVEL_LOG_EVENT_PROP = "level";
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
	 * Простой тест на запуск функций из SessionUtils.
	 */
	@Test
	public void testSessionUtilsFunctions() {
		assertEquals("", SessionUtils.getCurrentSessionUserName());
		assertEquals(SessionUtils.TEST_SESSION, SessionUtils.getCurrentSessionId());
		assertEquals(SessionUtils.TEST_SID, SessionUtils.getCurrentUserSID());
	}

	/**
	 * Проверка того, что значение getCurrentUserDataId сразу после запуска
	 * равно null.
	 */
	@Test
	public void testInitialCurUserDataIdValue() {
		setDefaultUserData();
		assertEquals(ExchangeConstants.DEFAULT_USERDATA, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
	}

	/**
	 * Базовый тест на запись и чтение URLParams.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetChart() throws IOException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);

		AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(FAKE_SESSION_ID,
				AUTH_VIA_AUTH_SERVER);
		AppInfoSingleton.getAppInfo().setAuthServerCrossAppPasswordForSession(FAKE_SESSION_ID,
				TEMP_PASS);

		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		command.execute();

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

		assertEquals(
				1,
				doc.getDocumentElement()
						.getElementsByTagName(XMLSessionContextGenerator.USERNAME_TAG).getLength());
		assertEquals(1,
				doc.getDocumentElement().getElementsByTagName(XMLSessionContextGenerator.SID_TAG)
						.getLength());
		assertEquals(SessionUtils.TEST_SID,
				doc.getDocumentElement().getElementsByTagName(XMLSessionContextGenerator.SID_TAG)
						.item(0).getChildNodes().item(0).getNodeValue());
		assertEquals(
				1,
				doc.getDocumentElement()
						.getElementsByTagName(XMLSessionContextGenerator.URL_PARAMS_TAG)
						.getLength());
		Node node =
			doc.getDocumentElement()
					.getElementsByTagName(XMLSessionContextGenerator.URL_PARAMS_TAG).item(0);
		assertEquals(XMLSessionContextGenerator.URL_PARAM_TAG, node.getChildNodes().item(1)
				.getNodeName());
		assertEquals(2, node.getChildNodes().item(1).getAttributes().getLength());
		assertEquals(KEY1, node.getChildNodes().item(1).getAttributes().getNamedItem(NAME_TAG)
				.getNodeValue());
		assertEquals("[" + VALUE12 + "]", node.getChildNodes().item(1).getAttributes()
				.getNamedItem(VALUE_TAG).getNodeValue());

		assertEquals(
				1,
				doc.getDocumentElement()
						.getElementsByTagName(XMLSessionContextGenerator.USERDATA_TAG).getLength());
		node =
			doc.getDocumentElement().getElementsByTagName(XMLSessionContextGenerator.USERDATA_TAG)
					.item(0);
		assertEquals(TEST1_USERDATA, node.getTextContent());
	}

	/**
	 * Проверка установки информации о сессии для функции получения инф. панели.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testSessionInfoForGetDP() throws IOException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);
		final int elID = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, elID);
		action.setSessionContext(params);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		command.execute();

		assertEquals(TEST1_USERDATA, AppInfoSingleton.getAppInfo().getCurUserDataId());
		checkTestUrlParams(action.getContext());
	}

	/**
	 * Проверка считывания информации о сессии, если userdata не задана.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testWriteAndReadIfNoURLParams() throws IOException, SAXException {
		Map<String, List<String>> params = new TreeMap<>();
		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		command.execute();

		String sessionContext = context.getSession();
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = db.parse(new InputSource(new StringReader(sessionContext)));
		assertEquals(
				"Не создан тэг userdata",
				1,
				doc.getDocumentElement()
						.getElementsByTagName(XMLSessionContextGenerator.USERDATA_TAG).getLength());
		assertEquals(ExchangeConstants.DEFAULT_USERDATA, doc.getDocumentElement()
				.getElementsByTagName(XMLSessionContextGenerator.USERDATA_TAG).item(0)
				.getTextContent());

		assertEquals(
				0,
				doc.getDocumentElement()
						.getElementsByTagName(XMLSessionContextGenerator.URL_PARAMS_TAG)
						.getLength());

	}

	/**
	 * Проверка установки и чтения текущей userdata.
	 */
	@Test
	public void testCurrentUserdata() {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(
				new TreeMap<String, ArrayList<String>>());
		assertEquals(ExchangeConstants.DEFAULT_USERDATA, AppInfoSingleton.getAppInfo()
				.getCurUserDataId());
		assertNotNull(AppProps.getUserDataCatalog());

		Map<String, ArrayList<String>> params = new TreeMap<>();
		ArrayList<String> value3 = new ArrayList<>();
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
		Map<String, ArrayList<String>> params = new TreeMap<>();
		ArrayList<String> value3 = new ArrayList<>();
		value3.add(NOT_EXIST_USERDATA_ID);
		params.put(ExchangeConstants.URL_PARAM_USERDATA, value3);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(params);
	}

	/**
	 * Проверка вызова функции getServerCurrentState.
	 */
	@Test
	public void testGetServerCurrentState() {
		CompositeContext context = new CompositeContext();
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState scs = command.execute();
		checkServerState(scs);
	}

	private void checkServerState(final ServerState scs) {
		assertNotNull(scs);
		assertEquals(AppInfoSingleton.getAppInfo().getServletContainerVersion(),
				scs.getServletContainerVersion());
		assertEquals(System.getProperty("java.version"), scs.getJavaVersion());
	}

	@Test
	public void testRelatedData() throws UnsupportedEncodingException {
		CompositeContext context = CompositeContext.createCurrent();
		getExtGridContext(context);
		String res = XMLSessionContextGenerator.generate(context);
		assertTrue(res.contains("<selectedRecordId>r2</selectedRecordId>"));
		assertTrue(res.contains("<currentColumnId>curColumnId</currentColumnId>"));
		assertTrue(res.contains("size=\"2\""));
		assertTrue(res.contains("<add>value</add>"));
	}

	@Test
	public void testLastLogEventQueue() throws InterruptedException {
		testBaseLastLogEventQueue(AppInfoSingleton.getAppInfo().getLastLogEvents());
	}

	@Test
	public void testLoggingEventDecorator() {
		LoggingEventDecorator decorator = new LoggingEventDecorator(generateTestLoggingEvent());

		decorator.setUserdata(TEST1_USERDATA);
		assertTrue(decorator.isSatisfied(ExchangeConstants.URL_PARAM_USERDATA, "test1"));
		assertFalse(decorator.isSatisfied(ExchangeConstants.URL_PARAM_USERDATA, "default"));

		decorator.setUserName("master");
		assertTrue(decorator.isSatisfied("userName", "master"));
		assertFalse(decorator.isSatisfied("userName", "master1"));

		decorator.setCommandName(XFormDownloadCommand.class.getSimpleName());
		assertTrue(decorator
				.isSatisfied("commandName", XFormDownloadCommand.class.getSimpleName()));
		assertFalse(decorator.isSatisfied("commandName", XFormUploadCommand.class.getSimpleName()));

		decorator.setRequestId("1");
		assertTrue(decorator.isSatisfied("requestId", "1"));
		assertFalse(decorator.isSatisfied("requestId", "2"));

		assertTrue(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "error"));
		assertTrue(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "ERROR"));
		assertFalse(decorator.isSatisfied(LEVEL_LOG_EVENT_PROP, "warn"));

		assertTrue(decorator.isSatisfied("requestid", "1111"));
	}

	@Test
	public void testGetLastLogEventsWithFilter() {
		AppInfoSingleton.getAppInfo().getLastLogEvents().clear();
		LoggingEventDecorator decorator = new LoggingEventDecorator(generateTestLoggingEvent());
		decorator.setUserdata(TEST1_USERDATA);
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		decorator = new LoggingEventDecorator(generateTestLoggingEvent());
		decorator.setUserdata("default");
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		Map<String, List<String>> params = new TreeMap<>();
		params.put(ExchangeConstants.URL_PARAM_USERDATA, Arrays.asList(TEST1_USERDATA));
		Collection<LoggingEventDecorator> selected =
			AppInfoSingleton.getAppInfo().getLastLogEvents(params);

		assertEquals(1, selected.size());
		assertEquals(TEST1_USERDATA, selected.iterator().next().getUserdata());
	}

	@Test
	public void testGetLastLogEventsWithNullFilter() {
		AppInfoSingleton.getAppInfo().getLastLogEvents().clear();
		LoggingEventDecorator decorator = new LoggingEventDecorator(generateTestLoggingEvent());
		decorator.setUserdata(TEST1_USERDATA);
		AppInfoSingleton.getAppInfo().addLogEvent(decorator);

		Map<String, List<String>> params = null;
		Collection<LoggingEventDecorator> selected =
			AppInfoSingleton.getAppInfo().getLastLogEvents(params);

		assertEquals(1, selected.size());
		assertEquals(TEST1_USERDATA, selected.iterator().next().getUserdata());
	}

	@Test
	public void testCommandContext() {
		CommandContext cc = new CommandContext();
		cc.setUserdata(TEST1_USERDATA);
		cc.setUserName(ExchangeConstants.DEFAULT_USERDATA);
		cc.setCommandName(XFormDownloadCommand.class.getSimpleName());
		cc.setRequestId("1");

		AbstractCommandContext clone = cc.gwtClone();
		assertEquals(cc, clone);
		assertNotSame(cc, clone);
		cc.setRequestId("2");
		assertFalse(cc.equals(clone));
		assertEquals(cc.getUserName(), clone.getUserName());

		CommandContext clone2 = new CommandContext();
		clone2.assignNullValues(cc);
		assertEquals(cc, clone2);
		assertNotSame(cc, clone2);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testCache() {
		assertNotNull(CacheManager.create());
		assertTrue(CacheManager.getInstance().cacheExists(AppInfoSingleton.GRID_STATE_CACHE));
		assertEquals(1, CacheManager.getInstance().getCacheNames().length);

		List list =
			CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE).getKeys();
		assertEquals(0, list.size());
		CompositeContext value = new CompositeContext();
		final String key = "key";
		CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE)
				.put(new net.sf.ehcache.Element(key, value));
		list = CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE).getKeys();
		assertEquals(1, list.size());
		assertEquals(key, list.get(0));
		assertEquals(value, CacheManager.getInstance().getCache(AppInfoSingleton.GRID_STATE_CACHE)
				.get(key).getValue());
	}

	@Test
	public void testJythonMessages() {
		Action action = generateActionForSA("TestWriteToLog.py");
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();
		int jythonEvents = 0;
		String expected1 = MAIN_CONDITION + " из jython";
		String expected2 = "из jython 2";
		for (LoggingEventDecorator event : AppInfoSingleton.getAppInfo().getLastLogEvents()) {
			if (JythonQuery.JYTHON_MARKER.equals(event.getProcess())) {
				if (expected1.equals(event.getMessage()) || expected2.equals(event.getMessage())) {
					assertEquals(Level.INFO, event.getLevel());
					assertEquals(ExchangeConstants.DEFAULT_USERDATA, event.getUserdata());
					assertEquals(ExecServerActivityCommand.class.getSimpleName(),
							event.getCommandName());
					jythonEvents++;
				}
			}
		}
		assertEquals(jythonEvents, 2);
	}

	private Action generateActionForSA(final String procName) {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", procName);
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		context.setMain(MAIN_CONDITION);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		return action;
	}

	@Test
	public void testClientState() {
		CompositeContext context = generateContextWithSessionInfo();
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState serverState = command.execute();
		ClientState clientState =
			new ClientState(serverState, "Opera/9.20 (Windows NT 6.0; U; en)");

		checkServerState(clientState.getServerState());
		assertEquals(BrowserType.OPERA, clientState.getBrowserType());
		assertEquals(BrowserType.VERSION_NOT_DEFINED, clientState.getBrowserVersion());

		clientState =
			new ClientState(serverState,
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
		assertEquals("2.0.0.6", clientState.getBrowserVersion());
	}

	@Test
	public void testExecutedProc() {
		AppInfoSingleton.getAppInfo().getExecutedProc().clear();
		final String procName = "activity_for_test";
		assertFalse(AppInfoSingleton.getAppInfo().getExecutedProc().contains(procName));

		Action action = generateActionForSA(procName);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();

		assertTrue(AppInfoSingleton.getAppInfo().getExecutedProc().contains(procName));
	}

	/**
	 * Проверка работы построителя ServerCurrentState.
	 * 
	 * @see ru.curs.showcase.app.api.ServerState ServerCurrentState
	 * @see ru.curs.showcase.runtime.ServerStateFactory
	 *      ServerCurrentStateBuilder
	 */
	@Test
	public void serverStateFactoryShouldReturnCorrectState() throws SQLException {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		ServerState state = ServerStateFactory.build("fake");
		assertNotNull(state);
		assertNotNull(state.getAppVersion());
		assertTrue(state.getAppVersion().endsWith("development"));
		assertNotNull(state.getJavaVersion());
		assertNotNull(state.getServerTime());
		assertNotNull(state.getSqlVersion());
		assertNotNull(state.getDojoVersion());

		assertEquals("10.0.0.9999", ServerStateFactory.getAppVersion("ru/curs/showcase/test/"));
	}

	@Test
	public void serverStateFactoryShouldRaiseExceptionWhenVersionFileAbsent() {
		try {
			ServerStateFactory.getAppVersion("ru/curs/showcase/test/util/");
			fail();
		} catch (SettingsFileOpenException e) {
			assertTrue(e.getLocalizedMessage().contains(
					"ru/curs/showcase/test/util/version.properties"));
		}
	}

	@Test
	public void serverStateFactoryShouldRaiseExceptionWhenBuildFileAbsent() {
		try {
			ServerStateFactory.getAppVersion("ru/curs/showcase/test/runtime/");
			fail();
		} catch (SettingsFileOpenException e) {
			assertTrue(e.getLocalizedMessage().contains("ru/curs/showcase/test/runtime/build"));
		}
	}
}