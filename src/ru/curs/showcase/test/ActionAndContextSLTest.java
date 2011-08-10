package ru.curs.showcase.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextSLTest extends AbstractTest {

	/**
	 * Проверка выполнения действия на сервере.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testServerActivityExec() throws GeneralException {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		sl.execServerAction(action);
		assertNotNull(action.getServerActivities().get(0).getContext().getSession());
	}

	/**
	 * Проверка выполнения действия на сервере, приводящего к ошибке.
	 * 
	 * @throws GeneralException
	 */
	@Test(expected = GeneralException.class)
	public void testServerActivityExecFail() throws GeneralException {
		final int actionNumber = 2;
		AppInfoSingleton.getAppInfo().setCurUserDataId("test1");
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		action.getContext().setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		sl.execServerAction(action);
	}
}
