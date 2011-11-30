package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.model.datapanel.DataPanelFileGateway;
import ru.curs.showcase.model.navigator.NavigatorFileGateway;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Тесты класса AppProps.
 */
public final class AppPropsTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест ф-ции loadResToStream.
	 */
	@Test
	public void testLoadResToStream() {
		assertNotNull(FileUtils.loadResToStream(FileUtils.PATH_PROPERTIES));
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

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public void testDirExists() {
		checkDir(SettingsFileType.XSLT.getFileDir());
		checkDir(AppProps.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(DataPanelFileGateway.DP_STORAGE_PARAM_NAME);
		checkDir(NavigatorFileGateway.NAVIGATORSTORAGE);
		checkDir(AppProps.SCHEMASDIR);
		checkDir(SettingsFileType.XFORM.getFileDir());

		assertTrue((new File(FileUtils.getResURL(AppProps.SCHEMASDIR).getFile())).exists());
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
}
