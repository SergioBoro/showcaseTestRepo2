package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.frame.MainPageFrameType;

/**
 * Тесты для получения фреймов из файла и из БД.
 * 
 * @author den
 * 
 */
public final class FramesTest extends AbstractTestBasedOnFiles {
	private static final String HTML_HEAD_BODY_BEGIN = "<html><head/><body>";
	private static final String BODY_HTML_ENDS = "</body></html>";
	private static final String HEADER_CODE = "<h1 align=\"center\">Заголовок из БД</h1>";
	private static final String WELCOME_CODE =
		"<h1 align=\"center\">Компания КУРС представляет</h1>";
	private static final String FOOTER_CODE = "<h1 align=\"center\">Подвал из БД для "
			+ TEST1_USERDATA + "</h1>";

	/**
	 * Тест получения файла фрейма.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetFramesInMainPage() throws GeneralException {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		MainPage page = sl.getMainPage(context);

		assertEquals(HEADER_CODE, page.getHeader());
		assertEquals(FOOTER_CODE, page.getFooter());
		assertTrue(page.getWelcome().endsWith(WELCOME_CODE));
	}

	/**
	 * Тест получения файла фрейма.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetFileFrameWelcomeAsFrame() throws GeneralException {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.WELCOME);
		assertTrue(html.endsWith(WELCOME_CODE + BODY_HTML_ENDS));
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetDBFrameHeaderAsFrame() throws GeneralException {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.HEADER);
		assertEquals(HTML_HEAD_BODY_BEGIN + HEADER_CODE + BODY_HTML_ENDS, html);
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testGetDBFrameFooterAsFrame() throws GeneralException {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.FOOTER);
		assertEquals(HTML_HEAD_BODY_BEGIN + FOOTER_CODE + BODY_HTML_ENDS, html);
	}
}
