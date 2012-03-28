package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.server.AppInitializer;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Тесты класса AppProps.
 */
public final class AppPropsTest extends AbstractTestWithDefaultUserData {

	private static final String GE_KEY_NAME = "ymapsKey";
	private static final String YM_KEY =
		"AMOPgE4BAAAA9Y-BUwMAonjZ5NBRJDj54c-cDVPzQcYlLNAAAAAAAAAAAACPSuKS9WyCiMuXm9An1ZKCx5Pk-A==";

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
		assertNotNull(UserdataUtils.loadUserDataToStream(UserdataUtils.PROPFILENAME));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public void testGetValueByName() {
		UserdataUtils.getGeneralRequiredProp(SecurityParamsFactory.AUTH_SERVER_URL_PARAM);
		UserdataUtils.getRequiredProp(ConnectionFactory.CONNECTION_URL_PARAM);
		assertNotNull(UserdataUtils.getOptionalProp(ConnectionFactory.CONNECTION_URL_PARAM));

		assertEquals("group_icon_default1.png",
				UserdataUtils.getOptionalProp("navigator.def.icon.name", TEST1_USERDATA));
	}

	@Test
	public void testDirExists() {
		checkDir(SettingsFileType.XSLT.getFileDir());
		checkDir(UserdataUtils.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(SettingsFileType.DATAPANEL.getFileDir());
		checkDir(SettingsFileType.NAVIGATOR.getFileDir());
		checkDir(UserdataUtils.SCHEMASDIR);
		checkDir(SettingsFileType.XFORM.getFileDir());

		assertTrue("Папка с XSD схемами не найдена", (new File(AppInfoSingleton.getAppInfo()
				.getWebAppPath() + "/WEB-INF/classes/" + UserdataUtils.SCHEMASDIR)).exists());
	}

	private void checkDir(final String dirName) {
		File dir = new File(UserdataUtils.getUserDataCatalog() + File.separator + dirName);
		assertTrue(dir.exists());
	}

	/**
	 * Проверка чтения информации о главном окне из app.properties.
	 */
	@Test
	public void testReadMainPageInfo() {
		assertEquals("100px", UserdataUtils.getOptionalProp(
				UserdataUtils.HEADER_HEIGHT_PROP, TEST1_USERDATA));
		assertEquals("50px", UserdataUtils.getOptionalProp(
				UserdataUtils.FOOTER_HEIGHT_PROP, TEST1_USERDATA));
		assertNull(UserdataUtils.getOptionalProp(UserdataUtils.HEADER_HEIGHT_PROP,
				TEST2_USERDATA));
		assertNull(UserdataUtils.getOptionalProp(UserdataUtils.FOOTER_HEIGHT_PROP,
				TEST2_USERDATA));
	}

	@Test(expected = NoSuchUserDataException.class)
	public void testAppPropsExists() {
		UserdataUtils.checkAppPropsExists("test33");
	}

	@Test(expected = SettingsFileOpenException.class)
	public void testCheckUserdatas() {
		try {
			AppInfoSingleton.getAppInfo().getUserdatas().put("test34", new UserData("c:\\"));
			UserdataUtils.checkUserdatas();
		} finally {
			AppInfoSingleton.getAppInfo().getUserdatas().clear();
			AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		}
	}

	@Test
	public void testGeoMapKeys() {
		assertEquals(YM_KEY, UserdataUtils.getGeoMapKey(GE_KEY_NAME, "localhost"));
		assertEquals(YM_KEY, UserdataUtils.getGeoMapKey(GE_KEY_NAME, "127.0.0.1"));
		assertEquals(YM_KEY, UserdataUtils.getGeoMapKey(GE_KEY_NAME, "mail.ru"));
		assertEquals("", UserdataUtils.getGeoMapKey("", "localhost"));
	}

}
