package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.util.AppProps;

/**
 * Тесты для получения фреймов из файла и из БД.
 * 
 * @author den
 * 
 */
public final class FramesTest extends AbstractTestBasedOnFiles {
	static final String HEADER_CODE = "<h1 align=\"center\">Заголовок из БД</h1>";
	static final String WELCOME_CODE = "<h1 align=\"center\">Компания КУРС представляет</h1>";
	static final String USERDATA_TEST1 = "test1";
	static final String FOOTER_CODE = "<h1 align=\"center\">Подвал из БД для " + USERDATA_TEST1
			+ "</h1>";

	/**
	 * Тест получения файла фрейма.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetFramesInMainPage() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		MainPage page = sl.getMainPage(context);

		assertEquals(HEADER_CODE, page.getHeader());
		assertEquals(FOOTER_CODE, page.getFooter());
		assertTrue(page.getWelcome().endsWith(WELCOME_CODE));

		MainPageFrameSelector selector = new MainPageFrameSelector(MainPageFrameType.WELCOME);
		MainPageFrameGateway gateway = selector.getGateway();
		String raw = gateway.get(context, selector.getSourceName());
		assertTrue(raw.indexOf(AppProps.CURRENT_USERDATA_TEMPLATE) > -1);
		assertTrue(raw.indexOf(AppProps.IMAGES_IN_GRID_DIR) > -1);

		assertTrue(page.getWelcome().indexOf(AppProps.CURRENT_USERDATA_TEMPLATE) == -1);
		assertTrue(page.getWelcome().indexOf(AppProps.IMAGES_IN_GRID_DIR) == -1);
		assertTrue(page.getWelcome().indexOf(AppInfoSingleton.getAppInfo().getCurUserDataId()) > -1);
		assertTrue(page.getWelcome().indexOf(
				AppProps.getRequiredValueByName(AppProps.IMAGES_IN_GRID_DIR)) > -1);
	}

	/**
	 * Тест получения файла фрейма.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetFileFrameWelcomeAsFrame() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.WELCOME);
		assertTrue(html.endsWith(WELCOME_CODE + "</body></html>"));
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetDBFrameHeaderAsFrame() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.HEADER);
		assertEquals("<html><head/><body>" + HEADER_CODE + "</body></html>", html);
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetDBFrameFooterAsFrame() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.FOOTER);
		assertEquals("<html><head/><body>" + FOOTER_CODE + "</body></html>", html);
	}
}
