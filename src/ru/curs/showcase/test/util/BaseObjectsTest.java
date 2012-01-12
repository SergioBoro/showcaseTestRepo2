package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import org.junit.Test;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.server.ProductionModeInitializer;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * Общий тестовый класс для мелких базовых объектов.
 * 
 * @author den
 * 
 */
public class BaseObjectsTest extends AbstractTestWithDefaultUserData {

	private static final String TEST_CSS = "ru\\curs\\showcase\\test\\ShowcaseDataGrid_test.css";

	/**
	 * Проверка работы StreamConvertor.
	 * 
	 * @throws IOException
	 * @see ru.curs.showcase.util.StreamConvertor StreamConvertor
	 */
	@Test
	public void testStreamConvertor() throws IOException {
		InputStream is =
			AppProps.loadUserDataToStream(String.format("%s//%s",
					SettingsFileType.DATAPANEL.getFileDir(), "a.xml"));

		StreamConvertor dup = new StreamConvertor(is);
		String data = XMLUtils.streamToString(dup.getCopy());
		checkForDP(data);

		data = XMLUtils.streamToString(dup.getCopy());
		checkForDP(data);

		ByteArrayOutputStream outStream = dup.getOutputStream();
		checkForDPWithXMLHeader(outStream);

		data = XMLUtils.streamToString(StreamConvertor.outputToInputStream(outStream));
		checkForDP(data);

		outStream = StreamConvertor.inputToOutputStream(dup.getCopy());
		checkForDPWithXMLHeader(outStream);
	}

	private void checkForDPWithXMLHeader(final ByteArrayOutputStream outStream)
			throws UnsupportedEncodingException {
		String data;
		data = outStream.toString(TextUtils.DEF_ENCODING);
		assertTrue(data.startsWith(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8));
		assertTrue(data.endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	private void checkForDP(final String data) {
		assertTrue(data.startsWith("<" + GeneralXMLHelper.DP_TAG));
		assertTrue(data.endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	/**
	 * Проверка работы построителя ServerCurrentState.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 * @see ru.curs.showcase.util.ServerState ServerCurrentState
	 * @see ru.curs.showcase.runtime.ServerStateFactory
	 *      ServerCurrentStateBuilder
	 */
	@Test
	public void testServerCurrentStateBuilder() throws IOException, SQLException {
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

	/**
	 * Проверка считывания из CSS ".webmain-SmartGrid .headerGap".
	 * 
	 * @see ru.curs.showcase.util.CSSPropReader CSSPropReader
	 */
	@Test
	public void testGridColumnGapRead() {
		CSSPropReader reader = new CSSPropReader();
		String width =
			reader.read(TEST_CSS, ProductionModeInitializer.HEADER_GAP_SELECTOR,
					ProductionModeInitializer.WIDTH_PROP);
		assertNotNull(width);
	}

	/**
	 * Проверка работы BatchFileProcessor.
	 * 
	 * @see ru.curs.showcase.util.BatchFileProcessor BatchFileProcessor
	 * @see ru.curs.showcase.util.FileUtils#deleteDir FileUtils.deleteDir
	 * @throws IOException
	 */
	@Test
	public void testBatchFileProcessorAndDeleteDir() throws IOException {
		String sourceDir = "userdatas\\default\\css";
		String destDir = "tmp\\css";

		File dir = new File(destDir);
		FileUtils.deleteDir(destDir);
		assertFalse(dir.exists());

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(sourceDir, new RegexFilenameFilter("^[.].*", false));
		fprocessor.process(new CopyFileAction(destDir));

		assertTrue(dir.exists());
		dir = new File(destDir + "\\level2");
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
		File file = new File(destDir + "\\level2\\test.css");
		assertTrue(file.exists());
		assertTrue(file.isFile());

		FileUtils.deleteDir(destDir);
	}

	/**
	 * Проверка создания XMLSource.
	 */
	@Test
	public void testXMLSourceCreate() {
		XMLSource source = new XMLSource(FileUtils.loadResToStream(TEST_XML_FILE), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNull(source.getSaxParser());

		source =
			new XMLSource(FileUtils.loadResToStream(TEST_XML_FILE), XMLUtils.createSAXParser(), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNotNull(source.getSaxParser());

		source = new XMLSource(XMLUtils.createEmptyDoc("testTag"), "testFile", "");
		assertNull(source.getInputStream());
		assertNotNull(source.getDocument());
		assertNull(source.getSaxParser());
	}

	/**
	 * Проверка функции детектирования браузера.
	 */
	@Test
	public void testBrowserTypeDetection() {
		final String safariUA =
			"mozilla/5.0 (windows; u; windows nt 6.1; ru-ru) applewebkit/533.20.25 (khtml, like gecko) "
					+ "version/5.0.4 safari/533.20.27";
		final String chromeUA =
			"mozilla/5.0 (windows nt 6.1; wow64) applewebkit/534.24 (khtml, like gecko) chrome/11.0.696.71 safari/534.24";
		final String operaUA = "opera/9.80 (windows nt 6.1; u; ru) presto/2.8.131 version/11.11";
		final String firefoxUA =
			"mozilla/5.0 (windows nt 6.1; wow64; rv:2.0.1) gecko/20100101 firefox/4.0.1";
		final String ieUA =
			"mozilla/5.0 (compatible; MSIE 9.0; windows nt 6.1; wow64; trident/5.0)";

		assertEquals(BrowserType.SAFARI, BrowserType.detect(safariUA));
		assertEquals(BrowserType.CHROME, BrowserType.detect(chromeUA));
		assertEquals(BrowserType.OPERA, BrowserType.detect(operaUA));
		assertEquals(BrowserType.FIREFOX, BrowserType.detect(firefoxUA));
		assertEquals(BrowserType.IE, BrowserType.detect(ieUA));

		assertEquals("5.0.4", BrowserType.detectVersion(safariUA));
		assertEquals("11.0.696.71", BrowserType.detectVersion(chromeUA));
		assertEquals("11.11", BrowserType.detectVersion(operaUA));
		assertEquals("4.0.1", BrowserType.detectVersion(firefoxUA));
		assertEquals("9.0", BrowserType.detectVersion(ieUA));
	}

	@Test
	public void testLastLogEventQueue() throws InterruptedException {
		testBaseLastLogEventQueue(new LastLogEvents());
	}

	@Test
	public void testLastLogEventQueueDuplicates() {
		LastLogEvents lle = new LastLogEvents();
		LoggingEvent original = generateTestLoggingEvent();
		long timestamp = original.getTimeStamp();
		lle.add(new LoggingEventDecorator(original));
		original = generateTestLoggingEvent();
		original.setTimeStamp(timestamp);
		lle.add(new LoggingEventDecorator(original));
		original = generateTestLoggingEvent();
		original.setTimeStamp(timestamp);
		lle.add(new LoggingEventDecorator(original));

		final int itemsCount = 3;
		assertEquals(itemsCount, lle.size());
	}

	@Test
	public void testTextDataFile() {
		DataFile<InputStream> file = new DataFile<InputStream>(null, "test.txt");
		assertTrue(file.isTextFile());
		file = new DataFile<InputStream>(null, "test.exe");
		assertFalse(file.isTextFile());
	}

	@Test
	public void testAddParamsToSQLTemplate() {
		Map<Integer, Object> params = new TreeMap<>();
		params.put(1, "first\n");
		params.put(2, 2);
		String value = "{call test_proc(?,?,?)}";

		value = SQLUtils.addParamsToSQLTemplate(value, params);

		assertEquals("test_proc 'first\n',2,'null'", value);
	}

	@Test
	public void testLogSettings() {
		final int logSize =
			Integer.parseInt(FileUtils.getGeneralOptionalParam(LastLogEvents.INTERNAL_LOG_SIZE));
		assertEquals(logSize, LastLogEvents.getMaxRecords());
	}
}
