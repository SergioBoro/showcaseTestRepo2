package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.frame.MainPageFrameType;

/**
 * Тесты для получения фреймов из файла и из БД.
 * 
 * @author den
 * 
 */
public final class FramesTest extends AbstractTestBasedOnFiles {
	static final String USERDATA_TEST1 = "test1";

	/**
	 * Тест получения файла фрейма.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetFileFrameWelcome() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.WELCOME);
		assertTrue(html.endsWith("<h1 align=\"center\">Компания КУРС представляет</h1>"));
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetDBFrameHeader() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.HEADER);
		assertEquals("<h1 align=\"center\">Заголовок из БД</h1>", html);
	}

	/**
	 * Тест получения кода фрейма из БД.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testGetDBFrameFooter() throws GeneralServerException {
		CompositeContext context = new CompositeContext(generateTestURLParams(USERDATA_TEST1));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		String html = sl.getMainPageFrame(context, MainPageFrameType.FOOTER);
		assertEquals("<h1 align=\"center\">Подвал из БД для " + USERDATA_TEST1 + "</h1>", html);
	}
}
