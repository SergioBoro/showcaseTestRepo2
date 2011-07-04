package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.datapanel.DataPanelXMLGateway;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.*;

/**
 * Тесты класса AppProps.
 */
public final class AppPropsTest extends AbstractTestBasedOnFiles {

	/**
	 * Тест ф-ции loadResToStream.
	 */
	@Test
	public void testLoadResToStream() {
		assertNotNull(AppProps.loadResToStream(AppProps.PATH_PROPERTIES));
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
				AppProps.getOptionalValueByName("navigator.def.icon.name", "test1"));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public void testDirExists() {
		checkDir(AppProps.XSLTTRANSFORMSDIR);
		checkDir(AppProps.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(DataPanelXMLGateway.DP_STORAGE_PARAM_NAME);
		checkDir(NAVIGATORSTORAGE);
		checkDir(AppProps.SCHEMASDIR);
		checkDir(AppProps.XFORMS_DIR);

		assertTrue((new File(AppProps.getResURL(AppProps.SCHEMASDIR).getFile())).exists());
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
				AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP, "test1"));
		assertEquals("50px", AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP, "test1"));
		assertNull(AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP, "test2"));
		assertNull(AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP, "test2"));
	}

	/**
	 * Проверка чтения информации о главном окне из app.properties.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testReadMainPageInfoBySL() throws GeneralServerException {
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		CompositeContext context = new CompositeContext(generateTestURLParams("test1"));
		MainPage mp = sl.getMainPage(context);
		assertEquals("100px", mp.getHeaderHeight());
		assertEquals("50px", mp.getFooterHeight());

		context = new CompositeContext(generateTestURLParams("test2"));
		mp = sl.getMainPage(context);
		assertEquals(AppProps.DEF_HEADER_HEIGTH, mp.getHeaderHeight());
		assertEquals(AppProps.DEF_FOOTER_HEIGTH, mp.getFooterHeight());
	}
}
