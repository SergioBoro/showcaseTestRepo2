package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.SQLException;

import org.junit.Test;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.model.datapanel.DataPanelXMLGateway;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Общий тестовый класс для мелких базовых объектов.
 * 
 * @author den
 * 
 */
public class BaseObjectsTest extends AbstractTestBasedOnFiles {
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
					DataPanelXMLGateway.DP_STORAGE_PARAM_NAME, "a.xml"));

		StreamConvertor dup = new StreamConvertor(is);
		String data = XMLUtils.xsltTransform(dup.getCopy(), null);
		checkForDP(data);

		data = XMLUtils.xsltTransform(dup.getCopy(), null);
		checkForDP(data);

		ByteArrayOutputStream outStream = dup.getOutputStream();
		checkForDPWithXMLHeader(outStream);

		data = XMLUtils.xsltTransform(StreamConvertor.outputToInputStream(outStream), null);
		checkForDP(data);

		outStream = StreamConvertor.inputToOutputStream(dup.getCopy());
		checkForDPWithXMLHeader(outStream);
	}

	private void checkForDPWithXMLHeader(final ByteArrayOutputStream outStream)
			throws UnsupportedEncodingException {
		String data;
		data = outStream.toString("UTF-8");
		assertTrue(data.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
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
	 * @see ru.curs.showcase.util.ServerCurrentState ServerCurrentState
	 * @see ru.curs.showcase.app.server.ServerCurrentStateBuilder
	 *      ServerCurrentStateBuilder
	 */
	@Test
	public void testServerCurrentStateBuilder() throws IOException, SQLException {
		ServerCurrentState state = ServerCurrentStateBuilder.build("fake");
		assertNotNull(state);
		assertNotNull(state.getAppVersion());
		assertNotNull(state.getJavaVersion());
		assertNotNull(state.getServerTime());
		assertNotNull(state.getSqlVersion());
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
		String sourceDir = "userdata\\css";
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
		XMLSource source = new XMLSource(AppProps.loadResToStream("log4j.xml"), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNull(source.getSaxParser());

		source =
			new XMLSource(AppProps.loadResToStream("log4j.xml"), XMLUtils.createSAXParser(), "");
		assertNotNull(source.getInputStream());
		assertNull(source.getDocument());
		assertNotNull(source.getSaxParser());

		source = new XMLSource(XMLUtils.createEmptyDoc("test"), "test", "");
		assertNull(source.getInputStream());
		assertNotNull(source.getDocument());
		assertNull(source.getSaxParser());
	}

	/**
	 * Проверка функции детектирования браузера.
	 */
	@Test
	public void testBrowserTypeDetection() {
		// CHECKSTYLE:OFF
		final String safariUA =
			"mozilla/5.0 (windows; u; windows nt 6.1; ru-ru) applewebkit/533.20.25 (khtml, like gecko) version/5.0.4 safari/533.20.27";
		final String chromeUA =
			"mozilla/5.0 (windows nt 6.1; wow64) applewebkit/534.24 (khtml, like gecko) chrome/11.0.696.71 safari/534.24";
		final String operaUA = "opera/9.80 (windows nt 6.1; u; ru) presto/2.8.131 version/11.11";
		final String firefoxUA =
			"mozilla/5.0 (windows nt 6.1; wow64; rv:2.0.1) gecko/20100101 firefox/4.0.1";
		final String IEUA =
			"mozilla/5.0 (compatible; msie 9.0; windows nt 6.1; wow64; trident/5.0; slcc2; .net clr 2.0.50727; .net clr 3.5.30729; .net clr 3.0.30729; media center pc 6.0; .net4.0c)";
		// CHECKSTYLE:ON

		assertEquals(BrowserType.SAFARI, BrowserType.detect(safariUA));
		assertEquals(BrowserType.CHROME, BrowserType.detect(chromeUA));
		assertEquals(BrowserType.OPERA, BrowserType.detect(operaUA));
		assertEquals(BrowserType.FIREFOX, BrowserType.detect(firefoxUA));
		assertEquals(BrowserType.IE, BrowserType.detect(IEUA));

		assertEquals("5.0.4", BrowserType.detectVersion(safariUA));
		assertEquals("11.0.696.71", BrowserType.detectVersion(chromeUA));
		assertEquals("11.11", BrowserType.detectVersion(operaUA));
		assertEquals("4.0.1", BrowserType.detectVersion(firefoxUA));
		assertEquals("9.0", BrowserType.detectVersion(IEUA));
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
}
