package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Тесты класса AppProps.
 */
public final class AppPropsTest extends AbstractTestWithDefaultUserData {

	private static final String GE_KEY_NAME = "geKey";
	private static final String GE_KEY =
		"ABQIAAAA-DMAtggvLwlIYlUJiASaAxRQnCpeV9jusWIeBw0POFqU6SItGxRWZhddpS8pIkVUd2fDQhzwPUWmMA";

	/**
	 * Тест ф-ции loadResToStream.
	 */
	@Test
	public void testLoadResToStream() {
		assertNotNull(FileUtils.loadResToStream(FileUtils.GENERAL_PROPERTIES));
	}

	/**
	 * Тест ф-ции loadUserDataToStream и получение пути к каталогу с
	 * пользовательскими данными.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadUserDataToStream() throws IOException {
		assertNotNull(AppProps.loadUserDataToStream(AppProps.PROPFILENAME));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public void testGetValueByName() {
		AppProps.getRequiredValueByName(SecurityParamsFactory.AUTH_SERVER_URL_PARAM);
		AppProps.getRequiredValueByName(ConnectionFactory.CONNECTION_URL_PARAM);
		assertNotNull(AppProps.getOptionalValueByName(ConnectionFactory.CONNECTION_URL_PARAM));

		assertEquals("group_icon_default1.png",
				AppProps.getOptionalValueByName("navigator.def.icon.name", TEST1_USERDATA));
	}

	@Test
	public void testDirExists() {
		checkDir(SettingsFileType.XSLT.getFileDir());
		checkDir(AppProps.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(SettingsFileType.DATAPANEL.getFileDir());
		checkDir(SettingsFileType.NAVIGATOR.getFileDir());
		checkDir(AppProps.SCHEMASDIR);
		checkDir(SettingsFileType.XFORM.getFileDir());

		assertTrue("Папка с XSD схемами не найдена", (new File(AppInfoSingleton.getAppInfo()
				.getWebAppPath() + "/WEB-INF/classes/" + AppProps.SCHEMASDIR)).exists());
	}

	private void checkDir(final String dirName) {
		File dir = new File(AppProps.getUserDataCatalog() + File.separator + dirName);
		assertTrue(dir.exists());
	}

	/**
	 * Проверка чтения информации о главном окне из app.properties.
	 */
	@Test
	public void testReadMainPageInfo() {
		assertEquals("100px",
				AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP, TEST1_USERDATA));
		assertEquals("50px",
				AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP, TEST1_USERDATA));
		assertNull(AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP, TEST2_USERDATA));
		assertNull(AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP, TEST2_USERDATA));
	}

	@Test(expected = NoSuchUserDataException.class)
	public void testAppPropsExists() {
		AppProps.checkAppPropsExists("test33");
	}

	@Test(expected = SettingsFileOpenException.class)
	public void testCheckUserdatas() {
		AppInfoSingleton.getAppInfo().getUserdatas().put("test34", new UserData("c:\\"));
		AppProps.checkUserdatas();
	}

	@Test
	public void testGeoMapKeys() {
		assertEquals(GE_KEY, AppProps.getGeoMapKey(GE_KEY_NAME, "localhost"));
		assertEquals(GE_KEY, AppProps.getGeoMapKey(GE_KEY_NAME, "127.0.0.1"));
		assertEquals(GE_KEY, AppProps.getGeoMapKey(GE_KEY_NAME, "mail.ru"));
		assertEquals("", AppProps.getGeoMapKey("", "localhost"));
	}

}
