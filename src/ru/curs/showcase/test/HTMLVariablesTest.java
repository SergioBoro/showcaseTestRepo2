package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.AppProps;

/**
 * Специальный тестовый класс для проверки замены переменных в HTML блоках в
 * Showcase. TODO - доделать.
 * 
 * @author den
 * 
 */
public class HTMLVariablesTest extends AbstractTestBasedOnFiles {

	/**
	 * Проверка переменных в коде фреймов главной страницы.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testFramesVariables() throws GeneralException {
		CompositeContext context = new CompositeContext(generateTestURLParams(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		MainPage page = sl.getMainPage(context);

		MainPageFrameSelector selector = new MainPageFrameSelector(MainPageFrameType.WELCOME);
		MainPageFrameGateway gateway = selector.getGateway();
		String raw = gateway.getRawData(context, selector.getSourceName());
		assertTrue(raw.indexOf(AppProps.CURRENT_USERDATA_TEMPLATE) > -1);
		assertTrue(raw.indexOf(AppProps.IMAGES_IN_GRID_DIR) > -1);

		assertEquals(-1, page.getWelcome().indexOf(AppProps.CURRENT_USERDATA_TEMPLATE));
		assertEquals(-1, page.getWelcome().indexOf(AppProps.IMAGES_IN_GRID_DIR));
		assertTrue(page.getWelcome().indexOf(AppInfoSingleton.getAppInfo().getCurUserDataId()) > -1);
		assertTrue(page.getWelcome().indexOf(
				AppProps.getRequiredValueByName(AppProps.IMAGES_IN_GRID_DIR)) > -1);
	}

	/**
	 * Переменные в XForms + подстановка userdata.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testXFormsVariables() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", "0205");

		XFormsGateway gateway = new XFormsFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		XFormsFactory factory = new XFormsFactory(raw);
		XForms xforms = factory.build();
		assertTrue(xforms.getXFormParts().get(2).indexOf("?userdata=default") > -1);
	}
}
